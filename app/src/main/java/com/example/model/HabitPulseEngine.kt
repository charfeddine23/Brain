package com.example.model

import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * 7 Types of Reward Items obtainable through 5-Day Streak Milestones, Egg hatches, and Mystery Boxes.
 */
enum class InventoryItemType(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val iconEmoji: String,
    val rarity: ItemRarity,
    val howToObtainEn: String,
    val howToObtainAr: String
) {
    DAILY_QUEST_SKIP_KEY(
        id = "daily_skip_key",
        titleEn = "Daily Quest Skip Key",
        titleAr = "مفتاح تخطي المهمة اليومية",
        descriptionEn = "Instantly auto-completes 1 daily quest without effort, immediately granting its full XP reward.",
        descriptionAr = "يكمل تلقائياً مهمة يومية واحدة بدون جهد وبنقرة واحدة، ويمنحك نقاط خبرتها كاملة فوراً.",
        iconEmoji = "🗝️",
        rarity = ItemRarity.COMMON,
        howToObtainEn = "Obtained from 5-day streak milestone drops or found inside Mystery Cyber Boxes.",
        howToObtainAr = "يُمنح من سحب مكافآت تتابع كل 5 أيام متتالية، أو عند فتح صناديق السايبر الغامضة."
    ),
    WEEKLY_QUEST_SKIP_KEY(
        id = "weekly_skip_key",
        titleEn = "Weekly Quest Skip Key",
        titleAr = "مفتاح تخطي المهمة الأسبوعية",
        descriptionEn = "Instantly auto-completes 1 hardcore weekly quest, bypassing difficult neural requirements.",
        descriptionAr = "يكمل تلقائياً مهمة أسبوعية شاقة وفورية بنقرة زر، متجاوزاً الشروط الصعبة.",
        iconEmoji = "🔑",
        rarity = ItemRarity.RARE,
        howToObtainEn = "Synthesized by fusing 3 Daily Keys in the Fusion Lab, from 5-day streak milestones, or Mystery Boxes.",
        howToObtainAr = "يمكن دمجه بصهر 3 مفاتيح يومية في مختبر الدمج، أو الحصول عليه من مكافأة كل 5 أيام، أو صناديق السايبر."
    ),
    BRAIN_HEALING_POTION(
        id = "healing_potion",
        titleEn = "Brain Healing Potion",
        titleAr = "جرعة التعافي الذهني",
        descriptionEn = "Promotes neurogenesis: Instantly elevates your brain by +1 full level and completely purges rot.",
        descriptionAr = "يعزز التجدد العصبي: يرفع عقلك فوراً بمقدار +1 مستوى كامل ويمحو تعفن الدماغ ويجدد التركيز.",
        iconEmoji = "🧪",
        rarity = ItemRarity.RARE,
        howToObtainEn = "Synthesized in the Fusion Lab using 2 Weekly Keys, or dropped from streak milestones and Mystery Boxes.",
        howToObtainAr = "يتم تخليقها في مختبر الدمج مقابل مفتاحين أسبوعيين، أو إسقاطها من مكافآت التتابع وصناديق السايبر."
    ),
    LEGENDARY_POTION(
        id = "legendary_potion",
        titleEn = "Legendary Potion",
        titleAr = "الجرعة الأسطورية",
        descriptionEn = "The ultimate neural catalyst: Adds +1 Brain Level and auto-completes all active daily & weekly quests!",
        descriptionAr = "المحفز العصبي الأعظم: يمنح +1 مستوى ذهني إضافي ويكمل تلقائياً جميع المهمات اليومية والأسبوعية النشطة فوراً!",
        iconEmoji = "⚗️",
        rarity = ItemRarity.LEGENDARY,
        howToObtainEn = "The rarest item! Guaranteed chance on Day 30 milestones, or ultra-rare drop from Mystery Cyber Boxes.",
        howToObtainAr = "أندر عناصر اللعبة على الإطلاق! فرصة خاصة عند تتابع 30 يوماً متتالياً أو جائزة كبرى نادرة جداً من صناديق السايبر."
    ),
    SHIELD_24H(
        id = "shield_24h",
        titleEn = "24-Hour Shield",
        titleAr = "درع حماية 24 ساعة",
        descriptionEn = "Absorbs time breaches and grants complete immunity from demotion or streak reset for 24 hours.",
        descriptionAr = "يمتص تجاوزات الوقت ويمنح عقلك حصانة تامة من الهبوط أو تصفير السلسلة لمدة 24 ساعة كاملة.",
        iconEmoji = "🛡️",
        rarity = ItemRarity.COMMON,
        howToObtainEn = "Forged in the Fusion Lab with 2 Daily Keys, dropped from 5-day streaks, or hatched from the Silver Egg (7 Days).",
        howToObtainAr = "يُصاغ في مختبر الدمج مقابل مفتاحين يوميين، أو من سحب مكافأة الـ 5 أيام، أو عند فقس البيضة الفضية (7 أيام)."
    ),
    SHIELD_1WEEK(
        id = "shield_1week",
        titleEn = "1-Week Aegis Shield",
        titleAr = "درع الحصانة الأسبوعي (Aegis)",
        descriptionEn = "Legendary fortress: 7 full days of continuous breach absorption, anti-demotion & streak protection.",
        descriptionAr = "حصن دفاعي أسطوري: 7 أيام متواصلة من امتصاص التجاوزات وحماية السلسلة ومنع هبوط المستوى تحت أي ظرف.",
        iconEmoji = "🔰",
        rarity = ItemRarity.EPIC,
        howToObtainEn = "Hatched from the Dragon Egg (30-day streak), or rare milestone drop on Day 30+.",
        howToObtainAr = "يُمنح عند فقس بيضة التنين الأسطورية (30 يوم تتابع متواصل)، أو كسحب نادر جداً في محطات السلسلة المتقدمة."
    ),
    MYSTERY_BOX(
        id = "mystery_box",
        titleEn = "Mystery Cyber Box",
        titleAr = "صندوق السايبر الغامض",
        descriptionEn = "Contains randomized high-value loot: Potions, Shields, Skip Keys, or the elusive Legendary Potion.",
        descriptionAr = "يحتوي على غنائم عشوائية قيّمة ونادرة: جرعات شفاء، دروع حصانة، مفاتيح تخطي، أو الجرعة الأسطورية النادرة.",
        iconEmoji = "🎁",
        rarity = ItemRarity.COMMON,
        howToObtainEn = "Crafted in the Exchange Lab for 15,000 XP, or awarded from 5-day streak milestones.",
        howToObtainAr = "يتم استخراجه في مختبر التبادل باستبدال 15,000 XP، أو الحصول عليه من مكافآت تتابع الـ 5 أيام."
    );

    companion object {
        fun fromId(id: String): InventoryItemType {
            return entries.firstOrNull { it.id == id } ?: MYSTERY_BOX
        }
    }
}

enum class ItemRarity(val labelEn: String, val labelAr: String, val colorHex: Long) {
    COMMON("Common", "شائع", 0xFF94A3B8),
    RARE("Rare", "نادر", 0xFF38BDF8),
    EPIC("Epic", "ملحمي", 0xFFA855F7),
    LEGENDARY("Legendary", "أسطوري", 0xFFF59E0B)
}

/**
 * Daily and Weekly Quest data structures.
 */
data class QuestItem(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val isCompleted: Boolean = false,
    val isWeekly: Boolean = false,
    val xpReward: Long = 5_000L
) {
    val progressPercent: Float
        get() = if (targetProgress <= 0) 1f else (currentProgress.toFloat() / targetProgress).coerceIn(0f, 1f)
}

/**
 * Daily Evaluation & Safety Buffer Pool Outcomes:
 * 1. Safety Buffer Pool: Each day starts with a 1,000-point protective buffer pool.
 * 2. Breach Handling: Boss App threshold breaches deduct points (-50 pts per 10-min block, min 50) from this 1,000-pt buffer.
 * 3. Streak Success Condition: Streak (+1 day) is awarded IF AND ONLY IF remaining buffer > 0.
 *    - 0 breaches: Full escalating streak bonus XP awarded.
 *    - 1+ breaches absorbed by buffer (> 0 remaining): Streak +1 day saved, no level demotion.
 * 4. Streak Break Condition: Streak resets to 0 ONLY IF the 1,000-pt buffer reaches 0 due to heavy breaches.
 */
enum class DailyOutcomeType {
    CLEAN_SWEEP,
    SINGLE_BREACH,
    MULTI_BREACH
}

data class DailyEvaluationResult(
    val outcomeType: DailyOutcomeType,
    val totalBreaches: Int,
    val streakDelta: Int,
    val pointsAwardedOrDeducted: Int,
    val bufferPoolRemaining: Int,
    val levelsGainedOrLost: Int,
    val wasShieldConsumed: Boolean,
    val isStreakReset: Boolean,
    val summaryEn: String,
    val summaryAr: String
)

/**
 * Brain Pulse Game Engine:
 * 1. 100-Level Brain Evolution Engine & Points Architecture (1000 points per level pool).
 * 2. Unconstrained dynamic promotion based on accumulated points.
 * 3. Dynamic Shrinking Time Rule (Level 1 to 100).
 * 4. Full 100% Foreground Session Time Tracking (every second counts).
 * 5. The 3 Daily Evaluation Outcomes (Clean Sweep, Single Breach, Multi-Breach).
 * 6. Escalating Streak Bonus System:
 *    - Days 1 - 89:   +500 points/day
 *    - Days 90 - 179:  +750 points/day
 *    - Days 180 - 359: +1,000 points/day
 *    - Days 360+:      +2,000 points/day
 * 7. Daily 1000-Point Buffer Pool & Unrestricted Demotion Engine.
 */
object HabitPulseEngine {

    const val MAX_LEVEL = 100
    const val POINTS_PER_LEVEL = 1000
    const val MAX_POINTS = (MAX_LEVEL - 1) * POINTS_PER_LEVEL // 99,000 pts for level 100
    const val DAILY_BUFFER_POOL_SIZE = 1000
    const val PENALTY_PER_10_MIN_BLOCK = 50

    /**
     * Points Pool Math:
     * Each level requires 1000 points.
     * Level 1 = 0..999 pts
     * Level 2 = 1000..1999 pts
     * Level 100 = 99000+ pts
     */
    fun calculateLevelFromPoints(totalPoints: Int): Int {
        if (totalPoints <= 0) return 1
        val level = (totalPoints / POINTS_PER_LEVEL) + 1
        return level.coerceIn(1, MAX_LEVEL)
    }

    fun calculateLevelProgress(totalPoints: Int): Float {
        if (totalPoints <= 0) return 0f
        if (totalPoints >= MAX_POINTS) return 1f
        val pointsInCurrentLevel = totalPoints % POINTS_PER_LEVEL
        return (pointsInCurrentLevel.toFloat() / POINTS_PER_LEVEL).coerceIn(0f, 1f)
    }

    fun getPointsForLevel(level: Int): Int {
        if (level <= 1) return 0
        if (level >= MAX_LEVEL) return MAX_POINTS
        return (level - 1) * POINTS_PER_LEVEL
    }

    // Backward-compatible adapters for XP mapping (1 XP = 1 Point)
    const val MAX_XP = MAX_POINTS.toLong()
    fun calculateLevel(xp: Long): Int = calculateLevelFromPoints(xp.toInt())
    fun getXpForLevel(level: Int): Long = getPointsForLevel(level).toLong()

    /**
     * Escalating Daily Streak Bonus:
     * - Days 1-89: +500
     * - Days 90-179: +750
     * - Days 180-359: +1,000
     * - Days 360+: +2,000
     */
    fun getEscalatingStreakBonus(streakDays: Int): Int {
        return when {
            streakDays >= 360 -> 2000
            streakDays >= 180 -> 1000
            streakDays >= 90 -> 750
            else -> 500
        }
    }

    /**
     * The 5 Neural Ranks across the 100 levels.
     */
    fun getRankTitle(level: Int, isArabic: Boolean): String {
        return when {
            level <= 25 -> if (isArabic) "عقل غارق في الضباب" else "The Foggy Mind"
            level <= 50 -> if (isArabic) "عقل قيد الاستيقاظ" else "The Awakening Neural"
            level <= 75 -> if (isArabic) "العقل الحارس" else "The Guardian Mind"
            level <= 99 -> if (isArabic) "الهندسة العصبية" else "The Neural Architect"
            else -> if (isArabic) "الوعي المطلق" else "The Ascended Singularity"
        }
    }

    fun getRankBadgeEmoji(level: Int): String {
        return when {
            level <= 25 -> "🌫️"
            level <= 50 -> "⚡"
            level <= 75 -> "🛡️"
            level <= 99 -> "🧬"
            else -> "🌌"
        }
    }

    /**
     * Dynamic Shrinking Time Rule:
     * - Social Media: 6 hours (Level 1) -> 20 minutes (Level 100)
     * - Games & Entertainment: 6 hours (Level 1) -> 40 minutes (Level 100)
     * - Productivity & Education: Unlimited (480 mins) -> 1 focused hour (Level 100)
     */
    fun calculateDynamicTimeLimit(category: AppCategory, level: Int): Int {
        return LevelTimeShrinkEngine.calculateLimitMinutes(category, level)
    }

    fun calculateDynamicLaunchLimit(category: AppCategory, level: Int): Int {
        return LevelTimeShrinkEngine.calculateLaunchLimit(category, level)
    }

    /**
     * Calculates total penalty points deducted from the 1,000-point safety buffer
     * across all breached Boss Apps (-50 pts per 10-min excess block, minimum 50 pts per breached app).
     */
    fun calculateBreachDeduction(breachedApps: List<AppThreshold>): Int {
        var totalDeduction = 0
        for (app in breachedApps) {
            if (app.isBreached) {
                val excessMinutes = (app.currentMinutes - app.maxMinutes).coerceAtLeast(0)
                val blocks = (excessMinutes / 10).coerceAtLeast(1)
                totalDeduction += blocks * PENALTY_PER_10_MIN_BLOCK
            }
        }
        return totalDeduction
    }

    /**
     * Strict Safety Buffer & Streak Evaluation:
     * 1. Each day provides a 1,000-point protective buffer pool.
     * 2. Breached Boss Apps deduct points from the 1,000-pt buffer.
     * 3. Streak +1 day is awarded IF AND ONLY IF remaining buffer > 0 (not fully exhausted).
     * 4. Streak breaks & resets to 0 ONLY IF the 1,000-pt buffer reaches 0 due to heavy breaches.
     */
    fun evaluateDailyOutcome(
        breachedApps: List<AppThreshold>,
        currentStreak: Int,
        currentTotalPoints: Int,
        hasActiveShield: Boolean
    ): DailyEvaluationResult {
        val activeBreaches = breachedApps.filter { it.isBreached }
        val breachCount = activeBreaches.size

        // 1. Clean Sweep: 0 breaches -> 1000 pts remain in buffer, Streak +1, full escalating XP bonus
        if (breachCount == 0) {
            val newStreak = currentStreak + 1
            val bonus = getEscalatingStreakBonus(newStreak)
            val newPoints = (currentTotalPoints + bonus).coerceAtMost(MAX_POINTS)
            val oldLevel = calculateLevelFromPoints(currentTotalPoints)
            val newLevel = calculateLevelFromPoints(newPoints)
            val levelsGained = (newLevel - oldLevel).coerceAtLeast(0)

            return DailyEvaluationResult(
                outcomeType = DailyOutcomeType.CLEAN_SWEEP,
                totalBreaches = 0,
                streakDelta = 1,
                pointsAwardedOrDeducted = bonus,
                bufferPoolRemaining = DAILY_BUFFER_POOL_SIZE,
                levelsGainedOrLost = levelsGained,
                wasShieldConsumed = false,
                isStreakReset = false,
                summaryEn = "Clean Sweep! 1000/1000 buffer intact • Streak +1 day • +$bonus XP bonus (+$levelsGained lvl).",
                summaryAr = "فوز ناصع! مخزن الأمان 1000/1000 كامل • السلسلة +1 يوم • مكافأة +$bonus نقطة (+$levelsGained مستوى)."
            )
        }

        // 2. Breaches occurred: deduct penalty points from the 1,000-pt safety buffer pool
        val totalDeduction = calculateBreachDeduction(activeBreaches)
        val remainingBuffer = (DAILY_BUFFER_POOL_SIZE - totalDeduction).coerceAtLeast(0)
        val bufferDeficit = (totalDeduction - DAILY_BUFFER_POOL_SIZE).coerceAtLeast(0)
        val outcomeCategory = if (breachCount == 1) DailyOutcomeType.SINGLE_BREACH else DailyOutcomeType.MULTI_BREACH

        // 3. Streak Success Condition: Remaining buffer > 0 -> Streak +1 day awarded!
        return if (remainingBuffer > 0) {
            DailyEvaluationResult(
                outcomeType = outcomeCategory,
                totalBreaches = breachCount,
                streakDelta = 1,
                pointsAwardedOrDeducted = 0, // Deducted from 1000-pt buffer, permanent XP untouched
                bufferPoolRemaining = remainingBuffer,
                levelsGainedOrLost = 0,
                wasShieldConsumed = false,
                isStreakReset = false,
                summaryEn = "Buffer Survived ($remainingBuffer/$DAILY_BUFFER_POOL_SIZE left)! Deducted -$totalDeduction from safety buffer. Streak +1 day preserved!",
                summaryAr = "نجا مخزن الأمان (تبقى $remainingBuffer/$DAILY_BUFFER_POOL_SIZE)! تم خصم -$totalDeduction من المخزن فقط. السلسلة +1 يوم بنجاح!"
            )
        } else {
            // 4. Streak Break Condition: Buffer completely drained (reached 0) -> Streak resets to 0
            if (hasActiveShield) {
                DailyEvaluationResult(
                    outcomeType = DailyOutcomeType.MULTI_BREACH,
                    totalBreaches = breachCount,
                    streakDelta = -currentStreak,
                    pointsAwardedOrDeducted = 0,
                    bufferPoolRemaining = 0,
                    levelsGainedOrLost = 0,
                    wasShieldConsumed = true,
                    isStreakReset = true,
                    summaryEn = "Buffer Exhausted (0/1000)! Streak reset to 0. Active Shield consumed to block level demotions.",
                    summaryAr = "نفد مخزن الأمان (0/1000)! تم تصفير السلسلة إلى 0. تم استهلاك الدرع لمنع هبوط المستوى."
                )
            } else {
                val oldLevel = calculateLevelFromPoints(currentTotalPoints)
                val newPoints = (currentTotalPoints - bufferDeficit).coerceAtLeast(0)
                val actualNewLevel = calculateLevelFromPoints(newPoints)
                val actualLevelsLost = if (bufferDeficit > 0) {
                    val minDemotion = (bufferDeficit / POINTS_PER_LEVEL) + 1
                    (oldLevel - actualNewLevel).coerceAtLeast(minDemotion).coerceAtMost(oldLevel - 1)
                } else {
                    0
                }

                DailyEvaluationResult(
                    outcomeType = DailyOutcomeType.MULTI_BREACH,
                    totalBreaches = breachCount,
                    streakDelta = -currentStreak,
                    pointsAwardedOrDeducted = if (bufferDeficit > 0) -bufferDeficit else 0,
                    bufferPoolRemaining = 0,
                    levelsGainedOrLost = -actualLevelsLost,
                    wasShieldConsumed = false,
                    isStreakReset = true,
                    summaryEn = "Buffer Exhausted (0/1000)! Streak reset to 0 (-$totalDeduction total breach penalty). Levels lost: -$actualLevelsLost.",
                    summaryAr = "نفد مخزن الأمان تماماً (0/1000)! تم تصفير السلسلة إلى 0. هبوط المستوى: -$actualLevelsLost."
                )
            }
        }
    }

    /**
     * 5-Day Streak Milestone Reward Drop.
     */
    fun rollStreakMilestoneReward(streakDays: Int): InventoryItemType {
        val random = Random.nextInt(100)
        return when {
            streakDays % 30 == 0 -> {
                if (random < 50) InventoryItemType.LEGENDARY_POTION else InventoryItemType.SHIELD_1WEEK
            }
            random < 30 -> InventoryItemType.DAILY_QUEST_SKIP_KEY
            random < 50 -> InventoryItemType.SHIELD_24H
            random < 65 -> InventoryItemType.BRAIN_HEALING_POTION
            random < 80 -> InventoryItemType.MYSTERY_BOX
            random < 92 -> InventoryItemType.WEEKLY_QUEST_SKIP_KEY
            else -> InventoryItemType.LEGENDARY_POTION
        }
    }

    fun openMysteryBoxLoot(): InventoryItemType {
        val random = Random.nextInt(100)
        return when {
            random < 35 -> InventoryItemType.DAILY_QUEST_SKIP_KEY
            random < 60 -> InventoryItemType.SHIELD_24H
            random < 78 -> InventoryItemType.BRAIN_HEALING_POTION
            random < 90 -> InventoryItemType.WEEKLY_QUEST_SKIP_KEY
            random < 96 -> InventoryItemType.SHIELD_1WEEK
            else -> InventoryItemType.LEGENDARY_POTION
        }
    }

    fun generateDailyQuests(level: Int): List<QuestItem> {
        val targetSocialMins = calculateDynamicTimeLimit(AppCategory.SOCIAL_MEDIA, level)
        val targetFocusMins = if (level < 30) 25 else 45

        return listOf(
            QuestItem(
                id = "daily_social_limit",
                titleEn = "Tame the Dopamine Loop",
                titleAr = "كبح حلقة الدوبامين",
                descriptionEn = "Keep total social media usage below $targetSocialMins minutes today.",
                descriptionAr = "حافظ على استخدام وسائل التواصل أقل من $targetSocialMins دقيقة اليوم.",
                currentProgress = 0,
                targetProgress = 1,
                xpReward = 800L
            ),
            QuestItem(
                id = "daily_deep_work",
                titleEn = "Deep Neural Focus",
                titleAr = "التركيز العصبي العميق",
                descriptionEn = "Accumulate $targetFocusMins minutes in productive or learning apps.",
                descriptionAr = "سجل $targetFocusMins دقيقة في تطبيقات الإنتاجية أو التعلم.",
                currentProgress = 0,
                targetProgress = targetFocusMins,
                xpReward = 1000L
            ),
            QuestItem(
                id = "daily_zero_breaches",
                titleEn = "Flawless Iron Will",
                titleAr = "إرادة حديدية لا تنكسر",
                descriptionEn = "Complete the day with zero time breaches (Case A: Clean Sweep).",
                descriptionAr = "أكمل اليوم دون أي تجاوز لحدود الوقت المحددة (الحالة أ: فوز ناصع).",
                currentProgress = 0,
                targetProgress = 1,
                xpReward = 1200L
            )
        )
    }

    fun generateWeeklyQuests(level: Int): List<QuestItem> {
        return listOf(
            QuestItem(
                id = "weekly_5day_streak",
                titleEn = "Cyber Monk Discipline",
                titleAr = "انضباط الراهب السيبراني",
                descriptionEn = "Maintain a 5-day continuous adherence streak this week.",
                descriptionAr = "حافظ على سلسلة انضباط مستمرة لمدة 5 أيام متتالية هذا الأسبوع.",
                currentProgress = 0,
                targetProgress = 5,
                isWeekly = true,
                xpReward = 2500L
            ),
            QuestItem(
                id = "weekly_social_detox",
                titleEn = "Total Digital Fast",
                titleAr = "الصيام الرقمي التام",
                descriptionEn = "Execute 3 complete days with zero social media time breaches.",
                descriptionAr = "حقق 3 أيام كاملة دون تجاوز الوقت المسموح لتطبيقات التواصل.",
                currentProgress = 0,
                targetProgress = 3,
                isWeekly = true,
                xpReward = 3500L
            ),
            QuestItem(
                id = "weekly_aegis_master",
                titleEn = "Fortress Defender",
                titleAr = "حامي الحصن العصبي",
                descriptionEn = "Keep your brain level shielded and avoid all demotions for 7 days.",
                descriptionAr = "حافظ على مستوى دماغك محصناً وتجنب أي هبوط في المستوى لمدة 7 أيام.",
                currentProgress = 0,
                targetProgress = 7,
                isWeekly = true,
                xpReward = 4000L
            )
        )
    }
}
