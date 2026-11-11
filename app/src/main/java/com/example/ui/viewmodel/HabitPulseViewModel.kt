package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitPulseRepository
import com.example.data.UsageTrackerManager
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
    val streakDays: Int = 5,
    val brainRotPercentage: Float = 15f,
    val isHealingActive: Boolean = false,
    val isGlitchingActive: Boolean = false,
    val currentLevel: Int = 1,
    val levelTitle: String = "Active Mind",
    val levelBadge: String = "🌱",

    // Shield Immunity System
    val activeImmunityUntilMillis: Long = 0L,
    val isImmunityActive: Boolean = false,
    val immunityRemainingHours: Int = 0,
    val shieldCount: Int = 1, // legacy/quick-access shields count

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
    val tutorialCurrentStep: Int = 0
)

class HabitPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HabitPulseRepository(application)
    private val usageTracker = UsageTrackerManager(application)

    private val _uiState = MutableStateFlow(HabitPulseUiState())
    val uiState: StateFlow<HabitPulseUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
        refreshUsageStats()
        loadInstalledApps()
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
                dailyBufferPool = HabitPulseEngine.DAILY_BUFFER_POOL_SIZE,
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
     * Queries verified foreground usage under the 1-Minute Anti-Cheat Rule.
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
     * STREAK, DEMOTION, AND SHIELD IMMUNITY SYSTEM:
     * - If active shield / immunity is ON: Absorbs breach (0 Level lost, streak saved, 0 rot added)!
     * - If shield available in inventory: Auto-triggers, consumes 1 shield, grants 24-hr immunity, absorbs penalty!
     * - If unprotected:
     *     - Brain Demoted by 1 Level (-1 Level, min 1)!
     *     - Streak reset to 0!
     *     - Visual rot surge applied!
     *     - Flame score deducted (-10 first, -20 second, reset on 3rd).
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
            // Already under active shield barrier: 100% absorbed!
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

        if (has24hShield || has1wShield || legacyShields > 0) {
            // AUTO-ACTIVATE SHIELD FROM INVENTORY
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
            val remainingShields = (updatedInv[InventoryItemType.SHIELD_24H] ?: 0) + (updatedInv[InventoryItemType.SHIELD_1WEEK] ?: 0)

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

            // Shield Defense Bonus XP
            addXp(2500L, "Shield Defense")

            _uiState.update {
                it.copy(
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
            // UNPROTECTED DEMOTION PENALTY
            val currentLevel = _uiState.value.brainLevel
            val demotedLevel = (currentLevel - 1).coerceAtLeast(1)
            val newXp = HabitPulseEngine.getXpForLevel(demotedLevel)
            repository.setTotalXp(newXp)

            // Reset Streak to 0!
            repository.setStreakDays(0)

            // Flame deduction
            val penaltyPoints = when (currentBreaches) {
                1 -> 10
                2 -> 20
                else -> 40
            }
            val newFlame = (repository.getFlameScore() - penaltyPoints).coerceAtLeast(0)
            repository.setFlameScore(newFlame)

            // Visual Rot Surge
            increaseBrainRot(30f)

            val log = BreachRecord(
                id = UUID.randomUUID().toString(),
                packageName = threshold.packageName,
                appName = threshold.appName,
                timestamp = System.currentTimeMillis(),
                breachIndexToday = currentBreaches,
                penaltyDeducted = penaltyPoints,
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
                    rankTitle = title,
                    rankBadge = badge,
                    flameScore = newFlame,
                    streakDays = 0,
                    dailyBreachesCount = currentBreaches,
                    isEggBlockedToday = true,
                    isGlitchingActive = true,
                    breachLogs = repository.getBreachRecords(),
                    activeShieldEvent = ShieldEvent.PenaltyApplied(threshold.appName, currentBreaches, penaltyPoints, true)
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

    /**
     * Consumes an item from the 7 reward item types in Inventory.
     */
    fun useInventoryItem(itemType: InventoryItemType) {
        val count = _uiState.value.inventory[itemType] ?: 0
        if (count <= 0) return

        repository.consumeItem(itemType)
        val updatedInv = repository.getInventory()

        when (itemType) {
            InventoryItemType.DAILY_QUEST_SKIP_KEY -> {
                // Auto-completes the first incomplete daily quest
                val quests = _uiState.value.dailyQuests.toMutableList()
                val idx = quests.indexOfFirst { !it.isCompleted }
                if (idx >= 0) {
                    quests[idx] = quests[idx].copy(isCompleted = true, currentProgress = quests[idx].targetProgress)
                    addXp(quests[idx].xpReward, "Daily Quest Skip")
                }
                val allDailyDone = quests.all { it.isCompleted }
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        dailyQuests = quests,
                        isDailyEggAvailable = allDailyDone,
                        itemEventMessage = "Daily Quest Key used: Auto-completed quest!"
                    )
                }
            }
            InventoryItemType.WEEKLY_QUEST_SKIP_KEY -> {
                // Auto-completes the first incomplete weekly quest
                val quests = _uiState.value.weeklyQuests.toMutableList()
                val idx = quests.indexOfFirst { !it.isCompleted }
                if (idx >= 0) {
                    quests[idx] = quests[idx].copy(isCompleted = true, currentProgress = quests[idx].targetProgress)
                    addXp(quests[idx].xpReward, "Weekly Quest Skip")
                }
                val allWeeklyDone = quests.all { it.isCompleted }
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        weeklyQuests = quests,
                        isWeeklyEggAvailable = allWeeklyDone,
                        itemEventMessage = "Weekly Quest Key used: Auto-completed hardcore quest!"
                    )
                }
            }
            InventoryItemType.BRAIN_HEALING_POTION -> {
                // Instantly elevates brain by +1 Level and purges rot
                elevateBrainLevelByOne()
                healBrainRot(35f)
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        isHealingActive = true,
                        itemEventMessage = "Brain Healing Potion drunk: +1 Brain Level & Rot Purged!"
                    )
                }
                scheduleHealingReset()
            }
            InventoryItemType.LEGENDARY_POTION -> {
                // +1 Brain Level + Auto-completes today's daily reward & weekly reward!
                elevateBrainLevelByOne()
                healBrainRot(100f)
                val completedDaily = _uiState.value.dailyQuests.map { it.copy(isCompleted = true, currentProgress = it.targetProgress) }
                val completedWeekly = _uiState.value.weeklyQuests.map { it.copy(isCompleted = true, currentProgress = it.targetProgress) }
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        dailyQuests = completedDaily,
                        weeklyQuests = completedWeekly,
                        isDailyEggAvailable = true,
                        isWeeklyEggAvailable = true,
                        isHealingActive = true,
                        itemEventMessage = "LEGENDARY POTION CONSUMED: +1 Level, All Quests Auto-Completed!"
                    )
                }
                scheduleHealingReset()
            }
            InventoryItemType.SHIELD_24H -> {
                val immunityUntil = System.currentTimeMillis() + (24 * 3600 * 1000L)
                repository.setActiveImmunityUntil(immunityUntil)
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        activeImmunityUntilMillis = immunityUntil,
                        isImmunityActive = true,
                        immunityRemainingHours = 24,
                        itemEventMessage = "24-Hour Shield Activated: Demotion Immunity Active!"
                    )
                }
            }
            InventoryItemType.SHIELD_1WEEK -> {
                val immunityUntil = System.currentTimeMillis() + (7 * 24 * 3600 * 1000L)
                repository.setActiveImmunityUntil(immunityUntil)
                _uiState.update {
                    it.copy(
                        inventory = updatedInv,
                        activeImmunityUntilMillis = immunityUntil,
                        isImmunityActive = true,
                        immunityRemainingHours = 168,
                        itemEventMessage = "1-Week Aegis Fortress Activated: 7 Days of Immunity!"
                    )
                }
            }
            InventoryItemType.MYSTERY_BOX -> {
                // Rolls mystery loot
                val droppedItem = HabitPulseEngine.openMysteryBoxLoot()
                repository.addItem(droppedItem, 1)
                val refreshedInv = repository.getInventory()
                _uiState.update {
                    it.copy(
                        inventory = refreshedInv,
                        itemEventMessage = "Mystery Box opened! Discovered: ${droppedItem.iconEmoji} ${if (it.isArabic) droppedItem.titleAr else droppedItem.titleEn}"
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

        val title = HabitPulseEngine.getRankTitle(nextLevel, _uiState.value.isArabic)
        val badge = HabitPulseEngine.getRankBadgeEmoji(nextLevel)

        _uiState.update {
            it.copy(
                brainLevel = nextLevel,
                totalXp = targetXp,
                rankTitle = title,
                rankBadge = badge,
                levelProgress = 0f
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
                lastClaimedMilestoneStreak = streak,
                isMilestoneRewardClaimable = false,
                nextMilestoneTarget = nextMilestone,
                itemEventMessage = "5-Day Streak Milestone Claimed! You received: ${rolledReward.iconEmoji} ${if (it.isArabic) rolledReward.titleAr else rolledReward.titleEn}"
            )
        }
    }

    fun completeQuestManually(questId: String, isWeekly: Boolean) {
        if (isWeekly) {
            val list = _uiState.value.weeklyQuests.toMutableList()
            val idx = list.indexOfFirst { it.id == questId }
            if (idx >= 0 && !list[idx].isCompleted) {
                list[idx] = list[idx].copy(isCompleted = true, currentProgress = list[idx].targetProgress)
                addXp(list[idx].xpReward, "Weekly Quest Complete")
                val allDone = list.all { it.isCompleted }
                _uiState.update { it.copy(weeklyQuests = list, isWeeklyEggAvailable = allDone) }
            }
        } else {
            val list = _uiState.value.dailyQuests.toMutableList()
            val idx = list.indexOfFirst { it.id == questId }
            if (idx >= 0 && !list[idx].isCompleted) {
                list[idx] = list[idx].copy(isCompleted = true, currentProgress = list[idx].targetProgress)
                addXp(list[idx].xpReward, "Daily Quest Complete")
                val allDone = list.all { it.isCompleted }
                _uiState.update { it.copy(dailyQuests = list, isDailyEggAvailable = allDone) }
            }
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
            it.copy(
                brainLevel = newLevel,
                totalXp = newXp,
                totalPoints = totalPts,
                pointsInCurrentLevel = ptsInLevel,
                levelProgress = prog,
                rankTitle = title,
                rankBadge = badge
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
     * Triggers the 3 Daily Evaluation Outcomes:
     * - Case A (Clean Sweep): 0 breaches -> Dynamic level-up, Streak +1, Escalating streak bonus.
     * - Case B (Single Breach): 1 app breach -> Streak frozen (no reset), +500 safety points.
     * - Case C (Multi-Breach): 2+ breaches -> Streak reset to 0, 1000-pt buffer pool deduction (-50 pts/10min).
     *     - If buffer reaches 0: Shield auto-consumes OR unrestricted multi-level demotion!
     */
    fun triggerDailyEvaluation() {
        val thresholds = _uiState.value.thresholds
        val breachedApps = thresholds.filter { it.isBreached }
        val currentStreak = _uiState.value.streakDays
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

        // Apply state updates based on outcome
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

        val updatedPoints = (currentTotalPoints + result.pointsAwardedOrDeducted).coerceIn(0, HabitPulseEngine.MAX_POINTS)
        repository.setTotalXp(updatedPoints.toLong())

        val newStreak = if (result.isStreakReset) 0 else (currentStreak + result.streakDelta)
        repository.setStreakDays(newStreak)

        val newLevel = HabitPulseEngine.calculateLevelFromPoints(updatedPoints)
        val ptsInLevel = updatedPoints % HabitPulseEngine.POINTS_PER_LEVEL
        val prog = HabitPulseEngine.calculateLevelProgress(updatedPoints)
        val title = HabitPulseEngine.getRankTitle(newLevel, _uiState.value.isArabic)
        val badge = HabitPulseEngine.getRankBadgeEmoji(newLevel)

        val updatedInv = repository.getInventory()
        val totalShields = (updatedInv[InventoryItemType.SHIELD_24H] ?: 0) + (updatedInv[InventoryItemType.SHIELD_1WEEK] ?: 0)

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
                inventory = updatedInv,
                shieldCount = totalShields.coerceAtLeast(repository.getShieldCount()),
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
     * Resets the entire brain level back to Level 1 (0 XP) and resets thresholds/pools accordingly.
     */
    fun resetBrainLevel() {
        repository.setTotalXp(0L)
        repository.setBrainRotPercentage(10f)
        repository.setDailyBreachesCount(0)
        repository.setFlameScore(100)
        repository.setStreakDays(0)

        val newLevel = 1
        val isAr = _uiState.value.isArabic
        val title = HabitPulseEngine.getRankTitle(newLevel, isAr)
        val badge = HabitPulseEngine.getRankBadgeEmoji(newLevel)

        val currentThresholds = repository.getAppThresholds()
        val adjusted = applyDynamicShrinkingLimits(currentThresholds, newLevel)
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
                brainRotPercentage = 10f,
                dailyBreachesCount = 0,
                flameScore = 100,
                streakDays = 0,
                dailyBufferPool = HabitPulseEngine.DAILY_BUFFER_POOL_SIZE,
                thresholds = adjusted,
                activeShieldEvent = null,
                itemEventMessage = if (isAr) "تم تصفير المستوى وإعادة ضبط الدماغ إلى المستوى 1 بنجاح 🧠" else "Brain Level reset to Level 1 (0 XP) successfully 🧠"
            )
        }
    }

    // --- The Brain Clinic: Item Exchange & Fusion System ---
    fun fuseDailyKeysToWeeklyKey(): Boolean {
        val dailyKeys = _uiState.value.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0
        if (dailyKeys >= 3) {
            repository.setItemCount(InventoryItemType.DAILY_QUEST_SKIP_KEY, dailyKeys - 3)
            repository.addItem(InventoryItemType.WEEKLY_QUEST_SKIP_KEY, 1)
            val updatedInv = repository.getInventory()
            _uiState.update {
                it.copy(
                    inventory = updatedInv,
                    itemEventMessage = "Neural Fusion Success: 3 Daily Keys fused into 1 Weekly Quest Key! 🔑"
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
                    itemEventMessage = "Synthesized: 1 Brain Healing Potion crafted from 2 Weekly Keys! 🧪"
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
                    itemEventMessage = "Aegis Defense Forged: 1 24-Hour Shield added to inventory! 🛡️"
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
                    itemEventMessage = "Vault Unlocked: 1 Mystery Cyber Box acquired for 15,000 XP! 🎁"
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
}
