package com.example.model

import kotlin.math.roundToInt

/**
 * LevelTimeShrinkEngine:
 * Implements the behavioral neuroscience shrinking time rules from Level 1 to Level 100:
 * - Social Media/Media/Live streams: 6 hours (Level 1) -> 20 minutes (Level 100).
 * - Games/Entertainment: 6 hours (Level 1) -> 40 minutes (Level 100).
 * - Productive/Educational apps: Unlimited (480 mins baseline) -> 1 focused hour (60 minutes at Level 100).
 */
object LevelTimeShrinkEngine {

    const val MAX_LEVEL = 100

    /**
     * Calculates the dynamic daily minute limit for a given category and brain level.
     */
    fun calculateLimitMinutes(category: AppCategory, level: Int): Int {
        val clampedLevel = level.coerceIn(1, MAX_LEVEL)
        val progress = (clampedLevel - 1).toFloat() / 99f // 0.0 at Level 1, 1.0 at Level 100

        return when (category) {
            AppCategory.SOCIAL_MEDIA -> {
                // Starts at 6 hours (360 mins) -> shrinks linearly down to 20 mins
                val start = 360f
                val end = 20f
                (start - ((start - end) * progress)).roundToInt()
            }
            AppCategory.ENTERTAINMENT_GAMES -> {
                // Starts at 6 hours (360 mins) -> shrinks down to 40 mins
                val start = 360f
                val end = 40f
                (start - ((start - end) * progress)).roundToInt()
            }
            AppCategory.BROWSING_STREAMING -> {
                // Starts at 6 hours (360 mins) -> shrinks down to 30 mins
                val start = 360f
                val end = 30f
                (start - ((start - end) * progress)).roundToInt()
            }
            AppCategory.PRODUCTIVITY_TOOLS -> {
                // Starts unlimited (480 mins baseline) -> shrinks down to 1 focused hour (60 mins)
                val start = 480f
                val end = 60f
                (start - ((start - end) * progress)).roundToInt()
            }
        }
    }

    /**
     * Calculates dynamic daily launch count limit.
     */
    fun calculateLaunchLimit(category: AppCategory, level: Int): Int {
        val clampedLevel = level.coerceIn(1, MAX_LEVEL)
        val progress = (clampedLevel - 1).toFloat() / 99f

        return when (category) {
            AppCategory.SOCIAL_MEDIA -> (40 - (40 - 5) * progress).roundToInt()
            AppCategory.ENTERTAINMENT_GAMES -> (30 - (30 - 4) * progress).roundToInt()
            AppCategory.BROWSING_STREAMING -> (35 - (35 - 6) * progress).roundToInt()
            AppCategory.PRODUCTIVITY_TOOLS -> (60 - (60 - 15) * progress).roundToInt()
        }
    }
}
