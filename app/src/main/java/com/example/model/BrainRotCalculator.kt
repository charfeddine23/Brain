package com.example.model

/**
 * Neuroscience-backed Brain Rot & Penalty Algorithm:
 * - Computes real-time cumulative Brain Rot Percentage (0% to 100%)
 * - Factors:
 *   1. Time Overuse: (minutes over threshold) * category.rotWeight * 0.5%
 *   2. App Open Count: Ignored (opening apps does not affect rot or penalties)
 *   3. Unprotected Breaches:
 *      - 1st breach: +20% Rot surge & -10 Flame points
 *      - 2nd breach: +30% Rot surge & -20 Flame points
 *      - 3rd breach: +45% Rot surge & Total Reset (0 Flame, 0 Streak)
 *   4. Shield Protection:
 *      - Completely absorbs breach penalty (0 flame deducted, streak saved)
 *      - Minimizes rot surge (suppressed by 80%)
 *   5. Productivity Immunity:
 *      - Productivity apps (rotWeight = 0.0f) generate 0% rot and are immune to penalties.
 *   6. Healing / Recovery:
 *      - Decreases rot over time or via active detox meditation.
 */
object BrainRotCalculator {

    /**
     * Calculates the real-time rot contributed by active app usage (time overuse only).
     */
    fun calculateUsageRot(thresholds: List<AppThreshold>): Float {
        var totalRot = 0.0f

        for (threshold in thresholds) {
            val category = AppCategory.fromString(threshold.category)
            if (category.isImmune) continue

            // Time Overuse excess only (launch count has zero impact)
            if (threshold.maxMinutes > 0 && threshold.currentMinutes > threshold.maxMinutes) {
                val excessMinutes = threshold.currentMinutes - threshold.maxMinutes
                val timeRot = excessMinutes * category.rotWeight * 0.5f
                totalRot += timeRot
            }
        }

        return totalRot.coerceIn(0f, 100f)
    }

    /**
     * Calculates the penalty points and rot surge for a given breach index.
     * Returns: Triple(penaltyPoints, rotSurge, isStreakReset)
     */
    fun calculateBreachImpact(
        breachIndex: Int,
        isShielded: Boolean,
        category: AppCategory
    ): Triple<Int, Float, Boolean> {
        if (category.isImmune) {
            return Triple(0, 0f, false)
        }

        if (isShielded) {
            // Shield absorbs 100% of flame penalty & suppresses 80% of rot surge
            val suppressedRot = when (breachIndex) {
                1 -> 4f
                2 -> 6f
                else -> 8f
            } * category.rotWeight
            return Triple(0, suppressedRot, false)
        }

        // Unprotected breach: strict penalties
        val penalty = when (breachIndex) {
            1 -> 10
            2 -> 20
            else -> 30
        }
        val rotSurge = when (breachIndex) {
            1 -> 20f
            2 -> 30f
            else -> 45f
        } * category.rotWeight
        val isStreakReset = breachIndex >= 3

        return Triple(penalty, rotSurge, isStreakReset)
    }

    /**
     * Formats rot status into human-readable neuropsychological state.
     */
    fun getRotStatusLabel(rotPercentage: Float, isArabic: Boolean): String {
        val clamped = rotPercentage.coerceIn(0f, 100f)
        return when {
            clamped <= 20f -> if (isArabic) "صفاء ذهني ناصع (حالة ممتازة)" else "Pristine Clarity (Optimal Focus)"
            clamped <= 50f -> if (isArabic) "تعفن تصفح أولي (تشتت ذهني)" else "Mild Doomscrolling Rot"
            clamped <= 80f -> if (isArabic) "اضمحلال عصبي متقدم (خطر الإدمان)" else "Severe Brain Decay"
            else -> if (isArabic) "تعفن دماغي حرج ونخر عصبي!" else "CRITICAL BRAIN ROT!"
        }
    }
}
