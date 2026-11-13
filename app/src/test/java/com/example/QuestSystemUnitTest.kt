package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.HabitPulseRepository
import com.example.data.quests.BrainPulseDatabase
import com.example.data.quests.ClaimQuestResult
import com.example.data.quests.QuestCategory
import com.example.data.quests.QuestFrequency
import com.example.data.quests.QuestRepository
import com.example.data.quests.QuestSeeder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuestSystemUnitTest {

    private lateinit var context: Context
    private lateinit var db: BrainPulseDatabase
    private lateinit var questRepository: QuestRepository
    private lateinit var habitPulseRepository: HabitPulseRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, BrainPulseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitPulseRepository = HabitPulseRepository(context)
        habitPulseRepository.clearAllUserData()
        questRepository = QuestRepository(db.questDao(), habitPulseRepository)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `seeder explicitly defines all 300 quests with unique IDs across all 8 categories`() {
        val daily = QuestSeeder.getAllDailyQuests()
        val weekly = QuestSeeder.getAllWeeklyQuests()
        val all = QuestSeeder.getAllQuests()

        assertEquals(200, daily.size)
        assertEquals(100, weekly.size)
        assertEquals(300, all.size)

        // All 300 IDs must be strictly unique
        assertEquals(300, all.map { it.id }.toSet().size)

        // Verify exact category distribution: 50 per daily category, 25 per weekly category
        assertEquals(50, daily.count { it.category == QuestCategory.DOPAMINE_FASTING })
        assertEquals(50, daily.count { it.category == QuestCategory.BUFFER_PROTECTION })
        assertEquals(50, daily.count { it.category == QuestCategory.KINETIC_ENERGY })
        assertEquals(50, daily.count { it.category == QuestCategory.ADVANCED_DISCIPLINE })

        assertEquals(25, weekly.count { it.category == QuestCategory.STREAK_GUARDIANS })
        assertEquals(25, weekly.count { it.category == QuestCategory.MYSTERY_BOX_HUNTERS })
        assertEquals(25, weekly.count { it.category == QuestCategory.DEEP_FASTING })
        assertEquals(25, weekly.count { it.category == QuestCategory.NEURAL_UPGRADES })

        assertTrue(all.all { it.title.isNotBlank() && it.description.isNotBlank() && it.xpReward > 0L && it.targetProgress > 0 })
    }

    @Test
    fun `room seeds 300 quests, rotates 3 daily and 3 weekly active quests, and prevents duplicate claims`() = runTest {
        questRepository.ensureInitializedAndRotated()

        val allInDb = questRepository.allQuests.first()
        val activeDaily = questRepository.activeDailyQuests.first()
        val activeWeekly = questRepository.activeWeeklyQuests.first()

        assertEquals(300, allInDb.size)
        assertEquals(3, activeDaily.size)
        assertEquals(3, activeWeekly.size)
        assertTrue(activeDaily.all { it.frequency == QuestFrequency.DAILY })
        assertTrue(activeWeekly.all { it.frequency == QuestFrequency.WEEKLY })

        // Pick first daily quest and reject claiming before completion
        val targetQuest = activeDaily.first()
        val prematureResult = questRepository.claimQuestReward(targetQuest.id)
        assertTrue(prematureResult is ClaimQuestResult.NotCompleted)

        // Advance to completion and claim once -> XP granted
        questRepository.setQuestProgress(targetQuest.id, targetQuest.targetProgress)
        val firstClaim = questRepository.claimQuestReward(targetQuest.id)
        assertTrue(firstClaim is ClaimQuestResult.Success)
        val successClaim = firstClaim as ClaimQuestResult.Success
        assertEquals(targetQuest.xpReward, successClaim.newTotalXp)
        assertEquals(targetQuest.xpReward, habitPulseRepository.getTotalXp())

        // Duplicate claim attempt on the same quest must fail cleanly with zero extra XP
        val duplicateClaim = questRepository.claimQuestReward(targetQuest.id)
        assertTrue(duplicateClaim is ClaimQuestResult.AlreadyClaimed)
        assertEquals(targetQuest.xpReward, habitPulseRepository.getTotalXp())
    }
}
