package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppUpdateInfo
import com.example.data.AppUpdaterManager
import com.example.data.HabitPulseRepository
import com.example.data.InstallLaunchResult
import com.example.data.UpdateCheckResult
import com.example.data.UsageTrackerManager
import java.io.File
import com.example.model.AppCategory
import com.example.model.AppThreshold
import com.example.model.BreachRecord
import com.example.model.EggState
import com.example.model.EggTier
import com.example.model.HabitPulseEngine
import com.example.model.InstalledAppInfo
import com.example.model.InventoryItemType
import com.example.model.QuestItem
import com.example.model.ShieldEvent
import com.example.model.ThemeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class HabitPulseUiState(
    // 100-Level Evolution Engine State
    val brainLevel: Int = 1,
    val totalXp: Long = 0L,
    val levelProgress: Float = 0f,
    val rankTitle: String = "Active Mind",
    val rankBadge: String = "🌱",
    val flameScore: Int = 100,
    val streakDays: Int = 0,
    val brainRotPercentage: Float = 0f,
    val isHealingActive: Boolean = false,
    val isGlitchingActive: Boolean = false,
    val currentLevel: Int = 1,
    val levelTitle: String = "Active Mind",
    val levelBadge: String = "🌱",

    // Shield Immunity System
    val activeImmunityUntilMillis: Long = 0L,
    val isImmunityActive: Boolean = false,
    val immunityRemainingHours: Int = 0,
    val shieldCount: Int = 0, // start fresh with 0 shields

    // Inventory System (Keys, Potions, Shields, Mystery Box)
    val inventory: Map<InventoryItemType, Int> = emptyMap(),
    val itemEventMessage: String? = null,

    // 5-Day Streak Milestone System
    val lastClaimedMilestoneStreak: Int = 0,
    val isMilestoneRewardClaimable: Boolean = false,
    val nextMilestoneTarget: Int = 5,

    // Daily & Weekly Quests & Eggs
    val dailyQuests: List<QuestItem> = emptyList(),
    val weeklyQuests: List<QuestItem> = emptyList(),
    val isDailyEggAvailable: Boolean = false,
    val isWeeklyEggAvailable: Boolean = false,
    val eggStates: Map<EggTier, EggState> = emptyMap(),

    // Monitored Apps & Dynamic Shrinking Limits
    val thresholds: List<AppThreshold> = emptyList(),
    val installedApps: List<InstalledAppInfo> = emptyList(),
    val dailyBreachesCount: Int = 0,
    val isEggBlockedToday: Boolean = false,
    val breachLogs: List<BreachRecord> = emptyList(),
    val activeShieldEvent: ShieldEvent? = null,

    // 1000-Point Level Pool & Daily Evaluation Engine
    val totalPoints: Int = 0,
    val pointsInCurrentLevel: Int = 0,
    val dailyBufferPool: Int = 1000,
    val latestDailyOutcome: com.example.model.DailyEvaluationResult? = null,

    // UI Preferences & Permissions
    val selectedTheme: ThemeType = ThemeType.LIQUID_GLASS,
    val isDarkMode: Boolean = true,
    val isArabic: Boolean = false,
    val hasUsagePermission: Boolean = false,
    val isRefreshing: Boolean = false,

    // Interactive Tutorial Overlay State
    val isTutorialVisible: Boolean = false,
    val tutorialCurrentStep: Int = 0,

    // Auto-Update System (GitHub: charfeddine23/Brain)
    val currentAppVersion: String = BuildConfig.VERSION_NAME,
    val isCheckingUpdate: Boolean = false,
    val availableUpdate: AppUpdateInfo? = null,
    val isUpdateDialogVisible: Boolean = false,
    val isDownloadingUpdate: Boolean = false,
    val updateDownloadProgress: Int = 0,
    val updateStatusMessage: String? = null,
    val updateErrorMessage: String? = null,
    val needsInstallPermission: Boolean = false,
    val pendingInstallApkPath: String? = null
)

class HabitPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HabitPulseRepository(application)
    private val usageTracker = UsageTrackerManager(application)
    private val updaterManager = AppUpdaterManager(application)

    private val _uiState = MutableStateFlow(HabitPulseUiState())
    val uiState: StateFlow<HabitPulseUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
        refreshUsageStats()
        loadInstalledApps()
        checkForUpdates(isManual = false)
    }

    private fun loadInitialState() {
        val xp = repository.getTotalXp()
        val level = HabitPulseEngine.calculateLevel(xp)
        val streak = repository.getStreakDays()
        val flame = repository.getFlameScore()
        val rot = repository.getBrainRotPercentage()
        val breaches = repository.getDailyBreachesCount()
        val theme = repository.getSelectedTheme()
        val isDark = repository.isDarkMode()
        val isAr = repository.isArabic()
        val eggs = repository.getEggStates()
        val logs = repository.getBreachRecords()
        val perm = usageTracker.hasUsageStatsPermission()
        val inv = repository.getInventory()
        val immunityTs = repository.getActiveImmunityUntil()
        val claimedMilestone = repository.getLastClaimedMilestoneStreak()

        val isImmune = System.currentTimeMillis() < immunityTs
        val remainingHours = if (isImmune) {
            (((immunityTs - System.currentTimeMillis()) / (1000 * 3600)) + 1).toInt()
        } else 0

        val totalPts = xp.toInt()
        val ptsInLevel = totalPts % HabitPulseEngine.POINTS_PER_LEVEL
        val prog = HabitPulseEngine.calculateLevelProgress(totalPts)

        val dailyQuests = HabitPulseEngine.generateDailyQuests(level)
        val weeklyQuests = HabitPulseEngine.generateWeeklyQuests(level)

        // Calculate next milestone (every 5 days)
        val nextMilestone = ((streak / 5) + 1) * 5
        val canClaimMilestone = streak >= 5 && streak > claimedMilestone && (streak % 5 == 0 || streak > (claimedMilestone + 4))

        val loadedThresholds = repository.getAppThresholds()
        val shrinkAdjustedThresholds = applyDynamicShrinkingLimits(loadedThresholds, level)

        val totalShieldsInInventory = (inv[InventoryItemType.SHIELD_24H] ?: 0) + (inv[InventoryItemType.SHIELD_1WEEK] ?: 0)

        _uiState.update {
            it.copy(
                brainLevel = level,
                totalXp = xp,
                totalPoints = totalPts,
                pointsInCurrentLevel = ptsInLevel,
                levelProgress = prog,
                dailyBufferPool = repository.getDailyBufferRemaining(),
                rankTitle = HabitPulseEngine.getRankTitle(level, isAr),
                rankBadge = HabitPulseEngine.getRankBadgeEmoji(level),
                flameScore = flame,
                streakDays = streak,
                brainRotPercentage = rot,
                dailyBreachesCount = breaches,
                isEggBlockedToday = breaches > 0,
                selectedTheme = theme,
                isDarkMode = isDark,
                isArabic = isAr,
                eggStates = eggs,
                breachLogs = logs,
                hasUsagePermission = perm,
                inventory = inv,
                shieldCount = totalShieldsInInventory.coerceAtLeast(repository.getShieldCount()),
                activeImmunityUntilMillis = immunityTs,
                isImmunityActive = isImmune,
                immunityRemainingHours = remainingHours,
                lastClaimedMilestoneStreak = claimedMilestone,
                isMilestoneRewardClaimable = canClaimMilestone,
                nextMilestoneTarget = nextMilestone,
                dailyQuests = dailyQuests,
                weeklyQuests = weeklyQuests,
                thresholds = shrinkAdjustedThresholds,
                isTutorialVisible = !repository.hasCompletedTutorial(),
                tutorialCurrentStep = 0
            )
        }
        evaluateQuestProgress(
            thresholds = shrinkAdjustedThresholds,
            streakDays = streak,
            isEndOfDayEvaluation = false
        )
    }

    /**
     * DYNAMIC SHRINKING TIME RULE:
     * Recalculates thresholds dynamically based on brain level (1 to 100):
     * - Social Media: 360m -> 20m
     * - Entertainment & Games: 360m -> 40m
     * - Productivity: 480m -> 60m focused hour
     */
    private fun applyDynamicShrinkingLimits(list: List<AppThreshold>, level: Int): List<AppThreshold> {
        return list.map { threshold ->
            val cat = threshold.appCategory
            val dynamicMaxMinutes = HabitPulseEngine.calculateDynamicTimeLimit(cat, level)
            val dynamicMaxLaunches = HabitPulseEngine.calculateDynamicLaunchLimit(cat, level)
            threshold.copy(
                maxMinutes = dynamicMaxMinutes,
                maxLaunches = dynamicMaxLaunches
            )
        }
    }

    fun checkPermission() {
        val hasPerm = usageTracker.hasUsageStatsPermission()
        _uiState.update { it.copy(hasUsagePermission = hasPerm) }
        if (hasPerm) {
            refreshUsageStats()
        }
    }

    fun getUsageAccessIntent() = usageTracker.createUsageAccessIntent()

    fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apps = usageTracker.getInstalledLaunchableApps()
                val existingThresholds = repository.getAppThresholds()
                val level = _uiState.value.brainLevel
                val existingMap = existingThresholds.associateBy { it.packageName }

                val mergedThresholds = apps.map { app ->
                    val existing = existingMap[app.packageName]
                    if (existing != null) {
                        val cat = existing.appCategory
                        val dynamicMins = HabitPulseEngine.calculateDynamicTimeLimit(cat, level)
                        val dynamicLaunches = HabitPulseEngine.calculateDynamicLaunchLimit(cat, level)
                        existing.copy(
                            appName = app.appName,
                            maxMinutes = dynamicMins,
                            maxLaunches = dynamicLaunches
                        )
                    } else {
                        val inferredCat = AppCategory.fromPackageName(app.packageName)
                        val dynamicMins = HabitPulseEngine.calculateDynamicTimeLimit(inferredCat, level)
                        val dynamicLaunches = HabitPulseEngine.calculateDynamicLaunchLimit(inferredCat, level)
                        AppThreshold(
                            packageName = app.packageName,
                            appName = app.appName,
                            maxMinutes = dynamicMins,
                            maxLaunches = dynamicLaunches,
                            currentMinutes = 0,
                            currentLaunches = 0,
                            category = inferredCat.name
                        )
                    }
                }.distinctBy { it.packageName }

                repository.saveAppThresholds(mergedThresholds)

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            installedApps = apps,
                            thresholds = mergedThresholds
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HabitPulseViewModel", "Failed to loadInstalledApps", e)
            }
        }
    }

    /**
     * Queries exact real-time foreground usage across all session durations (every second counts).
     * Guaranteed never to crash the UI or corrupt cache.
     */
    fun refreshUsageStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val hasPerm = usageTracker.hasUsageStatsPermission()
            if (!hasPerm) {
                _uiState.update { it.copy(hasUsagePermission = false, isRefreshing = false) }
                return@launch
            }

            try {
                withContext(Dispatchers.IO) {
                    val usageMap = usageTracker.queryTodayUsageStats()
                    val currentList = repository.getAppThresholds()
                    val level = _uiState.value.brainLevel

                    val updatedList = currentList.map { threshold ->
                        val usage = usageMap[threshold.packageName]
                        val cat = threshold.appCategory
                        val dynamicMins = HabitPulseEngine.calculateDynamicTimeLimit(cat, level)
                        val dynamicLaunches = HabitPulseEngine.calculateDynamicLaunchLimit(cat, level)

                        if (usage != null) {
                            threshold.copy(
                                currentMinutes = usage.foregroundMinutes,
                                currentLaunches = usage.launches,
                                maxMinutes = dynamicMins,
                                maxLaunches = dynamicLaunches
                            )
                        } else {
                            threshold.copy(
                                maxMinutes = dynamicMins,
                                maxLaunches = dynamicLaunches
                            )
                        }
                    }.distinctBy { it.packageName }

                    repository.saveAppThresholds(updatedList)

                    withContext(Dispatchers.Main) {
                        _uiState.update {
                            it.copy(
                                thresholds = updatedList,
                                hasUsagePermission = true,
                                isRefreshing = false
                            )
                        }
                        checkThresholdBreaches(updatedList)
                        evaluateQuestProgress(
                            thresholds = updatedList,
                            streakDays = _uiState.value.streakDays,
                            isEndOfDayEvaluation = false
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HabitPulseViewModel", "Error in refreshUsageStats", e)
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isRefreshing = false) }
                }
            }
        }
    }

    private fun checkThresholdBreaches(thresholdList: List<AppThreshold>) {
        for (threshold in thresholdList) {
            if (threshold.isBreached) {
                val alreadyLogged = _uiState.value.breachLogs.any {
                    it.packageName == threshold.packageName &&
                    it.breachIndexToday == _uiState.value.dailyBreachesCount
                }
                if (!alreadyLogged) {
                    executeBreachPenalty(threshold)
                }
            }
        }
    }

    /**
     * LIVE BREACH & 1,000-POINT SAFETY BUFFER SYSTEM:
     * 1. If active shield immunity is ON -> 100% absorbed (0 deducted from buffer, 0 streak impact).
     * 2. Otherwise -> deducts Boss App breach points from today's 1,000-pt protective buffer pool.
     * 3. While remaining buffer > 0 -> Streak is SAFE and preserved! No demotion!
     * 4. ONLY IF remaining buffer reaches 0 -> Shield auto-activates from inventory (if available) to stop demotion,
     *    and the streak breaks & resets to 0.
     */
    fun executeBreachPenalty(threshold: AppThreshold) {
        val currentBreaches = repository.getDailyBreachesCount() + 1
        repository.setDailyBreachesCount(currentBreaches)

        val isImmune = System.currentTimeMillis() < _uiState.value.activeImmunityUntilMillis
        val inventory = _uiState.value.inventory
        val has24hShield = (inventory[InventoryItemType.SHIELD_24H] ?: 0) > 0
        val has1wShield = (inventory[InventoryItemType.SHIELD_1WEEK] ?: 0) > 0
        val legacyShields = repository.getShieldCount()

        if (isImmune) {
            val log = BreachRecord(
                id = UUID.randomUUID().toString(),
                packageName = threshold.packageName,
                appName = threshold.appName,
                timestamp = System.currentTimeMillis(),
                breachIndexToday = currentBreaches,
                penaltyDeducted = 0,
                wasAbsorbedByShield = true,
                streakResetOccurred = false
            )
            repository.addBreachRecord(log)
            _uiState.update {
                it.copy(
                    dailyBreachesCount = currentBreaches,
                    isEggBlockedToday = true,
                    breachLogs = repository.getBreachRecords(),
                    activeShieldEvent = ShieldEvent.ShieldAbsorbed(threshold.appName, currentBreaches, it.shieldCount)
                )
            }
            return
        }

        // Deduct from today's 1,000-point safety buffer across all breached Boss Apps
        val singleAppPenalty = HabitPulseEngine.calculateBreachDeduction(listOf(threshold))
        val allBreachedDeduction = HabitPulseEngine.calculateBreachDeduction(
            _uiState.value.thresholds.filter { it.isBreached }
        ).coerceAtLeast(singleAppPenalty)
        val remainingBuffer = (HabitPulseEngine.DAILY_BUFFER_POOL_SIZE - allBreachedDeduction).coerceAtLeast(0)
        repository.setDailyBufferRemaining(remainingBuffer)

        if (remainingBuffer > 0) {
            // Buffer survived (> 0 points left): Streak is NOT broken and Level is NOT demoted!
            val log = BreachRecord(
                id = UUID.randomUUID().toString(),
                packageName = threshold.packageName,
                appName = threshold.appName,
                timestamp = System.currentTimeMillis(),
                breachIndexToday = currentBreaches,
                penaltyDeducted = singleAppPenalty,
                wasAbsorbedByShield = false,
                streakResetOccurred = false
            )
            repository.addBreachRecord(log)
            _uiState.update {
                it.copy(
                    dailyBufferPool = remainingBuffer,
                    dailyBreachesCount = currentBreaches,
                    isEggBlockedToday = true,
                    breachLogs = repository.getBreachRecords(),
                    itemEventMessage = if (it.isArabic) {
                        "🛡️ امتص مخزن الأمان التجاوز (-$singleAppPenalty نقطة). المتبقي: $remainingBuffer / 1000"
                    } else {
                        "🛡️ Safety Buffer absorbed -$singleAppPenalty pts ($remainingBuffer / 1000 left). Streak safe!"
                    }
                )
            }
            return
        }

        // Buffer completely drained (reached 0): Check Shield or apply Streak Reset + Demotion
        repository.setStreakDays(0)

        if (has24hShield || has1wShield || legacyShields > 0) {
            val immunityDuration = if (has1wShield && !has24hShield) 7 * 24 * 3600 * 1000L else 24 * 3600 * 1000L
            val immunityUntil = System.currentTimeMillis() + immunityDuration

            if (has24hShield) {
                repository.consumeItem(InventoryItemType.SHIELD_24H)
            } else if (has1wShield) {
                repository.consumeItem(InventoryItemType.SHIELD_1WEEK)
            } else {
                repository.setShieldCount((legacyShields - 1).coerceAtLeast(0))
            }
            repository.setActiveImmunityUntil(immunityUntil)

            val updatedInv = repository.getInventory()
            val remainingShields = computeTotalShields(updatedInv)

            val log = BreachRecord(
                id = UUID.randomUUID().toString(),
                packageName = threshold.packageName,
                appName = threshold.appName,
                timestamp = System.currentTimeMillis(),
                breachIndexToday = currentBreaches,
                penaltyDeducted = HabitPulseEngine.DAILY_BUFFER_POOL_SIZE,
                wasAbsorbedByShield = true,
                streakResetOccurred = true
            )
            repository.addBreachRecord(log)

            _uiState.update {
                it.copy(
                    dailyBufferPool = 0,
                    streakDays = 0,
                    inventory = updatedInv,
                    shieldCount = remainingShields,
                    activeImmunityUntilMillis = immunityUntil,
                    isImmunityActive = true,
                    immunityRemainingHours = (immunityDuration / (1000 * 3600)).toInt(),
                    dailyBreachesCount = currentBreaches,
                    isEggBlockedToday = true,
                    breachLogs = repository.getBreachRecords(),
                    activeShieldEvent = ShieldEvent.ShieldAbsorbed(threshold.appName, currentBreaches, remainingShields)
                )
            }
        } else {
            // Unprotected & Buffer Exhausted (0/1000): Streak resets to 0 + Brain Demotion + Rot surge
            val currentLevel = _uiState.value.brainLevel
            val demotedLevel = (currentLevel - 1).coerceAtLeast(1)
            val newXp = HabitPulseEngine.getXpForLevel(demotedLevel)
            repository.setTotalXp(newXp)

            val newFlame = (repository.getFlameScore() - 25).coerceAtLeast(0)
            repository.setFlameScore(newFlame)
            increaseBrainRot(30f)

            val log = BreachRecord(
                id = UUID.randomUUID().toString(),
                packageName = threshold.packageName,
                appName = threshold.appName,
                timestamp = System.currentTimeMillis(),
                breachIndexToday = currentBreaches,
                penaltyDeducted = singleAppPenalty,
                wasAbsorbedByShield = false,
                streakResetOccurred = true
            )
            repository.addBreachRecord(log)

            val title = HabitPulseEngine.getRankTitle(demotedLevel, _uiState.value.isArabic)
            val badge = HabitPulseEngine.getRankBadgeEmoji(demotedLevel)

            _uiState.update {
                it.copy(
                    brainLevel = demotedLevel,
                    totalXp = newXp,
                    totalPoints = newXp.toInt(),
                    dailyBufferPool = 0,
                    rankTitle = title,
                    rankBadge = badge,
                    flameScore = newFlame,
                    streakDays = 0,
                    dailyBreachesCount = currentBreaches,
                    isEggBlockedToday = true,
                    isGlitchingActive = true,
                    breachLogs = repository.getBreachRecords(),
                    activeShieldEvent = ShieldEvent.PenaltyApplied(threshold.appName, currentBreaches, singleAppPenalty, true)
                )
            }
            scheduleGlitchReset()
        }
    }

    private fun scheduleGlitchReset() {
        viewModelScope.launch {
            delay(4000)
            _uiState.update { it.copy(isGlitchingActive = false) }
        }
    }

    private fun computeTotalShields(inv: Map<InventoryItemType, Int>): Int {
        val invShields = (inv[InventoryItemType.SHIELD_24H] ?: 0) + (inv[InventoryItemType.SHIELD_1WEEK] ?: 0)
        return invShields.coerceAtLeast(repository.getShieldCount())
    }

    /**
     * Consumes an item from the 7 reward item types in Inventory.
     * Strictly guarantees every item (including Mystery Box) decrements by 1 upon use.
     */
    fun useInventoryItem(itemType: InventoryItemType) {
        val count = _uiState.value.inventory[itemType] ?: 0
        if (count <= 0) return

        val isAr = _uiState.value.isArabic

        when (itemType) {
            InventoryItemType.DAILY_QUEST_SKIP_KEY -> {
                val firstIncomplete = _uiState.value.dailyQuests.firstOrNull { !it.isCompleted }
                if (firstIncomplete == null) {
                    _uiState.update {
                        it.copy(
                            itemEventMessage = if (isAr) "جميع المهام اليومية مكتملة بالفعل! احتفظ بالمفتاح للغد." else "All daily quests are already completed! Save your key."
                        )
                    }
                    return
                }
                skipQuestWithKey(firstIncomplete.id, isWeekly = false)
            }
            InventoryItemType.WEEKLY_QUEST_SKIP_KEY -> {
                val firstIncomplete = _uiState.value.weeklyQuests.firstOrNull { !it.isCompleted }
                if (firstIncomplete == null) {
                    _uiState.update {
                        it.copy(
                            itemEventMessage = if (isAr) "جميع المهام الأسبوعية مكتملة بالفعل! احتفظ بالمفتاح للأسبوع القادم." else "All weekly quests are already completed! Save your key."
                        )
                    }
                    return
                }
                skipQuestWithKey(firstIncomplete.id, isWeekly = true)
            }
            InventoryItemType.BRAIN_HEALING_POTION -> {
                if (!repository.consumeItem(itemType)) return
                val updatedInv = repository.getInventory()
                elevateBrainLevelByOne()
                healBrainRot(35f)
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        shieldCount = computeTotalShields(updatedInv),
                        isHealingActive = true,
                        itemEventMessage = if (isAr) "🧪 تم تناول جرعة التعافي الذهني: +1 مستوى وتطهير التعفن الدماغي!" else "🧪 Brain Healing Potion drunk: +1 Brain Level & Rot Purged!"
                    )
                }
                scheduleHealingReset()
            }
            InventoryItemType.LEGENDARY_POTION -> {
                if (!repository.consumeItem(itemType)) return
                val updatedInv = repository.getInventory()
                elevateBrainLevelByOne()
                healBrainRot(100f)
                val completedDaily = _uiState.value.dailyQuests.map {
                    repository.markDailyQuestCompleted(it.id)
                    it.copy(isCompleted = true, currentProgress = it.targetProgress)
                }
                val completedWeekly = _uiState.value.weeklyQuests.map {
                    repository.markWeeklyQuestCompleted(it.id)
                    it.copy(isCompleted = true, currentProgress = it.targetProgress)
                }
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        shieldCount = computeTotalShields(updatedInv),
                        dailyQuests = completedDaily,
                        weeklyQuests = completedWeekly,
                        isDailyEggAvailable = true,
                        isWeeklyEggAvailable = true,
                        isHealingActive = true,
                        itemEventMessage = if (isAr) "⚗️ تم استهلاك الجرعة الأسطورية: +1 مستوى واكتمال كافة المهمات فوراً!" else "⚗️ LEGENDARY POTION CONSUMED: +1 Level, All Quests Auto-Completed!"
                    )
                }
                scheduleHealingReset()
            }
            InventoryItemType.SHIELD_24H -> {
                if (!repository.consumeItem(itemType)) return
                val updatedInv = repository.getInventory()
                val immunityUntil = System.currentTimeMillis() + (24 * 3600 * 1000L)
                repository.setActiveImmunityUntil(immunityUntil)
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        shieldCount = computeTotalShields(updatedInv),
                        activeImmunityUntilMillis = immunityUntil,
                        isImmunityActive = true,
                        immunityRemainingHours = 24,
                        itemEventMessage = if (isAr) "🛡️ تم تفعيل درع حماية 24 ساعة: حصانة تامة من الهبوط!" else "🛡️ 24-Hour Shield Activated: Demotion Immunity Active!"
                    )
                }
            }
            InventoryItemType.SHIELD_1WEEK -> {
                if (!repository.consumeItem(itemType)) return
                val updatedInv = repository.getInventory()
                val immunityUntil = System.currentTimeMillis() + (7 * 24 * 3600 * 1000L)
                repository.setActiveImmunityUntil(immunityUntil)
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        shieldCount = computeTotalShields(updatedInv),
                        activeImmunityUntilMillis = immunityUntil,
                        isImmunityActive = true,
                        immunityRemainingHours = 168,
                        itemEventMessage = if (isAr) "🔰 تم تفعيل درع الحصانة الأسبوعي: 7 أيام كاملة من الحماية!" else "🔰 1-Week Aegis Fortress Activated: 7 Days of Immunity!"
                    )
                }
            }
            InventoryItemType.MYSTERY_BOX -> {
                if (!repository.consumeItem(itemType)) return
                // Rolls mystery loot (never rolls MYSTERY_BOX itself) and adds it to inventory
                val droppedItem = HabitPulseEngine.openMysteryBoxLoot()
                repository.addItem(droppedItem, 1)
                val refreshedInv = repository.getInventory()
                _uiState.update {
                    it.copy(
                        inventory = refreshedInv,
                        shieldCount = computeTotalShields(refreshedInv),
                        itemEventMessage = if (isAr) {
                            "🎁 تم فتح صندوق السايبر (-1 صندوق) وحصلت على: ${droppedItem.iconEmoji} ${droppedItem.titleAr}!"
                        } else {
                            "🎁 Mystery Box opened (-1 box)! Discovered: ${droppedItem.iconEmoji} ${droppedItem.titleEn}"
                        }
                    )
                }
            }
        }
    }

    private fun elevateBrainLevelByOne() {
        val currentLevel = _uiState.value.brainLevel
        val nextLevel = (currentLevel + 1).coerceAtMost(HabitPulseEngine.MAX_LEVEL)
        val targetXp = HabitPulseEngine.getXpForLevel(nextLevel)
        repository.setTotalXp(targetXp)

        val totalPts = targetXp.toInt()
        val ptsInLevel = totalPts % HabitPulseEngine.POINTS_PER_LEVEL
        val prog = HabitPulseEngine.calculateLevelProgress(totalPts)
        val title = HabitPulseEngine.getRankTitle(nextLevel, _uiState.value.isArabic)
        val badge = HabitPulseEngine.getRankBadgeEmoji(nextLevel)

        _uiState.update {
            val adjustedThresholds = applyDynamicShrinkingLimits(it.thresholds, nextLevel)
            repository.saveAppThresholds(adjustedThresholds)
            it.copy(
                brainLevel = nextLevel,
                totalXp = targetXp,
                totalPoints = totalPts,
                pointsInCurrentLevel = ptsInLevel,
                levelProgress = prog,
                rankTitle = title,
                rankBadge = badge,
                thresholds = adjustedThresholds
            )
        }
    }

    private fun scheduleHealingReset() {
        viewModelScope.launch {
            delay(3500)
            _uiState.update { it.copy(isHealingActive = false) }
        }
    }

    /**
     * 5-Day Streak Milestone Reward Claiming:
     * Grants randomized or periodic item from the 7 reward items.
     */
    fun claimStreakMilestoneReward() {
        val streak = _uiState.value.streakDays
        if (streak < 5) return

        val rolledReward = HabitPulseEngine.rollStreakMilestoneReward(streak)
        repository.addItem(rolledReward, 1)
        repository.setLastClaimedMilestoneStreak(streak)

        val updatedInv = repository.getInventory()
        val nextMilestone = ((streak / 5) + 1) * 5

        _uiState.update {
            it.copy(
                inventory = updatedInv,
                shieldCount = computeTotalShields(updatedInv),
                lastClaimedMilestoneStreak = streak,
                isMilestoneRewardClaimable = false,
                nextMilestoneTarget = nextMilestone,
                itemEventMessage = if (it.isArabic) {
                    "🎉 تم استلام مكافأة تتابع 5 أيام! حصلت على: ${rolledReward.iconEmoji} ${rolledReward.titleAr}"
                } else {
                    "🎉 5-Day Streak Milestone Claimed! You received: ${rolledReward.iconEmoji} ${rolledReward.titleEn}"
                }
            )
        }
    }

    /**
     * Skips a specific daily or weekly quest ONLY when the player owns and spends its dedicated Skip Key.
     * Manual tap-to-complete without a key is strictly disallowed.
     */
    fun skipQuestWithKey(questId: String, isWeekly: Boolean) {
        val isAr = _uiState.value.isArabic
        if (isWeekly) {
            val quests = _uiState.value.weeklyQuests.toMutableList()
            val idx = quests.indexOfFirst { it.id == questId && !it.isCompleted }
            if (idx < 0) return
            if (!repository.consumeItem(InventoryItemType.WEEKLY_QUEST_SKIP_KEY)) return

            val updatedInv = repository.getInventory()
            quests[idx] = quests[idx].copy(isCompleted = true, currentProgress = quests[idx].targetProgress)
            repository.markWeeklyQuestCompleted(questId)
            addXp(quests[idx].xpReward, "Weekly Quest Skip Key")
            val allDone = quests.all { it.isCompleted }
            _uiState.update {
                it.copy(
                    inventory = updatedInv,
                    shieldCount = computeTotalShields(updatedInv),
                    weeklyQuests = quests,
                    isWeeklyEggAvailable = allDone,
                    itemEventMessage = if (isAr) {
                        "🔑 تم استخدام مفتاح التخطي الأسبوعي: اكتملت المهمة (+${quests[idx].xpReward} XP)!"
                    } else {
                        "🔑 Weekly Skip Key used: Quest completed (+${quests[idx].xpReward} XP)!"
                    }
                )
            }
        } else {
            val quests = _uiState.value.dailyQuests.toMutableList()
            val idx = quests.indexOfFirst { it.id == questId && !it.isCompleted }
            if (idx < 0) return
            if (!repository.consumeItem(InventoryItemType.DAILY_QUEST_SKIP_KEY)) return

            val updatedInv = repository.getInventory()
            quests[idx] = quests[idx].copy(isCompleted = true, currentProgress = quests[idx].targetProgress)
            repository.markDailyQuestCompleted(questId)
            addXp(quests[idx].xpReward, "Daily Quest Skip Key")
            val allDone = quests.all { it.isCompleted }
            _uiState.update {
                it.copy(
                    inventory = updatedInv,
                    shieldCount = computeTotalShields(updatedInv),
                    dailyQuests = quests,
                    isDailyEggAvailable = allDone,
                    itemEventMessage = if (isAr) {
                        "🗝️ تم استخدام مفتاح التخطي اليومي: اكتملت المهمة (+${quests[idx].xpReward} XP)!"
                    } else {
                        "🗝️ Daily Skip Key used: Quest completed (+${quests[idx].xpReward} XP)!"
                    }
                )
            }
        }
    }

    /**
     * Automatically evaluates real user usage, daily outcomes, and streak counters to update
     * daily and weekly quest progress without allowing manual click-to-complete shortcuts.
     */
    private fun evaluateQuestProgress(
        thresholds: List<AppThreshold>,
        streakDays: Int,
        isEndOfDayEvaluation: Boolean,
        dailyOutcome: com.example.model.DailyEvaluationResult? = null
    ) {
        val state = _uiState.value
        val isAr = state.isArabic
        val currentLevel = state.brainLevel
        val completedDailyIds = repository.getCompletedDailyQuestIds()
        val completedWeeklyIds = repository.getCompletedWeeklyQuestIds()

        // Update weekly counters only on genuine end-of-day evaluations
        if (isEndOfDayEvaluation && dailyOutcome != null) {
            val socialBreached = thresholds.any { it.appCategory == AppCategory.SOCIAL_MEDIA && it.isBreached }
            if (!socialBreached) {
                repository.setWeeklyCleanSocialDays(repository.getWeeklyCleanSocialDays() + 1)
            }
            if (dailyOutcome.levelsGainedOrLost >= 0) {
                repository.setWeeklyNoDemotionDays(repository.getWeeklyNoDemotionDays() + 1)
            } else {
                repository.setWeeklyNoDemotionDays(0)
            }
        }

        val socialMinutes = thresholds.filter { it.appCategory == AppCategory.SOCIAL_MEDIA }.sumOf { it.currentMinutes }
        val targetSocialLimit = HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.SOCIAL_MEDIA, currentLevel)
        val focusMinutes = thresholds.filter { it.appCategory == AppCategory.PRODUCTIVITY_TOOLS }.sumOf { it.currentMinutes }
        val cleanSocialDays = repository.getWeeklyCleanSocialDays()
        val noDemotionDays = repository.getWeeklyNoDemotionDays()

        var totalNewQuestXp = 0L
        var newlyCompletedCount = 0

        val baseDaily = if (state.dailyQuests.isNotEmpty()) state.dailyQuests else HabitPulseEngine.generateDailyQuests(currentLevel)
        val updatedDaily = baseDaily.map { quest ->
            if (quest.id in completedDailyIds || quest.isCompleted) {
                quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
            } else {
                when (quest.id) {
                    "daily_social_limit" -> {
                        if (isEndOfDayEvaluation && socialMinutes < targetSocialLimit) {
                            repository.markDailyQuestCompleted(quest.id)
                            totalNewQuestXp += quest.xpReward
                            newlyCompletedCount++
                            quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
                        } else {
                            quest.copy(currentProgress = 0, isCompleted = false)
                        }
                    }
                    "daily_deep_work" -> {
                        val clamped = focusMinutes.coerceAtMost(quest.targetProgress)
                        if (focusMinutes >= quest.targetProgress) {
                            repository.markDailyQuestCompleted(quest.id)
                            totalNewQuestXp += quest.xpReward
                            newlyCompletedCount++
                            quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
                        } else {
                            quest.copy(currentProgress = clamped, isCompleted = false)
                        }
                    }
                    "daily_zero_breaches" -> {
                        if (isEndOfDayEvaluation && dailyOutcome?.outcomeType == com.example.model.DailyOutcomeType.CLEAN_SWEEP) {
                            repository.markDailyQuestCompleted(quest.id)
                            totalNewQuestXp += quest.xpReward
                            newlyCompletedCount++
                            quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
                        } else {
                            quest.copy(currentProgress = 0, isCompleted = false)
                        }
                    }
                    else -> quest
                }
            }
        }

        val baseWeekly = if (state.weeklyQuests.isNotEmpty()) state.weeklyQuests else HabitPulseEngine.generateWeeklyQuests(currentLevel)
        val updatedWeekly = baseWeekly.map { quest ->
            if (quest.id in completedWeeklyIds || quest.isCompleted) {
                quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
            } else {
                when (quest.id) {
                    "weekly_5day_streak" -> {
                        val clamped = streakDays.coerceAtMost(quest.targetProgress)
                        if (streakDays >= quest.targetProgress) {
                            repository.markWeeklyQuestCompleted(quest.id)
                            totalNewQuestXp += quest.xpReward
                            newlyCompletedCount++
                            quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
                        } else {
                            quest.copy(currentProgress = clamped, isCompleted = false)
                        }
                    }
                    "weekly_social_detox" -> {
                        val clamped = cleanSocialDays.coerceAtMost(quest.targetProgress)
                        if (cleanSocialDays >= quest.targetProgress) {
                            repository.markWeeklyQuestCompleted(quest.id)
                            totalNewQuestXp += quest.xpReward
                            newlyCompletedCount++
                            quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
                        } else {
                            quest.copy(currentProgress = clamped, isCompleted = false)
                        }
                    }
                    "weekly_aegis_master" -> {
                        val clamped = noDemotionDays.coerceAtMost(quest.targetProgress)
                        if (noDemotionDays >= quest.targetProgress) {
                            repository.markWeeklyQuestCompleted(quest.id)
                            totalNewQuestXp += quest.xpReward
                            newlyCompletedCount++
                            quest.copy(isCompleted = true, currentProgress = quest.targetProgress)
                        } else {
                            quest.copy(currentProgress = clamped, isCompleted = false)
                        }
                    }
                    else -> quest
                }
            }
        }

        if (totalNewQuestXp > 0L) {
            addXp(totalNewQuestXp, "Real Quest Auto-Completion")
        }

        _uiState.update { current ->
            val completionBanner = if (newlyCompletedCount > 0 && !isEndOfDayEvaluation) {
                if (isAr) "✅ تم إنجاز $newlyCompletedCount مهمة تلقائياً (+$totalNewQuestXp XP)!"
                else "✅ $newlyCompletedCount quest(s) completed automatically (+$totalNewQuestXp XP)!"
            } else {
                current.itemEventMessage
            }
            current.copy(
                dailyQuests = updatedDaily,
                weeklyQuests = updatedWeekly,
                isDailyEggAvailable = updatedDaily.all { it.isCompleted },
                isWeeklyEggAvailable = updatedWeekly.all { it.isCompleted },
                itemEventMessage = completionBanner
            )
        }
    }

    fun addPoints(amount: Int, reason: String = "") {
        addXp(amount.toLong(), reason)
    }

    fun addXp(amount: Long, reason: String = "") {
        val currentXp = repository.getTotalXp()
        val newXp = (currentXp + amount).coerceIn(0L, HabitPulseEngine.MAX_XP)
        repository.setTotalXp(newXp)

        val totalPts = newXp.toInt()
        val newLevel = HabitPulseEngine.calculateLevelFromPoints(totalPts)
        val ptsInLevel = totalPts % HabitPulseEngine.POINTS_PER_LEVEL
        val prog = HabitPulseEngine.calculateLevelProgress(totalPts)
        val title = HabitPulseEngine.getRankTitle(newLevel, _uiState.value.isArabic)
        val badge = HabitPulseEngine.getRankBadgeEmoji(newLevel)

        _uiState.update {
            val adjustedThresholds = if (newLevel != it.brainLevel) {
                applyDynamicShrinkingLimits(it.thresholds, newLevel).also { list ->
                    repository.saveAppThresholds(list)
                }
            } else {
                it.thresholds
            }
            it.copy(
                brainLevel = newLevel,
                totalXp = newXp,
                totalPoints = totalPts,
                pointsInCurrentLevel = ptsInLevel,
                levelProgress = prog,
                rankTitle = title,
                rankBadge = badge,
                thresholds = adjustedThresholds
            )
        }
    }

    fun increaseBrainRot(amount: Float) {
        val currentRot = repository.getBrainRotPercentage()
        val newRot = (currentRot + amount).coerceIn(0f, 100f)
        repository.setBrainRotPercentage(newRot)
        _uiState.update { it.copy(brainRotPercentage = newRot) }
    }

    fun healBrainRot(amount: Float) {
        val currentRot = repository.getBrainRotPercentage()
        val newRot = (currentRot - amount).coerceIn(0f, 100f)
        repository.setBrainRotPercentage(newRot)
        _uiState.update { it.copy(brainRotPercentage = newRot) }
    }

    fun clearItemMessage() {
        _uiState.update { it.copy(itemEventMessage = null) }
    }

    fun dismissShieldEvent() {
        _uiState.update { it.copy(activeShieldEvent = null) }
    }

    /**
     * Evaluates End-of-Day Boss Apps Breaches against the 1,000-Point Safety Buffer Pool:
     * 1. Deducts breach penalties from the 1,000-pt buffer pool.
     * 2. Awards & saves +1 Streak Day IF AND ONLY IF remaining buffer > 0.
     * 3. Resets Streak to 0 ONLY IF the 1,000-pt buffer reaches 0 due to heavy breaches.
     */
    fun triggerDailyEvaluation() {
        val thresholds = _uiState.value.thresholds
        val breachedApps = thresholds.filter { it.isBreached }
        val currentStreak = repository.getStreakDays()
        val currentTotalPoints = _uiState.value.totalPoints
        val hasShield = (_uiState.value.inventory[InventoryItemType.SHIELD_24H] ?: 0) > 0 ||
                (_uiState.value.inventory[InventoryItemType.SHIELD_1WEEK] ?: 0) > 0 ||
                _uiState.value.isImmunityActive ||
                _uiState.value.shieldCount > 0

        val result = HabitPulseEngine.evaluateDailyOutcome(
            breachedApps = breachedApps,
            currentStreak = currentStreak,
            currentTotalPoints = currentTotalPoints,
            hasActiveShield = hasShield
        )

        // Consume Shield if buffer reached 0 and Shield blocked demotions
        if (result.wasShieldConsumed) {
            val inv = _uiState.value.inventory
            if ((inv[InventoryItemType.SHIELD_24H] ?: 0) > 0) {
                repository.consumeItem(InventoryItemType.SHIELD_24H)
            } else if ((inv[InventoryItemType.SHIELD_1WEEK] ?: 0) > 0) {
                repository.consumeItem(InventoryItemType.SHIELD_1WEEK)
            } else {
                repository.setShieldCount((_uiState.value.shieldCount - 1).coerceAtLeast(0))
            }
        }

        // Persist remaining 1,000-pt daily buffer
        repository.setDailyBufferRemaining(result.bufferPoolRemaining)

        // Persist permanent XP changes (Clean Sweep bonus or overflow demotion)
        val updatedPoints = (currentTotalPoints + result.pointsAwardedOrDeducted).coerceIn(0, HabitPulseEngine.MAX_POINTS)
        repository.setTotalXp(updatedPoints.toLong())

        // Streak rule: +1 day iff buffer > 0, reset to 0 iff buffer == 0
        val newStreak = if (result.isStreakReset) 0 else (currentStreak + result.streakDelta).coerceAtLeast(0)
        repository.setStreakDays(newStreak)

        val newLevel = HabitPulseEngine.calculateLevelFromPoints(updatedPoints)
        val ptsInLevel = updatedPoints % HabitPulseEngine.POINTS_PER_LEVEL
        val prog = HabitPulseEngine.calculateLevelProgress(updatedPoints)
        val title = HabitPulseEngine.getRankTitle(newLevel, _uiState.value.isArabic)
        val badge = HabitPulseEngine.getRankBadgeEmoji(newLevel)

        val adjustedThresholds = applyDynamicShrinkingLimits(thresholds, newLevel)
        repository.saveAppThresholds(adjustedThresholds)

        val claimedMilestone = repository.getLastClaimedMilestoneStreak()
        val nextMilestone = ((newStreak / 5) + 1) * 5
        val canClaimMilestone = newStreak >= 5 && newStreak > claimedMilestone && (newStreak % 5 == 0 || newStreak > (claimedMilestone + 4))

        val updatedInv = repository.getInventory()
        val totalShields = computeTotalShields(updatedInv)

        _uiState.update {
            it.copy(
                brainLevel = newLevel,
                totalXp = updatedPoints.toLong(),
                totalPoints = updatedPoints,
                pointsInCurrentLevel = ptsInLevel,
                levelProgress = prog,
                rankTitle = title,
                rankBadge = badge,
                streakDays = newStreak,
                dailyBufferPool = result.bufferPoolRemaining,
                thresholds = adjustedThresholds,
                isMilestoneRewardClaimable = canClaimMilestone,
                nextMilestoneTarget = nextMilestone,
                inventory = updatedInv,
                shieldCount = totalShields,
                latestDailyOutcome = result,
                itemEventMessage = if (it.isArabic) result.summaryAr else result.summaryEn,
                isGlitchingActive = result.levelsGainedOrLost < 0,
                isHealingActive = result.levelsGainedOrLost > 0
            )
        }

        if (result.levelsGainedOrLost < 0) {
            scheduleGlitchReset()
        } else if (result.levelsGainedOrLost > 0) {
            scheduleHealingReset()
        }

        evaluateQuestProgress(
            thresholds = adjustedThresholds,
            streakDays = newStreak,
            isEndOfDayEvaluation = true,
            dailyOutcome = result
        )
    }

    fun dismissDailyOutcome() {
        _uiState.update { it.copy(latestDailyOutcome = null) }
    }

    // --- Demo & Testing Helpers ---
    fun simulateAddStreakDay() {
        val newStreak = _uiState.value.streakDays + 1
        repository.setStreakDays(newStreak)
        val nextMilestone = ((newStreak / 5) + 1) * 5
        val canClaim = newStreak >= 5 && (newStreak % 5 == 0 || newStreak > _uiState.value.lastClaimedMilestoneStreak)
        _uiState.update {
            it.copy(
                streakDays = newStreak,
                isMilestoneRewardClaimable = canClaim,
                nextMilestoneTarget = nextMilestone
            )
        }
        evaluateQuestProgress(
            thresholds = _uiState.value.thresholds,
            streakDays = newStreak,
            isEndOfDayEvaluation = false
        )
    }

    fun simulateTimeUsage(packageName: String, minutes: Int) {
        val currentList = _uiState.value.thresholds.toMutableList()
        val index = currentList.indexOfFirst { it.packageName == packageName }
        if (index >= 0) {
            val old = currentList[index]
            val updated = old.copy(currentMinutes = old.currentMinutes + minutes)
            currentList[index] = updated
            repository.saveAppThresholds(currentList)
            _uiState.update { it.copy(thresholds = currentList) }
            if (updated.isBreached && !old.isBreached) {
                executeBreachPenalty(updated)
            }
            evaluateQuestProgress(
                thresholds = currentList,
                streakDays = _uiState.value.streakDays,
                isEndOfDayEvaluation = false
            )
        }
    }

    // Theme & Language
    fun setTheme(theme: ThemeType) {
        repository.setSelectedTheme(theme)
        _uiState.update { it.copy(selectedTheme = theme) }
    }

    fun setDarkMode(isDark: Boolean) {
        repository.setDarkMode(isDark)
        _uiState.update { it.copy(isDarkMode = isDark) }
    }

    fun setLanguage(isArabic: Boolean) {
        repository.setArabic(isArabic)
        val title = HabitPulseEngine.getRankTitle(_uiState.value.brainLevel, isArabic)
        _uiState.update { it.copy(isArabic = isArabic, rankTitle = title) }
    }

    fun hatchEgg(tier: EggTier) {
        val state = _uiState.value
        if (state.streakDays < tier.requiredStreakDays || state.isEggBlockedToday) return
        val eggState = state.eggStates[tier]
        if (eggState?.isHatched == true) return

        val rewardMessage = when (tier) {
            EggTier.BRONZE -> {
                addXp(15_000L, "Bronze Egg Hatch")
                repository.addItem(InventoryItemType.DAILY_QUEST_SKIP_KEY, 1)
                "Unlocked Theme: Emerald Glass & +1 Daily Quest Skip Key"
            }
            EggTier.SILVER -> {
                addXp(35_000L, "Silver Egg Hatch")
                repository.addItem(InventoryItemType.SHIELD_24H, 1)
                "Acquired +1 24-Hour Shield & Theme: Cyber Neon Glass"
            }
            EggTier.GOLD_DRAGON -> {
                addXp(75_000L, "Dragon Egg Hatch")
                repository.addItem(InventoryItemType.SHIELD_1WEEK, 1)
                repository.addItem(InventoryItemType.LEGENDARY_POTION, 1)
                "Dragon Fortress Master: +1 Week Aegis Shield & +1 Legendary Potion!"
            }
        }

        repository.markEggHatched(tier, rewardMessage)
        val updatedEggs = repository.getEggStates()
        val updatedInv = repository.getInventory()
        _uiState.update {
            it.copy(
                eggStates = updatedEggs,
                inventory = updatedInv,
                shieldCount = computeTotalShields(updatedInv),
                itemEventMessage = rewardMessage
            )
        }
    }

    fun triggerDetoxHealing() {
        healBrainRot(15f)
        addXp(10_000L, "Adherence Detox")
        _uiState.update { it.copy(isHealingActive = true) }
        scheduleHealingReset()
    }

    fun simulateRotSpike(amount: Float = 20f) {
        increaseBrainRot(amount)
    }

    fun toggleDarkMode() {
        setDarkMode(!_uiState.value.isDarkMode)
    }

    fun simulateLaunch(packageName: String) {
        val currentList = _uiState.value.thresholds.toMutableList()
        val index = currentList.indexOfFirst { it.packageName == packageName }
        if (index >= 0) {
            val old = currentList[index]
            val updated = old.copy(currentLaunches = old.currentLaunches + 1)
            currentList[index] = updated
            repository.saveAppThresholds(currentList)
            _uiState.update { it.copy(thresholds = currentList) }
            if (updated.isBreached && !old.isBreached) {
                executeBreachPenalty(updated)
            }
        }
    }

    fun simulateMinutes(packageName: String, additionalMinutes: Int) {
        simulateTimeUsage(packageName, additionalMinutes)
    }

    fun addOrUpdateThreshold(
        packageName: String,
        appName: String,
        maxMinutes: Int,
        maxLaunches: Int,
        category: String = "Social"
    ) {
        val currentList = repository.getAppThresholds().toMutableList()
        val existingIndex = currentList.indexOfFirst { it.packageName == packageName }

        val item = if (existingIndex >= 0) {
            currentList[existingIndex].copy(
                appName = appName,
                maxMinutes = maxMinutes,
                maxLaunches = maxLaunches,
                category = category
            )
        } else {
            AppThreshold(
                packageName = packageName,
                appName = appName,
                maxMinutes = maxMinutes,
                maxLaunches = maxLaunches,
                category = category
            )
        }

        if (existingIndex >= 0) {
            currentList[existingIndex] = item
        } else {
            currentList.add(item)
        }

        repository.saveAppThresholds(currentList)
        val adjusted = applyDynamicShrinkingLimits(currentList, _uiState.value.brainLevel)
        _uiState.update { it.copy(thresholds = adjusted) }
    }

    fun deleteThreshold(packageName: String) {
        val currentList = repository.getAppThresholds().filterNot { it.packageName == packageName }
        repository.saveAppThresholds(currentList)
        val adjusted = applyDynamicShrinkingLimits(currentList, _uiState.value.brainLevel)
        _uiState.update { it.copy(thresholds = adjusted) }
    }

    fun resetDailyStats() {
        repository.setDailyBreachesCount(0)
        repository.setFlameScore(100)
        val resetThresholds = repository.getAppThresholds().map {
            it.copy(currentMinutes = 0, currentLaunches = 0)
        }
        repository.saveAppThresholds(resetThresholds)
        val adjusted = applyDynamicShrinkingLimits(resetThresholds, _uiState.value.brainLevel)
        _uiState.update {
            it.copy(
                dailyBreachesCount = 0,
                flameScore = 100,
                isEggBlockedToday = false,
                thresholds = adjusted,
                activeShieldEvent = null
            )
        }
    }

    /**
     * Resets the entire brain level, rewards, inventory items, eggs, and streaks back to initial clean state.
     */
    fun resetBrainLevel() {
        val wasArabic = _uiState.value.isArabic
        val wasDark = _uiState.value.isDarkMode
        val theme = _uiState.value.selectedTheme

        repository.clearAllUserData()
        repository.setArabic(wasArabic)
        repository.setDarkMode(wasDark)
        repository.setSelectedTheme(theme)
        repository.setFlameScore(100)
        repository.setStreakDays(0)
        repository.setTotalXp(0L)
        repository.setBrainRotPercentage(0f)

        val newLevel = 1
        val isAr = wasArabic
        val title = HabitPulseEngine.getRankTitle(newLevel, isAr)
        val badge = HabitPulseEngine.getRankBadgeEmoji(newLevel)

        val resetThresholds = repository.getAppThresholds().map {
            it.copy(currentMinutes = 0, currentLaunches = 0)
        }
        val adjusted = applyDynamicShrinkingLimits(resetThresholds, newLevel)
        repository.saveAppThresholds(adjusted)

        _uiState.update {
            it.copy(
                brainLevel = newLevel,
                totalXp = 0L,
                totalPoints = 0,
                pointsInCurrentLevel = 0,
                levelProgress = 0f,
                rankTitle = title,
                rankBadge = badge,
                brainRotPercentage = 0f,
                dailyBreachesCount = 0,
                flameScore = 100,
                streakDays = 0,
                shieldCount = 0,
                inventory = emptyMap(),
                eggStates = repository.getEggStates(),
                dailyBufferPool = HabitPulseEngine.DAILY_BUFFER_POOL_SIZE,
                thresholds = adjusted,
                activeShieldEvent = null,
                isMilestoneRewardClaimable = false,
                lastClaimedMilestoneStreak = 0,
                dailyQuests = HabitPulseEngine.generateDailyQuests(newLevel),
                weeklyQuests = HabitPulseEngine.generateWeeklyQuests(newLevel),
                isDailyEggAvailable = false,
                isWeeklyEggAvailable = false,
                itemEventMessage = if (isAr) "تم تصفير كافة المستويات والجوائز والتقدم بنجاح! لتبدأ اللعبة الحقيقية 🧠✨" else "All levels, rewards & items reset to Level 1 (0 XP) successfully! 🧠✨"
            )
        }
    }

    // --- Neural Inventory: Item Exchange & Fusion System ---
    fun fuseDailyKeysToWeeklyKey(): Boolean {
        val dailyKeys = _uiState.value.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0
        if (dailyKeys >= 3) {
            repository.setItemCount(InventoryItemType.DAILY_QUEST_SKIP_KEY, dailyKeys - 3)
            repository.addItem(InventoryItemType.WEEKLY_QUEST_SKIP_KEY, 1)
            val updatedInv = repository.getInventory()
            _uiState.update {
                it.copy(
                    inventory = updatedInv,
                    shieldCount = computeTotalShields(updatedInv),
                    itemEventMessage = if (it.isArabic) "🔑 تم الدمج بنجاح: استبدال 3 مفاتيح يومية بمفتاح أسبوعي نادر!" else "🔑 Neural Fusion Success: 3 Daily Keys fused into 1 Weekly Quest Key!"
                )
            }
            return true
        }
        return false
    }

    fun synthesizeHealingPotion(): Boolean {
        val weeklyKeys = _uiState.value.inventory[InventoryItemType.WEEKLY_QUEST_SKIP_KEY] ?: 0
        if (weeklyKeys >= 2) {
            repository.setItemCount(InventoryItemType.WEEKLY_QUEST_SKIP_KEY, weeklyKeys - 2)
            repository.addItem(InventoryItemType.BRAIN_HEALING_POTION, 1)
            val updatedInv = repository.getInventory()
            _uiState.update {
                it.copy(
                    inventory = updatedInv,
                    shieldCount = computeTotalShields(updatedInv),
                    itemEventMessage = if (it.isArabic) "🧪 تم التخليق بنجاح: إضافة 1 جرعة تعافي ذهني مقابل مفتاحين أسبوعيين!" else "🧪 Synthesized: 1 Brain Healing Potion crafted from 2 Weekly Keys!"
                )
            }
            return true
        }
        return false
    }

    fun forgeShield(): Boolean {
        val dailyKeys = _uiState.value.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0
        if (dailyKeys >= 2) {
            repository.setItemCount(InventoryItemType.DAILY_QUEST_SKIP_KEY, dailyKeys - 2)
            repository.addItem(InventoryItemType.SHIELD_24H, 1)
            val updatedInv = repository.getInventory()
            _uiState.update {
                it.copy(
                    inventory = updatedInv,
                    shieldCount = computeTotalShields(updatedInv),
                    itemEventMessage = if (it.isArabic) "🛡️ تمت صياغة الدرع: إضافة درع حماية 24 ساعة إلى المستودع!" else "🛡️ Aegis Defense Forged: 1 24-Hour Shield added to inventory!"
                )
            }
            return true
        }
        return false
    }

    fun craftMysteryBox(): Boolean {
        val currentXp = repository.getTotalXp()
        if (currentXp >= 15_000L) {
            addXp(-15_000L, "Craft Mystery Box")
            repository.addItem(InventoryItemType.MYSTERY_BOX, 1)
            val updatedInv = repository.getInventory()
            _uiState.update {
                it.copy(
                    inventory = updatedInv,
                    shieldCount = computeTotalShields(updatedInv),
                    itemEventMessage = if (it.isArabic) "🎁 تم فتح الخزينة: إضافة 1 صندوق سايبر غامض مقابل 15,000 XP!" else "🎁 Vault Unlocked: 1 Mystery Cyber Box acquired for 15,000 XP!"
                )
            }
            return true
        }
        return false
    }

    // --- Interactive Tutorial Overlay Controls ---
    fun openTutorial(step: Int = 0) {
        _uiState.update {
            it.copy(
                isTutorialVisible = true,
                tutorialCurrentStep = step.coerceIn(0, 6)
            )
        }
    }

    fun dismissTutorial() {
        _uiState.update { it.copy(isTutorialVisible = false) }
        repository.setTutorialCompleted(true)
    }

    fun completeTutorial() {
        _uiState.update { it.copy(isTutorialVisible = false) }
        repository.setTutorialCompleted(true)
    }

    fun nextTutorialStep(totalSteps: Int = 7) {
        _uiState.update {
            val next = it.tutorialCurrentStep + 1
            if (next >= totalSteps) {
                repository.setTutorialCompleted(true)
                it.copy(isTutorialVisible = false)
            } else {
                it.copy(tutorialCurrentStep = next)
            }
        }
    }

    fun previousTutorialStep() {
        _uiState.update {
            val prev = (it.tutorialCurrentStep - 1).coerceAtLeast(0)
            it.copy(tutorialCurrentStep = prev)
        }
    }

    fun setTutorialStep(step: Int) {
        _uiState.update {
            it.copy(tutorialCurrentStep = step.coerceIn(0, 6))
        }
    }

    // --- Auto-Update System (https://github.com/charfeddine23/Brain) ---
    fun checkForUpdatesOnAppEnter() {
        val state = _uiState.value
        if (state.isCheckingUpdate || state.isDownloadingUpdate || state.needsInstallPermission) return
        checkForUpdates(isManual = false)
    }

    fun checkForUpdates(isManual: Boolean = false) {
        if (_uiState.value.isCheckingUpdate || _uiState.value.isDownloadingUpdate) return

        viewModelScope.launch {
            val isAr = _uiState.value.isArabic
            _uiState.update {
                it.copy(
                    isCheckingUpdate = true,
                    updateErrorMessage = null,
                    updateStatusMessage = if (isManual) {
                        if (isAr) "جاري البحث عن تحديثات في GitHub..." else "Checking GitHub Releases for updates..."
                    } else it.updateStatusMessage
                )
            }

            when (val result = updaterManager.checkForUpdates(BuildConfig.VERSION_NAME)) {
                is UpdateCheckResult.UpdateAvailable -> {
                    _uiState.update {
                        it.copy(
                            isCheckingUpdate = false,
                            availableUpdate = result.info,
                            isUpdateDialogVisible = true,
                            updateErrorMessage = null,
                            updateStatusMessage = if (isAr) {
                                "يتوفر إصدار جديد v${result.info.versionName}!"
                            } else {
                                "New update v${result.info.versionName} available!"
                            }
                        )
                    }
                }
                is UpdateCheckResult.UpToDate -> {
                    _uiState.update {
                        it.copy(
                            isCheckingUpdate = false,
                            availableUpdate = null,
                            updateErrorMessage = null,
                            updateStatusMessage = if (isManual) {
                                if (isAr) "أنت تستخدم أحدث إصدار بالفعل (v${result.currentVersion}) ✓" else "You are already on the latest version (v${result.currentVersion}) ✓"
                            } else null
                        )
                    }
                }
                is UpdateCheckResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isCheckingUpdate = false,
                            updateStatusMessage = if (isManual) {
                                if (isAr) "تعذر الاتصال بـ GitHub: ${result.message}" else "Update check failed: ${result.message}"
                            } else it.updateStatusMessage
                        )
                    }
                }
            }
        }
    }

    fun openUpdateDialog() {
        if (_uiState.value.availableUpdate != null) {
            _uiState.update { it.copy(isUpdateDialogVisible = true, updateErrorMessage = null) }
        } else {
            checkForUpdates(isManual = true)
        }
    }

    fun dismissUpdateDialog() {
        if (!_uiState.value.isDownloadingUpdate) {
            _uiState.update {
                it.copy(
                    isUpdateDialogVisible = false,
                    needsInstallPermission = false,
                    updateErrorMessage = null
                )
            }
        }
    }

    fun startUpdateDownloadAndInstall() {
        val update = _uiState.value.availableUpdate ?: return
        if (_uiState.value.isDownloadingUpdate) return

        viewModelScope.launch {
            val isAr = _uiState.value.isArabic
            _uiState.update {
                it.copy(
                    isDownloadingUpdate = true,
                    updateDownloadProgress = 0,
                    updateErrorMessage = null,
                    needsInstallPermission = false
                )
            }

            val downloadResult = updaterManager.downloadApk(update) { pct ->
                _uiState.update { it.copy(updateDownloadProgress = pct) }
            }

            downloadResult.fold(
                onSuccess = { apkFile ->
                    _uiState.update {
                        it.copy(
                            isDownloadingUpdate = false,
                            updateDownloadProgress = 100,
                            pendingInstallApkPath = apkFile.absolutePath
                        )
                    }
                    when (val launchResult = updaterManager.launchApkInstaller(apkFile)) {
                        is InstallLaunchResult.LaunchedInstaller -> {
                            _uiState.update {
                                it.copy(
                                    needsInstallPermission = false,
                                    isUpdateDialogVisible = false
                                )
                            }
                        }
                        is InstallLaunchResult.NeedsUnknownSourcesPermission -> {
                            _uiState.update {
                                it.copy(needsInstallPermission = true)
                            }
                        }
                        is InstallLaunchResult.Failed -> {
                            _uiState.update {
                                it.copy(
                                    updateErrorMessage = if (isAr) "فشل بدء المثبّت: ${launchResult.error}" else "Failed to launch installer: ${launchResult.error}"
                                )
                            }
                        }
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isDownloadingUpdate = false,
                            updateErrorMessage = if (isAr) {
                                "تعذر تحميل ملف APK: ${err.localizedMessage ?: "خطأ في الشبكة"}"
                            } else {
                                "Download failed: ${err.localizedMessage ?: "Network error"}"
                            }
                        )
                    }
                }
            )
        }
    }

    fun retryPendingInstallIfPermitted() {
        val apkPath = _uiState.value.pendingInstallApkPath ?: return
        if (!_uiState.value.needsInstallPermission) return
        if (!updaterManager.canRequestPackageInstalls()) return

        val apkFile = File(apkPath)
        if (apkFile.exists()) {
            when (updaterManager.launchApkInstaller(apkFile)) {
                is InstallLaunchResult.LaunchedInstaller -> {
                    _uiState.update {
                        it.copy(
                            needsInstallPermission = false,
                            isUpdateDialogVisible = false,
                            pendingInstallApkPath = null
                        )
                    }
                }
                else -> Unit
            }
        }
    }

    fun getInstallPermissionIntent(): Intent = updaterManager.createUnknownSourcesPermissionIntent()

    fun getReleasesBrowserIntent(): Intent = updaterManager.createReleasesBrowserIntent(
        _uiState.value.availableUpdate?.htmlUrl ?: AppUpdaterManager.RELEASES_LATEST_URL
    )
}
