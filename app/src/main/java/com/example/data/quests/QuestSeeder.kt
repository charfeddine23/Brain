package com.example.data.quests

/**
 * Complete 300-Quest Catalog Seeder:
 * - 200 Explicit Daily Quests (50 Dopamine Fasting, 50 Buffer Protection, 50 Kinetic Energy, 50 Advanced Discipline)
 * - 100 Explicit Weekly Quests (25 Streak Guardians, 25 Mystery Box Hunters, 25 Deep Fasting, 25 Neural Upgrades)
 */
object QuestSeeder {

    fun getAllDailyQuests(): List<QuestEntity> =
        DailyFastingAndBufferQuests.dopamineFastingQuests +
            DailyFastingAndBufferQuests.bufferProtectionQuests +
            DailyKineticAndDisciplineQuests.kineticEnergyQuests +
            DailyKineticAndDisciplineQuests.advancedDisciplineQuests

    fun getAllWeeklyQuests(): List<QuestEntity> =
        WeeklyQuestSeeder.allWeeklyQuests

    fun getAllQuests(): List<QuestEntity> =
        getAllDailyQuests() + getAllWeeklyQuests()

    suspend fun seedIfEmpty(questDao: QuestDao) {
        val allQuests = getAllQuests()
        if (questDao.getQuestCount() < allQuests.size) {
            questDao.insertAll(allQuests)
        }
    }
}
