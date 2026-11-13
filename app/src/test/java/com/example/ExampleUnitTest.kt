package com.example

import com.example.model.AppCategory
import com.example.model.AppThreshold
import com.example.model.DailyOutcomeType
import com.example.model.EggTier
import com.example.model.HabitPulseEngine
import com.example.model.InventoryItemType
import com.example.model.ThemeType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `1000-point level pool math verifies level 1 to 100`() {
        assertEquals(1, HabitPulseEngine.calculateLevelFromPoints(0))
        assertEquals(1, HabitPulseEngine.calculateLevelFromPoints(500))
        assertEquals(2, HabitPulseEngine.calculateLevelFromPoints(1000))
        assertEquals(2, HabitPulseEngine.calculateLevelFromPoints(1500))
        assertEquals(4, HabitPulseEngine.calculateLevelFromPoints(3500))
        assertEquals(100, HabitPulseEngine.calculateLevelFromPoints(99000))

        // Progress in level
        assertEquals(0.5f, HabitPulseEngine.calculateLevelProgress(500), 0.001f)
        assertEquals(0.5f, HabitPulseEngine.calculateLevelProgress(1500), 0.001f)
    }

    @Test
    fun `escalating daily streak bonuses verify all tiers`() {
        assertEquals(500, HabitPulseEngine.getEscalatingStreakBonus(1))
        assertEquals(500, HabitPulseEngine.getEscalatingStreakBonus(89))
        assertEquals(750, HabitPulseEngine.getEscalatingStreakBonus(90))
        assertEquals(750, HabitPulseEngine.getEscalatingStreakBonus(179))
        assertEquals(1000, HabitPulseEngine.getEscalatingStreakBonus(180))
        assertEquals(1000, HabitPulseEngine.getEscalatingStreakBonus(359))
        assertEquals(2000, HabitPulseEngine.getEscalatingStreakBonus(360))
        assertEquals(2000, HabitPulseEngine.getEscalatingStreakBonus(500))
    }

    @Test
    fun `Case A Clean Sweep awards dynamic level up, streak +1 and escalating bonus`() {
        val result = HabitPulseEngine.evaluateDailyOutcome(
            breachedApps = emptyList(),
            currentStreak = 10,
            currentTotalPoints = 1800,
            hasActiveShield = false
        )

        assertEquals(DailyOutcomeType.CLEAN_SWEEP, result.outcomeType)
        assertEquals(0, result.totalBreaches)
        assertEquals(1, result.streakDelta)
        assertEquals(500, result.pointsAwardedOrDeducted) // Tier 1 bonus for streak 11
        assertFalse(result.isStreakReset)
        // 1800 + 500 = 2300 pts -> Old Level = 2, New Level = 3 (+1 level)
        assertEquals(1, result.levelsGainedOrLost)
    }

    @Test
    fun `single breach deducted from 1000-pt buffer still awards +1 streak while buffer above 0`() {
        val singleBreach = listOf(
            AppThreshold(
                packageName = "com.instagram.android",
                appName = "Instagram",
                maxMinutes = 30,
                maxLaunches = 10,
                currentMinutes = 45, // 15m excess = 1 block = -50 pts from buffer
                currentLaunches = 5
            )
        )

        val result = HabitPulseEngine.evaluateDailyOutcome(
            breachedApps = singleBreach,
            currentStreak = 15,
            currentTotalPoints = 2000,
            hasActiveShield = false
        )

        assertEquals(DailyOutcomeType.SINGLE_BREACH, result.outcomeType)
        assertEquals(1, result.totalBreaches)
        assertEquals(1, result.streakDelta) // +1 streak day awarded because 950 > 0 in buffer
        assertFalse(result.isStreakReset)
        assertEquals(950, result.bufferPoolRemaining)
        assertEquals(0, result.pointsAwardedOrDeducted)
        assertEquals(0, result.levelsGainedOrLost)
        assertFalse(result.wasShieldConsumed)
    }

    @Test
    fun `multi-breach within 1000-pt buffer deducts from buffer and preserves +1 streak`() {
        val breaches = listOf(
            AppThreshold(packageName = "app1", appName = "App 1", maxMinutes = 20, maxLaunches = 10, currentMinutes = 30, currentLaunches = 5), // 10m excess -> -50 pts
            AppThreshold(packageName = "app2", appName = "App 2", maxMinutes = 20, maxLaunches = 10, currentMinutes = 40, currentLaunches = 5)  // 20m excess -> -100 pts
        )

        val result = HabitPulseEngine.evaluateDailyOutcome(
            breachedApps = breaches,
            currentStreak = 25,
            currentTotalPoints = 5000,
            hasActiveShield = false
        )

        assertEquals(DailyOutcomeType.MULTI_BREACH, result.outcomeType)
        assertEquals(2, result.totalBreaches)
        assertFalse(result.isStreakReset)
        assertEquals(1, result.streakDelta) // +1 streak because 850 > 0 remain in buffer
        assertEquals(0, result.pointsAwardedOrDeducted)
        assertEquals(850, result.bufferPoolRemaining) // 1000 - 150 = 850 pts
        assertEquals(0, result.levelsGainedOrLost)
        assertFalse(result.wasShieldConsumed)
    }

    @Test
    fun `exact 1000-pt buffer depletion resets streak to 0`() {
        // 200 mins excess across 2 apps = 20 blocks * 50 = 1000 pts deducted -> buffer reaches 0
        val drainingBreaches = listOf(
            AppThreshold(packageName = "app1", appName = "App 1", maxMinutes = 20, maxLaunches = 10, currentMinutes = 120, currentLaunches = 5),
            AppThreshold(packageName = "app2", appName = "App 2", maxMinutes = 20, maxLaunches = 10, currentMinutes = 120, currentLaunches = 5)
        )

        val result = HabitPulseEngine.evaluateDailyOutcome(
            breachedApps = drainingBreaches,
            currentStreak = 12,
            currentTotalPoints = 4000,
            hasActiveShield = false
        )

        assertEquals(0, result.bufferPoolRemaining)
        assertTrue(result.isStreakReset)
        assertEquals(-12, result.streakDelta)
    }

    @Test
    fun `heavy breach exceeding 1000-pt buffer with Shield resets streak and blocks demotion`() {
        val heavyBreaches = listOf(
            AppThreshold(packageName = "app1", appName = "App 1", maxMinutes = 20, maxLaunches = 10, currentMinutes = 120, currentLaunches = 5),
            AppThreshold(packageName = "app2", appName = "App 2", maxMinutes = 20, maxLaunches = 10, currentMinutes = 170, currentLaunches = 5)
        )

        val shieldedResult = HabitPulseEngine.evaluateDailyOutcome(
            breachedApps = heavyBreaches,
            currentStreak = 30,
            currentTotalPoints = 6000,
            hasActiveShield = true
        )

        assertEquals(DailyOutcomeType.MULTI_BREACH, shieldedResult.outcomeType)
        assertTrue(shieldedResult.wasShieldConsumed)
        assertTrue(shieldedResult.isStreakReset)
        assertEquals(0, shieldedResult.levelsGainedOrLost)
        assertEquals(0, shieldedResult.bufferPoolRemaining)
    }

    @Test
    fun `heavy breach exceeding 1000-pt buffer without Shield resets streak and triggers demotion`() {
        val heavyBreaches = listOf(
            AppThreshold(packageName = "app1", appName = "App 1", maxMinutes = 20, maxLaunches = 10, currentMinutes = 120, currentLaunches = 5),
            AppThreshold(packageName = "app2", appName = "App 2", maxMinutes = 20, maxLaunches = 10, currentMinutes = 170, currentLaunches = 5)
        )

        val unshieldedResult = HabitPulseEngine.evaluateDailyOutcome(
            breachedApps = heavyBreaches,
            currentStreak = 30,
            currentTotalPoints = 6000,
            hasActiveShield = false
        )

        assertEquals(DailyOutcomeType.MULTI_BREACH, unshieldedResult.outcomeType)
        assertFalse(unshieldedResult.wasShieldConsumed)
        assertTrue(unshieldedResult.isStreakReset)
        assertEquals(0, unshieldedResult.bufferPoolRemaining)
        assertTrue(unshieldedResult.levelsGainedOrLost < 0)
    }

    @Test
    fun `dynamic shrinking time rule scales from Level 1 down to Level 100`() {
        val socialLvl1 = HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.SOCIAL_MEDIA, 1)
        val gamesLvl1 = HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.ENTERTAINMENT_GAMES, 1)
        val prodLvl1 = HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.PRODUCTIVITY_TOOLS, 1)

        assertEquals(360, socialLvl1) // 6 hours
        assertEquals(360, gamesLvl1)  // 6 hours
        assertEquals(480, prodLvl1)  // 8 hours baseline

        val socialLvl100 = HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.SOCIAL_MEDIA, 100)
        val gamesLvl100 = HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.ENTERTAINMENT_GAMES, 100)
        val prodLvl100 = HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.PRODUCTIVITY_TOOLS, 100)

        assertEquals(20, socialLvl100) // Shrunk down to 20 minutes
        assertEquals(40, gamesLvl100)  // Shrunk down to 40 minutes
        assertEquals(60, prodLvl100)  // Disciplined 1-hour focused deep work
    }

    @Test
    fun `all foreground sessions including under 60 seconds accumulate accurately`() {
        val session1Millis = 25_000L
        val session2Millis = 40_000L
        val usageData = com.example.data.AppUsageData(
            packageName = "com.example.social",
            launches = 2,
            foregroundTimeMillis = session1Millis + session2Millis
        )
        assertEquals(65_000L, usageData.foregroundTimeMillis)
        assertEquals(1, usageData.foregroundMinutes)
    }

    @Test
    fun `7 reward items and 5-day milestone drops work reliably`() {
        assertEquals(7, InventoryItemType.entries.size)
        assertNotNull(InventoryItemType.DAILY_QUEST_SKIP_KEY)
        assertNotNull(InventoryItemType.WEEKLY_QUEST_SKIP_KEY)
        assertNotNull(InventoryItemType.BRAIN_HEALING_POTION)
        assertNotNull(InventoryItemType.LEGENDARY_POTION)
        assertNotNull(InventoryItemType.SHIELD_24H)
        assertNotNull(InventoryItemType.SHIELD_1WEEK)
        assertNotNull(InventoryItemType.MYSTERY_BOX)

        val dropDay5 = HabitPulseEngine.rollStreakMilestoneReward(5)
        assertTrue(InventoryItemType.entries.contains(dropDay5))

        repeat(200) {
            val boxLoot = HabitPulseEngine.openMysteryBoxLoot()
            assertTrue(InventoryItemType.entries.contains(boxLoot))
            assertFalse(boxLoot == InventoryItemType.MYSTERY_BOX)
        }
    }

    @Test
    fun `tutorial overlay mechanics count and steps validation`() {
        assertEquals(7, com.example.ui.components.TUTORIAL_TOTAL_STEPS)
    }

    @Test
    fun `app open count never triggers breach, warning, progress, or brain rot`() {
        val highOpensLowTime = AppThreshold(
            packageName = "com.instagram.android",
            appName = "Instagram",
            maxMinutes = 60,
            maxLaunches = 5,
            currentMinutes = 10,
            currentLaunches = 999,
            category = AppCategory.SOCIAL_MEDIA.name
        )

        assertFalse(highOpensLowTime.isLaunchBreached)
        assertFalse(highOpensLowTime.isBreached)
        assertFalse(highOpensLowTime.isWarning)
        assertEquals(10f / 60f, highOpensLowTime.usageProgress, 0.001f)

        val rot = com.example.model.BrainRotCalculator.calculateUsageRot(listOf(highOpensLowTime))
        assertEquals(0f, rot, 0.001f)
    }

    @Test
    fun `github updater version parsing and comparison handles Brain_v1_x_x releases`() {
        assertEquals("1.15.2", com.example.data.AppUpdaterManager.extractCleanVersion("v1.15.2"))
        assertEquals("1.15.2", com.example.data.AppUpdaterManager.extractCleanVersion("", "Brain.v1.15.2.apk"))

        assertTrue(com.example.data.AppUpdaterManager.isRemoteVersionNewer("1.0.0", "1.0.1"))
        assertTrue(com.example.data.AppUpdaterManager.isRemoteVersionNewer("1.9.5", "1.10.0"))
        assertFalse(com.example.data.AppUpdaterManager.isRemoteVersionNewer("1.15.2", "1.15.2"))
        assertFalse(com.example.data.AppUpdaterManager.isRemoteVersionNewer("1.15.2", "1.14.9"))
    }
}
