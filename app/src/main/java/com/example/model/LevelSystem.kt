package com.example.model

import kotlin.math.pow

object LevelSystem {
    const val MAX_XP: Long = 1_000_000L
    const val MAX_LEVEL: Int = 100
    private const val EXPONENT: Double = 1.85

    /**
     * Calculates the required cumulative XP to reach a given level (1 to 100).
     * Level 1 = 0 XP
     * Level 100 = 1,000,000 XP
     */
    fun xpRequiredForLevel(level: Int): Long {
        if (level <= 1) return 0L
        if (level >= MAX_LEVEL) return MAX_XP
        val progress = (level - 1).toDouble() / (MAX_LEVEL - 1).toDouble()
        return (progress.pow(EXPONENT) * MAX_XP).toLong().coerceIn(0L, MAX_XP)
    }

    /**
     * Calculates the player's current level (1 to 100) based on their total XP.
     */
    fun calculateLevel(xp: Long): Int {
        val clampedXp = xp.coerceIn(0L, MAX_XP)
        if (clampedXp >= MAX_XP) return MAX_LEVEL
        if (clampedXp <= 0L) return 1

        val normalized = clampedXp.toDouble() / MAX_XP.toDouble()
        val calculated = (normalized.pow(1.0 / EXPONENT) * (MAX_LEVEL - 1)).toInt() + 1
        return calculated.coerceIn(1, MAX_LEVEL)
    }

    /**
     * Calculates progress percentage (0.0 to 1.0) towards the next level.
     */
    fun calculateProgressWithinLevel(xp: Long): Float {
        val level = calculateLevel(xp)
        if (level >= MAX_LEVEL) return 1.0f

        val currentBaseXp = xpRequiredForLevel(level)
        val nextLevelXp = xpRequiredForLevel(level + 1)
        val range = (nextLevelXp - currentBaseXp).coerceAtLeast(1L)
        val currentProgress = (xp - currentBaseXp).coerceAtLeast(0L)

        return (currentProgress.toFloat() / range.toFloat()).coerceIn(0f, 1f)
    }

    /**
     * Returns the dynamic title based on the level tiers:
     * - Level 1-20: Active Mind / العقل النشط
     * - Level 21-50: Focus Guardian / حارس التركيز
     * - Level 51-80: Iron Will / الإرادة الحديدية
     * - Level 81-100: Ascended Mind / العقل السامي
     */
    fun getTitleForLevel(level: Int, isArabic: Boolean): String {
        return when (level) {
            in 1..20 -> if (isArabic) "العقل النشط" else "Active Mind"
            in 21..50 -> if (isArabic) "حارس التركيز" else "Focus Guardian"
            in 51..80 -> if (isArabic) "الإرادة الحديدية" else "Iron Will"
            else -> if (isArabic) "العقل السامي" else "Ascended Mind"
        }
    }

    fun getBadgeForLevel(level: Int): String {
        return when (level) {
            in 1..20 -> "⚡"
            in 21..50 -> "🛡️"
            in 51..80 -> "⚔️"
            else -> "👑"
        }
    }
}
