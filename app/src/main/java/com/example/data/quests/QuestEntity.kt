package com.example.data.quests

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class QuestFrequency {
    DAILY,
    WEEKLY
}

enum class QuestCategory(
    val titleEn: String,
    val titleAr: String,
    val emoji: String,
    val accentHex: Long,
    val frequency: QuestFrequency
) {
    // 4 Daily Categories (50 quests each = 200 Daily Quests)
    DOPAMINE_FASTING(
        titleEn = "Dopamine Fasting",
        titleAr = "صيام الدوبامين",
        emoji = "🧠",
        accentHex = 0xFF38BDF8,
        frequency = QuestFrequency.DAILY
    ),
    BUFFER_PROTECTION(
        titleEn = "Buffer Protection",
        titleAr = "حماية المخزن",
        emoji = "🛡️",
        accentHex = 0xFF10B981,
        frequency = QuestFrequency.DAILY
    ),
    KINETIC_ENERGY(
        titleEn = "Kinetic Energy",
        titleAr = "الطاقة الحركية",
        emoji = "⚡",
        accentHex = 0xFFF59E0B,
        frequency = QuestFrequency.DAILY
    ),
    ADVANCED_DISCIPLINE(
        titleEn = "Advanced Discipline",
        titleAr = "الانضباط المتقدم",
        emoji = "🎯",
        accentHex = 0xFFA855F7,
        frequency = QuestFrequency.DAILY
    ),

    // 4 Weekly Categories (25 quests each = 100 Weekly Quests)
    STREAK_GUARDIANS(
        titleEn = "Streak Guardians",
        titleAr = "حراس السلسلة",
        emoji = "🔥",
        accentHex = 0xFFFB7185,
        frequency = QuestFrequency.WEEKLY
    ),
    MYSTERY_BOX_HUNTERS(
        titleEn = "Mystery Box Hunters",
        titleAr = "صائدو الصناديق",
        emoji = "🎁",
        accentHex = 0xFFFBBF24,
        frequency = QuestFrequency.WEEKLY
    ),
    DEEP_FASTING(
        titleEn = "Deep Fasting",
        titleAr = "الصيام العميق",
        emoji = "🌌",
        accentHex = 0xFF22D3EE,
        frequency = QuestFrequency.WEEKLY
    ),
    NEURAL_UPGRADES(
        titleEn = "Neural Upgrades",
        titleAr = "الترقيات العصبية",
        emoji = "🧬",
        accentHex = 0xFFC084FC,
        frequency = QuestFrequency.WEEKLY
    )
}

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey val id: String,
    val title: String,
    val titleAr: String,
    val description: String,
    val descriptionAr: String,
    val frequency: QuestFrequency,
    val category: QuestCategory,
    val targetProgress: Int,
    val currentProgress: Int = 0,
    val xpReward: Long,
    val isActive: Boolean = false,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val cycleKey: String = "",
    val claimedAtMillis: Long = 0L
) {
    val progressFraction: Float
        get() = if (targetProgress <= 0) 1f else (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f)

    val isReadyToClaim: Boolean
        get() = (isCompleted || currentProgress >= targetProgress) && !isClaimed
}

class QuestTypeConverters {
    @TypeConverter
    fun fromFrequency(value: QuestFrequency): String = value.name

    @TypeConverter
    fun toFrequency(value: String): QuestFrequency =
        QuestFrequency.entries.firstOrNull { it.name == value } ?: QuestFrequency.DAILY

    @TypeConverter
    fun fromCategory(value: QuestCategory): String = value.name

    @TypeConverter
    fun toCategory(value: String): QuestCategory =
        QuestCategory.entries.firstOrNull { it.name == value } ?: QuestCategory.DOPAMINE_FASTING
}
