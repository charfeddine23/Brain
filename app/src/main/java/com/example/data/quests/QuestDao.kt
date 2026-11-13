package com.example.data.quests

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    @Query("SELECT * FROM quests ORDER BY id ASC")
    fun observeAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE frequency = 'DAILY' AND isActive = 1 ORDER BY isClaimed ASC, id ASC")
    fun observeActiveDailyQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE frequency = 'WEEKLY' AND isActive = 1 ORDER BY isClaimed ASC, id ASC")
    fun observeActiveWeeklyQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE frequency = 'DAILY' ORDER BY id ASC")
    fun observeAllDailyQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE frequency = 'WEEKLY' ORDER BY id ASC")
    fun observeAllWeeklyQuests(): Flow<List<QuestEntity>>

    @Query("SELECT COUNT(*) FROM quests")
    suspend fun getQuestCount(): Int

    @Query("SELECT * FROM quests WHERE id = :questId LIMIT 1")
    suspend fun getQuestById(questId: String): QuestEntity?

    @Query("SELECT * FROM quests WHERE frequency = :frequency AND isActive = 1")
    suspend fun getActiveQuestsByFrequency(frequency: QuestFrequency): List<QuestEntity>

    /**
     * Random Daily Rotation Query:
     * Picks up to [limit] un-claimed Daily Quests randomly from the 200-quest pool.
     */
    @Query("SELECT * FROM quests WHERE frequency = 'DAILY' AND isClaimed = 0 ORDER BY RANDOM() LIMIT :limit")
    suspend fun pickRandomDailyQuests(limit: Int = 3): List<QuestEntity>

    /**
     * Random Daily Fallback Query when the entire 200 pool has been claimed:
     */
    @Query("SELECT * FROM quests WHERE frequency = 'DAILY' ORDER BY RANDOM() LIMIT :limit")
    suspend fun pickAnyRandomDailyQuests(limit: Int = 3): List<QuestEntity>

    /**
     * Random Weekly Challenge Query:
     * Picks up to [limit] un-claimed Weekly Challenges from the 100-quest pool.
     */
    @Query("SELECT * FROM quests WHERE frequency = 'WEEKLY' AND isClaimed = 0 ORDER BY RANDOM() LIMIT :limit")
    suspend fun pickRandomWeeklyQuests(limit: Int = 3): List<QuestEntity>

    @Query("SELECT * FROM quests WHERE frequency = 'WEEKLY' ORDER BY RANDOM() LIMIT :limit")
    suspend fun pickAnyRandomWeeklyQuests(limit: Int = 3): List<QuestEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(quests: List<QuestEntity>)

    /**
     * Resets rotation state for a fresh day or week cycle without losing catalog quests.
     */
    @Query(
        """
        UPDATE quests
        SET isActive = 0,
            currentProgress = 0,
            isCompleted = 0,
            isClaimed = 0,
            cycleKey = :newCycleKey
        WHERE frequency = :frequency AND cycleKey != :newCycleKey
        """
    )
    suspend fun resetExpiredCycleForFrequency(frequency: QuestFrequency, newCycleKey: String)

    @Query("UPDATE quests SET isActive = 0 WHERE frequency = :frequency")
    suspend fun deactivateAllByFrequency(frequency: QuestFrequency)

    @Query("UPDATE quests SET isActive = 1, cycleKey = :cycleKey WHERE id IN (:questIds)")
    suspend fun activateQuests(questIds: List<String>, cycleKey: String)

    @Query(
        """
        UPDATE quests
        SET currentProgress = :progress,
            isCompleted = :completed
        WHERE id = :questId AND isClaimed = 0
        """
    )
    suspend fun updateQuestProgress(questId: String, progress: Int, completed: Boolean)

    /**
     * Atomic Duplicate-Claim Prevention:
     * Only transitions isClaimed from 0 -> 1 if the quest is NOT already claimed in the current cycle.
     * Returns affected row count (1 on success, 0 if already claimed / duplicate attempt).
     */
    @Query(
        """
        UPDATE quests
        SET isClaimed = 1,
            isCompleted = 1,
            currentProgress = targetProgress,
            cycleKey = :cycleKey,
            claimedAtMillis = :claimedAtMillis
        WHERE id = :questId AND isClaimed = 0
        """
    )
    suspend fun claimQuestAtomically(questId: String, cycleKey: String, claimedAtMillis: Long): Int

    @Query("SELECT COUNT(*) FROM quests WHERE frequency = :frequency AND isClaimed = 1 AND cycleKey = :cycleKey")
    suspend fun countClaimedInCycle(frequency: QuestFrequency, cycleKey: String): Int

    @Transaction
    suspend fun rotateDailyQuestsTransaction(cycleKey: String, limit: Int = 3): List<QuestEntity> {
        resetExpiredCycleForFrequency(QuestFrequency.DAILY, cycleKey)
        val currentActive = getActiveQuestsByFrequency(QuestFrequency.DAILY)
        if (currentActive.size >= limit) {
            return currentActive
        }
        val needed = limit - currentActive.size
        val existingIds = currentActive.map { it.id }.toSet()
        var candidates = pickRandomDailyQuests(limit + existingIds.size)
            .filter { it.id !in existingIds }
            .take(needed)
        if (candidates.isEmpty() && currentActive.isEmpty()) {
            candidates = pickAnyRandomDailyQuests(limit)
        }
        if (candidates.isNotEmpty()) {
            activateQuests(candidates.map { it.id }, cycleKey)
        }
        return getActiveQuestsByFrequency(QuestFrequency.DAILY)
    }

    @Transaction
    suspend fun rotateWeeklyQuestsTransaction(cycleKey: String, limit: Int = 3): List<QuestEntity> {
        resetExpiredCycleForFrequency(QuestFrequency.WEEKLY, cycleKey)
        val currentActive = getActiveQuestsByFrequency(QuestFrequency.WEEKLY)
        if (currentActive.size >= limit) {
            return currentActive
        }
        val needed = limit - currentActive.size
        val existingIds = currentActive.map { it.id }.toSet()
        var candidates = pickRandomWeeklyQuests(limit + existingIds.size)
            .filter { it.id !in existingIds }
            .take(needed)
        if (candidates.isEmpty() && currentActive.isEmpty()) {
            candidates = pickAnyRandomWeeklyQuests(limit)
        }
        if (candidates.isNotEmpty()) {
            activateQuests(candidates.map { it.id }, cycleKey)
        }
        return getActiveQuestsByFrequency(QuestFrequency.WEEKLY)
    }
}
