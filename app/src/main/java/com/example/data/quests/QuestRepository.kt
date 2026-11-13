package com.example.data.quests

import com.example.data.HabitPulseRepository
import com.example.model.HabitPulseEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed class ClaimQuestResult {
    data class Success(
        val quest: QuestEntity,
        val xpAwarded: Long,
        val newTotalXp: Long,
        val newLevel: Int
    ) : ClaimQuestResult()

    data object AlreadyClaimed : ClaimQuestResult()
    data object NotCompleted : ClaimQuestResult()
    data object NotFound : ClaimQuestResult()
}

class QuestRepository(
    private val questDao: QuestDao,
    private val habitPulseRepository: HabitPulseRepository
) {
    val allQuests: Flow<List<QuestEntity>> = questDao.observeAllQuests()
    val activeDailyQuests: Flow<List<QuestEntity>> = questDao.observeActiveDailyQuests()
    val activeWeeklyQuests: Flow<List<QuestEntity>> = questDao.observeActiveWeeklyQuests()
    val allDailyQuests: Flow<List<QuestEntity>> = questDao.observeAllDailyQuests()
    val allWeeklyQuests: Flow<List<QuestEntity>> = questDao.observeAllWeeklyQuests()

    companion object {
        const val DAILY_ACTIVE_SLOTS = 3
        const val WEEKLY_ACTIVE_SLOTS = 3
    }

    suspend fun ensureInitializedAndRotated() = withContext(Dispatchers.IO) {
        QuestSeeder.seedIfEmpty(questDao)
        val todayKey = habitPulseRepository.getTodayKey()
        val weekKey = habitPulseRepository.getWeekKey()
        questDao.rotateDailyQuestsTransaction(cycleKey = todayKey, limit = DAILY_ACTIVE_SLOTS)
        questDao.rotateWeeklyQuestsTransaction(cycleKey = weekKey, limit = WEEKLY_ACTIVE_SLOTS)
    }

    suspend fun rerollUnclaimedDailyQuests(): List<QuestEntity> = withContext(Dispatchers.IO) {
        val todayKey = habitPulseRepository.getTodayKey()
        val currentActive = questDao.getActiveQuestsByFrequency(QuestFrequency.DAILY)
        val claimedToday = currentActive.filter { it.isClaimed }
        val slotsNeeded = (DAILY_ACTIVE_SLOTS - claimedToday.size).coerceAtLeast(0)
        if (slotsNeeded == 0) return@withContext currentActive

        val activeIds = currentActive.map { it.id }.toSet()
        questDao.deactivateAllByFrequency(QuestFrequency.DAILY)
        if (claimedToday.isNotEmpty()) {
            questDao.activateQuests(claimedToday.map { it.id }, todayKey)
        }
        val freshPicks = questDao.pickRandomDailyQuests(slotsNeeded + activeIds.size)
            .filter { it.id !in activeIds }
            .take(slotsNeeded)
        if (freshPicks.isNotEmpty()) {
            questDao.activateQuests(freshPicks.map { it.id }, todayKey)
        }
        questDao.getActiveQuestsByFrequency(QuestFrequency.DAILY)
    }

    suspend fun advanceQuestProgress(questId: String, step: Int = 1) = withContext(Dispatchers.IO) {
        val quest = questDao.getQuestById(questId) ?: return@withContext
        if (quest.isClaimed) return@withContext

        val safeStep = if (step <= 0) 1 else step
        val nextProgress = (quest.currentProgress + safeStep).coerceAtMost(quest.targetProgress)
        val completed = nextProgress >= quest.targetProgress
        questDao.updateQuestProgress(questId = quest.id, progress = nextProgress, completed = completed)
    }

    suspend fun setQuestProgress(questId: String, progress: Int) = withContext(Dispatchers.IO) {
        val quest = questDao.getQuestById(questId) ?: return@withContext
        if (quest.isClaimed) return@withContext

        val clamped = progress.coerceIn(0, quest.targetProgress)
        questDao.updateQuestProgress(
            questId = quest.id,
            progress = clamped,
            completed = clamped >= quest.targetProgress
        )
    }

    /**
     * Claims a completed quest reward with strict duplicate-claim protection:
     * 1. Rejects if quest is missing or not yet completed.
     * 2. Rejects if already claimed or already claimed in today's/this week's cycle.
     * 3. Uses atomic SQLite `claimQuestAtomically` (`WHERE isClaimed = 0`) to prevent double XP grants.
     */
    suspend fun claimQuestReward(questId: String): ClaimQuestResult = withContext(Dispatchers.IO) {
        val quest = questDao.getQuestById(questId) ?: return@withContext ClaimQuestResult.NotFound
        val currentCycleKey = when (quest.frequency) {
            QuestFrequency.DAILY -> habitPulseRepository.getTodayKey()
            QuestFrequency.WEEKLY -> habitPulseRepository.getWeekKey()
        }

        if (quest.isClaimed && quest.cycleKey == currentCycleKey) {
            return@withContext ClaimQuestResult.AlreadyClaimed
        }
        if (quest.isClaimed) {
            return@withContext ClaimQuestResult.AlreadyClaimed
        }
        if (!quest.isCompleted && quest.currentProgress < quest.targetProgress) {
            return@withContext ClaimQuestResult.NotCompleted
        }

        val now = System.currentTimeMillis()
        val updatedRows = questDao.claimQuestAtomically(
            questId = quest.id,
            cycleKey = currentCycleKey,
            claimedAtMillis = now
        )
        if (updatedRows == 0) {
            return@withContext ClaimQuestResult.AlreadyClaimed
        }

        val currentXp = habitPulseRepository.getTotalXp()
        val updatedXp = (currentXp + quest.xpReward).coerceIn(0L, HabitPulseEngine.MAX_XP)
        habitPulseRepository.setTotalXp(updatedXp)
        val updatedLevel = HabitPulseEngine.calculateLevel(updatedXp)

        ClaimQuestResult.Success(
            quest = quest.copy(
                isClaimed = true,
                isCompleted = true,
                currentProgress = quest.targetProgress,
                cycleKey = currentCycleKey,
                claimedAtMillis = now
            ),
            xpAwarded = quest.xpReward,
            newTotalXp = updatedXp,
            newLevel = updatedLevel
        )
    }

    fun getCurrentXp(): Long = habitPulseRepository.getTotalXp()

    fun getCurrentBrainLevel(): Int = HabitPulseEngine.calculateLevel(habitPulseRepository.getTotalXp())
}
