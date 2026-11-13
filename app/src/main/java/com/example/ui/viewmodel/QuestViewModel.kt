package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitPulseRepository
import com.example.data.quests.BrainPulseDatabase
import com.example.data.quests.ClaimQuestResult
import com.example.data.quests.QuestCategory
import com.example.data.quests.QuestEntity
import com.example.data.quests.QuestFrequency
import com.example.data.quests.QuestRepository
import com.example.model.HabitPulseEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class QuestTab {
    DAILY,
    WEEKLY
}

data class QuestUiState(
    val selectedTab: QuestTab = QuestTab.DAILY,
    val showFullCatalog: Boolean = false,
    val selectedCategory: QuestCategory? = null,
    val activeDailyQuests: List<QuestEntity> = emptyList(),
    val activeWeeklyQuests: List<QuestEntity> = emptyList(),
    val allDailyQuests: List<QuestEntity> = emptyList(),
    val allWeeklyQuests: List<QuestEntity> = emptyList(),
    val totalXp: Long = 0L,
    val brainLevel: Int = 1,
    val levelProgress: Float = 0f,
    val totalCatalogCount: Int = 300,
    val totalClaimedCount: Int = 0,
    val isLoading: Boolean = true,
    val statusBannerMessage: String? = null,
    val isArabic: Boolean = false
) {
    val displayedQuests: List<QuestEntity>
        get() {
            val base = when {
                selectedTab == QuestTab.DAILY && !showFullCatalog -> activeDailyQuests
                selectedTab == QuestTab.DAILY && showFullCatalog -> allDailyQuests
                selectedTab == QuestTab.WEEKLY && !showFullCatalog -> activeWeeklyQuests
                else -> allWeeklyQuests
            }
            val filter = selectedCategory
            return if (filter == null || filter.frequency != if (selectedTab == QuestTab.DAILY) QuestFrequency.DAILY else QuestFrequency.WEEKLY) {
                base
            } else {
                base.filter { it.category == filter }
            }
        }
}

class QuestViewModel(application: Application) : AndroidViewModel(application) {

    private val habitPulseRepository = HabitPulseRepository(application)
    private val questDao = BrainPulseDatabase.getInstance(application).questDao()
    private val questRepository = QuestRepository(questDao, habitPulseRepository)

    private val _controlsState = MutableStateFlow(
        QuestControlsState(
            totalXp = habitPulseRepository.getTotalXp(),
            brainLevel = HabitPulseEngine.calculateLevel(habitPulseRepository.getTotalXp()),
            levelProgress = HabitPulseEngine.calculateLevelProgress(habitPulseRepository.getTotalXp().toInt()),
            isArabic = habitPulseRepository.isArabic(),
            isLoading = true
        )
    )

    val uiState: StateFlow<QuestUiState> = combine(
        questRepository.activeDailyQuests,
        questRepository.activeWeeklyQuests,
        questRepository.allDailyQuests,
        questRepository.allWeeklyQuests,
        _controlsState
    ) { activeDaily, activeWeekly, allDaily, allWeekly, controls ->
        val totalCatalog = allDaily.size + allWeekly.size
        val claimedCount = (allDaily + allWeekly).count { it.isClaimed }
        QuestUiState(
            selectedTab = controls.selectedTab,
            showFullCatalog = controls.showFullCatalog,
            selectedCategory = controls.selectedCategory,
            activeDailyQuests = activeDaily,
            activeWeeklyQuests = activeWeekly,
            allDailyQuests = allDaily,
            allWeeklyQuests = allWeekly,
            totalXp = controls.totalXp,
            brainLevel = controls.brainLevel,
            levelProgress = controls.levelProgress,
            totalCatalogCount = totalCatalog,
            totalClaimedCount = claimedCount,
            isLoading = controls.isLoading && totalCatalog == 0,
            statusBannerMessage = controls.statusBannerMessage,
            isArabic = habitPulseRepository.isArabic()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = QuestUiState()
    )

    init {
        initializeQuests()
    }

    fun initializeQuests() {
        viewModelScope.launch {
            questRepository.ensureInitializedAndRotated()
            syncXpAndLevelFromRepo()
            _controlsState.update { it.copy(isLoading = false) }
        }
    }

    fun syncXpAndLevelFromRepo() {
        val xp = habitPulseRepository.getTotalXp()
        val level = HabitPulseEngine.calculateLevel(xp)
        val prog = HabitPulseEngine.calculateLevelProgress(xp.toInt())
        _controlsState.update {
            it.copy(
                totalXp = xp,
                brainLevel = level,
                levelProgress = prog,
                isArabic = habitPulseRepository.isArabic()
            )
        }
    }

    fun selectTab(tab: QuestTab) {
        _controlsState.update {
            it.copy(
                selectedTab = tab,
                selectedCategory = null
            )
        }
    }

    fun toggleCatalogView(showFull: Boolean) {
        _controlsState.update { it.copy(showFullCatalog = showFull) }
    }

    fun selectCategoryFilter(category: QuestCategory?) {
        _controlsState.update { current ->
            val toggled = if (current.selectedCategory == category) null else category
            current.copy(selectedCategory = toggled)
        }
    }

    fun advanceQuestProgress(questId: String, step: Int = 1) {
        viewModelScope.launch {
            questRepository.advanceQuestProgress(questId, step)
        }
    }

    fun rerollDailyRotation() {
        viewModelScope.launch {
            questRepository.rerollUnclaimedDailyQuests()
            val isAr = habitPulseRepository.isArabic()
            _controlsState.update {
                it.copy(
                    statusBannerMessage = if (isAr) "🎲 تم سحب مهام يومية جديدة من مخزون الـ 200 مهمة!"
                    else "🎲 Rotated fresh Daily Quests from the 200-quest pool!"
                )
            }
        }
    }

    fun claimQuest(questId: String, onXpSynced: (() -> Unit)? = null) {
        viewModelScope.launch {
            val isAr = habitPulseRepository.isArabic()
            when (val result = questRepository.claimQuestReward(questId)) {
                is ClaimQuestResult.Success -> {
                    _controlsState.update {
                        it.copy(
                            totalXp = result.newTotalXp,
                            brainLevel = result.newLevel,
                            levelProgress = HabitPulseEngine.calculateLevelProgress(result.newTotalXp.toInt()),
                            statusBannerMessage = if (isAr) {
                                "✨ تم استلام +${result.xpAwarded} XP! (المستوى ${result.newLevel})"
                            } else {
                                "✨ Claimed +${result.xpAwarded} XP! (Brain Level ${result.newLevel})"
                            }
                        )
                    }
                    onXpSynced?.invoke()
                }
                ClaimQuestResult.AlreadyClaimed -> {
                    _controlsState.update {
                        it.copy(
                            statusBannerMessage = if (isAr) "⛔ تم استلام مكافأة هذه المهمة مسبقاً اليوم!"
                            else "⛔ Reward already claimed — duplicate claims are blocked!"
                        )
                    }
                }
                ClaimQuestResult.NotCompleted -> {
                    _controlsState.update {
                        it.copy(
                            statusBannerMessage = if (isAr) "⏳ أكمل شروط المهمة أولاً قبل استلام الخبرة."
                            else "⏳ Complete the quest objective before claiming XP."
                        )
                    }
                }
                ClaimQuestResult.NotFound -> Unit
            }
        }
    }

    fun clearBannerMessage() {
        _controlsState.update { it.copy(statusBannerMessage = null) }
    }

    private data class QuestControlsState(
        val selectedTab: QuestTab = QuestTab.DAILY,
        val showFullCatalog: Boolean = false,
        val selectedCategory: QuestCategory? = null,
        val totalXp: Long = 0L,
        val brainLevel: Int = 1,
        val levelProgress: Float = 0f,
        val isLoading: Boolean = true,
        val statusBannerMessage: String? = null,
        val isArabic: Boolean = false
    )
}
