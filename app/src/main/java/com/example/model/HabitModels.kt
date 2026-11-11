package com.example.model

/**
 * Represents daily limits set for an application.
 */
data class AppThreshold(
    val packageName: String,
    val appName: String,
    val maxMinutes: Int,
    val maxLaunches: Int,
    val currentMinutes: Int = 0,
    val currentLaunches: Int = 0,
    val category: String = AppCategory.SOCIAL_MEDIA.name
) {
    val appCategory: AppCategory
        get() = AppCategory.fromString(category)

    val isImmune: Boolean
        get() = appCategory.isImmune

    val isMinuteBreached: Boolean
        get() = !isImmune && maxMinutes > 0 && currentMinutes >= maxMinutes

    val isLaunchBreached: Boolean
        get() = !isImmune && maxLaunches > 0 && currentLaunches >= maxLaunches

    val isBreached: Boolean
        get() = !isImmune && (isMinuteBreached || isLaunchBreached)

    val isWarning: Boolean
        get() = !isImmune && !isBreached && (
            (maxMinutes > 0 && currentMinutes >= (maxMinutes * 0.8f).toInt()) ||
            (maxLaunches > 0 && currentLaunches >= (maxLaunches * 0.8f).toInt())
        )

    val usageProgress: Float
        get() {
            if (isImmune) return 0f
            val minProg = if (maxMinutes > 0) currentMinutes.toFloat() / maxMinutes else 0f
            val launchProg = if (maxLaunches > 0) currentLaunches.toFloat() / maxLaunches else 0f
            return maxOf(minProg, launchProg).coerceIn(0f, 1f)
        }
}

/**
 * Represents an installed app metadata for selection.
 */
data class InstalledAppInfo(
    val packageName: String,
    val appName: String
)

/**
 * Multi-Theme Engine themes.
 */
enum class ThemeType(val displayName: String, val arabicName: String) {
    LIQUID_GLASS("Liquid Glass Dark", "زجاج سائل داكن"),
    CYBER_NEON("Cyber Neon Glass", "سايبر نيون زجاجي"),
    MIDNIGHT_OLED("Midnight OLED", "منتصف الليل OLED"),
    EMERALD_GLASS("Emerald Glass", "زمرد زجاجي")
}

/**
 * Tiers of Egg Rewards based on streaks.
 */
enum class EggTier(
    val title: String,
    val arabicTitle: String,
    val requiredStreakDays: Int,
    val dropDescription: String,
    val dropDescriptionAr: String
) {
    BRONZE(
        title = "Bronze Egg",
        arabicTitle = "البيضة البرونزية",
        requiredStreakDays = 3,
        dropDescription = "Drops 'Emerald Glass' theme & Bronze Guardian Badge",
        dropDescriptionAr = "تمنح سمة 'الزمرد الزجاجي' وشارة الحارس البرونزي"
    ),
    SILVER(
        title = "Silver Egg",
        arabicTitle = "البيضة الفضية",
        requiredStreakDays = 7,
        dropDescription = "Drops +1 Shield (Rare) & 'Cyber Neon' theme",
        dropDescriptionAr = "تمنح +1 درع نادر وسمة 'سايبر نيون'"
    ),
    GOLD_DRAGON(
        title = "Dragon Egg",
        arabicTitle = "بيضة التنين الذهبية",
        requiredStreakDays = 30,
        dropDescription = "Guaranteed +1 Shield, 'Midnight OLED' theme & Dragon Mastery",
        dropDescriptionAr = "تمنح درعاً مؤكداً، سمة 'OLED منتصف الليل' ووسام التنين الأسطوري"
    )
}

data class EggState(
    val tier: EggTier,
    val isHatched: Boolean = false,
    val hatchedTimestamp: Long? = null,
    val droppedReward: String? = null
)

/**
 * Represents a logged breach event.
 */
data class BreachRecord(
    val id: String,
    val packageName: String,
    val appName: String,
    val timestamp: Long,
    val breachIndexToday: Int,
    val penaltyDeducted: Int,
    val wasAbsorbedByShield: Boolean,
    val streakResetOccurred: Boolean
)

/**
 * Event emitted when a shield interaction or breach occurs.
 */
sealed class ShieldEvent {
    data class ShieldAbsorbed(
        val appName: String,
        val breachIndex: Int,
        val remainingShields: Int
    ) : ShieldEvent()

    data class PenaltyApplied(
        val appName: String,
        val breachIndex: Int,
        val penalty: Int,
        val streakReset: Boolean
    ) : ShieldEvent()
}
