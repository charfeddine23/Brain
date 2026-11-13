package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppThreshold
import com.example.model.BreachRecord
import com.example.model.EggState
import com.example.model.EggTier
import com.example.model.ThemeType
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitPulseRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("habit_pulse_prefs", Context.MODE_PRIVATE)

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)

    fun getTodayKey(): String = dateFormat.format(Date())

    fun getWeekKey(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}_w${cal.get(Calendar.WEEK_OF_YEAR)}"
    }

    // --- Flame & Strike ---
    fun getFlameScore(): Int = prefs.getInt(KEY_FLAME_SCORE, 100)
    fun setFlameScore(score: Int) = prefs.edit().putInt(KEY_FLAME_SCORE, score.coerceIn(0, 100)).apply()

    fun getStreakDays(): Int = prefs.getInt(KEY_STREAK_DAYS, 0) // Start fresh at 0 days!
    fun setStreakDays(days: Int) = prefs.edit().putInt(KEY_STREAK_DAYS, maxOf(0, days)).apply()

    // --- Shields (Max 2) ---
    fun getShieldCount(): Int = prefs.getInt(KEY_SHIELD_COUNT, 0).coerceIn(0, 2)
    fun setShieldCount(count: Int) = prefs.edit().putInt(KEY_SHIELD_COUNT, count.coerceIn(0, 2)).apply()

    // --- Daily Breaches ---
    fun getBreachesDate(): String = prefs.getString(KEY_BREACHES_DATE, "") ?: ""
    fun getDailyBreachesCount(): Int {
        val today = getTodayKey()
        return if (getBreachesDate() == today) {
            prefs.getInt(KEY_DAILY_BREACHES_COUNT, 0)
        } else {
            0
        }
    }

    fun setDailyBreachesCount(count: Int) {
        val today = getTodayKey()
        prefs.edit()
            .putString(KEY_BREACHES_DATE, today)
            .putInt(KEY_DAILY_BREACHES_COUNT, count)
            .apply()
    }

    // --- Daily 1000-Point Protective Safety Buffer Pool ---
    fun getDailyBufferRemaining(): Int {
        val today = getTodayKey()
        val storedDate = prefs.getString(KEY_BUFFER_DATE, "") ?: ""
        return if (storedDate == today) {
            prefs.getInt(KEY_DAILY_BUFFER_REMAINING, 1000).coerceIn(0, 1000)
        } else {
            1000
        }
    }

    fun setDailyBufferRemaining(points: Int) {
        val today = getTodayKey()
        prefs.edit()
            .putString(KEY_BUFFER_DATE, today)
            .putInt(KEY_DAILY_BUFFER_REMAINING, points.coerceIn(0, 1000))
            .apply()
    }

    // --- Theme & Language ---
    fun getSelectedTheme(): ThemeType {
        val name = prefs.getString(KEY_SELECTED_THEME, ThemeType.LIQUID_GLASS.name)
        return try {
            ThemeType.valueOf(name ?: ThemeType.LIQUID_GLASS.name)
        } catch (_: Exception) {
            ThemeType.LIQUID_GLASS
        }
    }
    fun setSelectedTheme(theme: ThemeType) =
        prefs.edit().putString(KEY_SELECTED_THEME, theme.name).apply()

    fun isArabic(): Boolean = prefs.getBoolean(KEY_IS_ARABIC, false)
    fun setArabic(isArabic: Boolean) = prefs.edit().putBoolean(KEY_IS_ARABIC, isArabic).apply()

    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_IS_DARK_MODE, true)
    fun setDarkMode(isDark: Boolean) = prefs.edit().putBoolean(KEY_IS_DARK_MODE, isDark).apply()

    // --- 100-Level System (Up to 1,000,000 XP) ---
    fun getTotalXp(): Long = prefs.getLong(KEY_TOTAL_XP, 0L).coerceIn(0L, 1_000_000L)
    fun setTotalXp(xp: Long) = prefs.edit().putLong(KEY_TOTAL_XP, xp.coerceIn(0L, 1_000_000L)).apply()

    // --- Dynamic Brain Rot (0% to 100%) ---
    fun getBrainRotPercentage(): Float = prefs.getFloat(KEY_BRAIN_ROT, 0f).coerceIn(0f, 100f)
    fun setBrainRotPercentage(rot: Float) = prefs.edit().putFloat(KEY_BRAIN_ROT, rot.coerceIn(0f, 100f)).apply()

    fun getLastDetoxTimestamp(): Long = prefs.getLong(KEY_LAST_DETOX, System.currentTimeMillis())
    fun setLastDetoxTimestamp(ts: Long) = prefs.edit().putLong(KEY_LAST_DETOX, ts).apply()

    // --- Egg States ---
    fun getEggStates(): Map<EggTier, EggState> {
        val map = mutableMapOf<EggTier, EggState>()
        for (tier in EggTier.entries) {
            val hatched = prefs.getBoolean("egg_hatched_${tier.name}", false)
            val timestamp = prefs.getLong("egg_ts_${tier.name}", 0L).takeIf { it > 0 }
            val reward = prefs.getString("egg_reward_${tier.name}", null)
            map[tier] = EggState(tier, hatched, timestamp, reward)
        }
        return map
    }

    fun markEggHatched(tier: EggTier, rewardDescription: String) {
        prefs.edit()
            .putBoolean("egg_hatched_${tier.name}", true)
            .putLong("egg_ts_${tier.name}", System.currentTimeMillis())
            .putString("egg_reward_${tier.name}", rewardDescription)
            .apply()
    }

    // --- App Thresholds ---
    fun getAppThresholds(): List<AppThreshold> {
        val raw = prefs.getString(KEY_THRESHOLDS, null)
        if (raw.isNullOrBlank()) {
            return getDefaultThresholds()
        }
        val list = mutableListOf<AppThreshold>()
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    AppThreshold(
                        packageName = obj.getString("packageName"),
                        appName = obj.getString("appName"),
                        maxMinutes = obj.getInt("maxMinutes"),
                        maxLaunches = obj.getInt("maxLaunches"),
                        currentMinutes = obj.optInt("currentMinutes", 0),
                        currentLaunches = obj.optInt("currentLaunches", 0),
                        category = obj.optString("category", "Social")
                    )
                )
            }
        } catch (_: Exception) {
            return getDefaultThresholds()
        }
        return list
    }

    fun saveAppThresholds(thresholds: List<AppThreshold>) {
        val jsonArray = JSONArray()
        for (t in thresholds) {
            val obj = JSONObject().apply {
                put("packageName", t.packageName)
                put("appName", t.appName)
                put("maxMinutes", t.maxMinutes)
                put("maxLaunches", t.maxLaunches)
                put("currentMinutes", t.currentMinutes)
                put("currentLaunches", t.currentLaunches)
                put("category", t.category)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_THRESHOLDS, jsonArray.toString()).apply()
    }

    // --- Breach Records Log ---
    fun getBreachRecords(): List<BreachRecord> {
        val raw = prefs.getString(KEY_BREACH_LOGS, null) ?: return emptyList()
        val list = mutableListOf<BreachRecord>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    BreachRecord(
                        id = obj.getString("id"),
                        packageName = obj.getString("packageName"),
                        appName = obj.getString("appName"),
                        timestamp = obj.getLong("timestamp"),
                        breachIndexToday = obj.getInt("breachIndexToday"),
                        penaltyDeducted = obj.getInt("penaltyDeducted"),
                        wasAbsorbedByShield = obj.getBoolean("wasAbsorbedByShield"),
                        streakResetOccurred = obj.getBoolean("streakResetOccurred")
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return list
    }

    fun addBreachRecord(record: BreachRecord) {
        val current = getBreachRecords().toMutableList()
        current.add(0, record)
        val trimmed = current.take(20) // Keep recent 20 logs
        val arr = JSONArray()
        for (r in trimmed) {
            val obj = JSONObject().apply {
                put("id", r.id)
                put("packageName", r.packageName)
                put("appName", r.appName)
                put("timestamp", r.timestamp)
                put("breachIndexToday", r.breachIndexToday)
                put("penaltyDeducted", r.penaltyDeducted)
                put("wasAbsorbedByShield", r.wasAbsorbedByShield)
                put("streakResetOccurred", r.streakResetOccurred)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_BREACH_LOGS, arr.toString()).apply()
    }

    private fun getDefaultThresholds(): List<AppThreshold> {
        return listOf(
            AppThreshold(
                packageName = "com.instagram.android",
                appName = "Instagram",
                maxMinutes = 30,
                maxLaunches = 10,
                currentMinutes = 0,
                currentLaunches = 0,
                category = "Social"
            ),
            AppThreshold(
                packageName = "com.zhiliaoapp.musically",
                appName = "TikTok",
                maxMinutes = 20,
                maxLaunches = 8,
                currentMinutes = 0,
                currentLaunches = 0,
                category = "Entertainment"
            ),
            AppThreshold(
                packageName = "com.google.android.youtube",
                appName = "YouTube",
                maxMinutes = 45,
                maxLaunches = 12,
                currentMinutes = 0,
                currentLaunches = 0,
                category = "Video"
            )
        )
    }

    // --- Inventory Items & Potions ---
    @Synchronized
    fun getInventory(): Map<com.example.model.InventoryItemType, Int> {
        val map = mutableMapOf<com.example.model.InventoryItemType, Int>()
        for (item in com.example.model.InventoryItemType.entries) {
            val count = prefs.getInt("item_${item.id}", 0) // Start with 0 items
            map[item] = count.coerceAtLeast(0)
        }
        return map
    }

    @Synchronized
    fun setItemCount(type: com.example.model.InventoryItemType, count: Int) {
        prefs.edit().putInt("item_${type.id}", count.coerceAtLeast(0)).apply()
    }

    @Synchronized
    fun addItem(type: com.example.model.InventoryItemType, amount: Int = 1) {
        val current = prefs.getInt("item_${type.id}", 0)
        prefs.edit().putInt("item_${type.id}", (current + amount).coerceAtLeast(0)).apply()
    }

    @Synchronized
    fun consumeItem(type: com.example.model.InventoryItemType): Boolean {
        val current = prefs.getInt("item_${type.id}", 0)
        return if (current > 0) {
            prefs.edit().putInt("item_${type.id}", current - 1).apply()
            true
        } else {
            false
        }
    }

    // --- Shield Immunity Timestamp (in millis) ---
    fun getActiveImmunityUntil(): Long = prefs.getLong(KEY_IMMUNITY_UNTIL, 0L)
    fun setActiveImmunityUntil(timestampMillis: Long) =
        prefs.edit().putLong(KEY_IMMUNITY_UNTIL, timestampMillis).apply()

    // --- Streak Milestones (Every 5 Days) ---
    fun getLastClaimedMilestoneStreak(): Int = prefs.getInt(KEY_CLAIMED_MILESTONE, 0)
    fun setLastClaimedMilestoneStreak(streak: Int) =
        prefs.edit().putInt(KEY_CLAIMED_MILESTONE, streak).apply()

    // --- Quests Persistence (Auto-tracked & Skip-Key only) ---
    @Synchronized
    fun getCompletedDailyQuestIds(): Set<String> {
        val today = getTodayKey()
        if (prefs.getString(KEY_DAILY_QUESTS_DATE, "") != today) {
            return emptySet()
        }
        return prefs.getStringSet(KEY_DAILY_QUESTS_DONE, emptySet())?.toSet() ?: emptySet()
    }

    @Synchronized
    fun markDailyQuestCompleted(questId: String) {
        val today = getTodayKey()
        val existing = if (prefs.getString(KEY_DAILY_QUESTS_DATE, "") == today) {
            prefs.getStringSet(KEY_DAILY_QUESTS_DONE, emptySet())?.toMutableSet() ?: mutableSetOf()
        } else {
            mutableSetOf()
        }
        existing.add(questId)
        prefs.edit()
            .putString(KEY_DAILY_QUESTS_DATE, today)
            .putStringSet(KEY_DAILY_QUESTS_DONE, existing)
            .apply()
    }

    @Synchronized
    fun getCompletedWeeklyQuestIds(): Set<String> {
        val week = getWeekKey()
        if (prefs.getString(KEY_WEEKLY_QUESTS_WEEK, "") != week) {
            return emptySet()
        }
        return prefs.getStringSet(KEY_WEEKLY_QUESTS_DONE, emptySet())?.toSet() ?: emptySet()
    }

    @Synchronized
    fun markWeeklyQuestCompleted(questId: String) {
        val week = getWeekKey()
        val existing = if (prefs.getString(KEY_WEEKLY_QUESTS_WEEK, "") == week) {
            prefs.getStringSet(KEY_WEEKLY_QUESTS_DONE, emptySet())?.toMutableSet() ?: mutableSetOf()
        } else {
            mutableSetOf()
        }
        existing.add(questId)
        prefs.edit()
            .putString(KEY_WEEKLY_QUESTS_WEEK, week)
            .putStringSet(KEY_WEEKLY_QUESTS_DONE, existing)
            .apply()
    }

    @Synchronized
    fun getWeeklyCleanSocialDays(): Int {
        val week = getWeekKey()
        return if (prefs.getString(KEY_WEEKLY_QUESTS_WEEK, "") == week) {
            prefs.getInt(KEY_WEEKLY_CLEAN_SOCIAL_DAYS, 0).coerceAtLeast(0)
        } else {
            0
        }
    }

    @Synchronized
    fun setWeeklyCleanSocialDays(days: Int) {
        prefs.edit()
            .putString(KEY_WEEKLY_QUESTS_WEEK, getWeekKey())
            .putInt(KEY_WEEKLY_CLEAN_SOCIAL_DAYS, days.coerceAtLeast(0))
            .apply()
    }

    @Synchronized
    fun getWeeklyNoDemotionDays(): Int {
        val week = getWeekKey()
        return if (prefs.getString(KEY_WEEKLY_QUESTS_WEEK, "") == week) {
            prefs.getInt(KEY_WEEKLY_NO_DEMOTION_DAYS, 0).coerceAtLeast(0)
        } else {
            0
        }
    }

    @Synchronized
    fun setWeeklyNoDemotionDays(days: Int) {
        prefs.edit()
            .putString(KEY_WEEKLY_QUESTS_WEEK, getWeekKey())
            .putInt(KEY_WEEKLY_NO_DEMOTION_DAYS, days.coerceAtLeast(0))
            .apply()
    }

    // --- Tutorial ---
    fun hasCompletedTutorial(): Boolean = prefs.getBoolean(KEY_HAS_COMPLETED_TUTORIAL, false)
    fun setTutorialCompleted(completed: Boolean) =
        prefs.edit().putBoolean(KEY_HAS_COMPLETED_TUTORIAL, completed).apply()

    // --- Reset All User Data & Game Progress ---
    fun clearAllUserData() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_FLAME_SCORE = "flame_score"
        private const val KEY_STREAK_DAYS = "streak_days"
        private const val KEY_SHIELD_COUNT = "shield_count"
        private const val KEY_BREACHES_DATE = "breaches_date"
        private const val KEY_DAILY_BREACHES_COUNT = "daily_breaches_count"
        private const val KEY_SELECTED_THEME = "selected_theme"
        private const val KEY_IS_ARABIC = "is_arabic"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_TOTAL_XP = "total_xp"
        private const val KEY_BRAIN_ROT = "brain_rot"
        private const val KEY_LAST_DETOX = "last_detox"
        private const val KEY_THRESHOLDS = "app_thresholds"
        private const val KEY_BREACH_LOGS = "breach_logs"
        private const val KEY_IMMUNITY_UNTIL = "immunity_until_ts"
        private const val KEY_CLAIMED_MILESTONE = "claimed_milestone_streak"
        private const val KEY_HAS_COMPLETED_TUTORIAL = "has_completed_tutorial"
        private const val KEY_BUFFER_DATE = "daily_buffer_date"
        private const val KEY_DAILY_BUFFER_REMAINING = "daily_buffer_remaining"
        private const val KEY_DAILY_QUESTS_DATE = "daily_quests_date"
        private const val KEY_DAILY_QUESTS_DONE = "daily_quests_done"
        private const val KEY_WEEKLY_QUESTS_WEEK = "weekly_quests_week"
        private const val KEY_WEEKLY_QUESTS_DONE = "weekly_quests_done"
        private const val KEY_WEEKLY_CLEAN_SOCIAL_DAYS = "weekly_clean_social_days"
        private const val KEY_WEEKLY_NO_DEMOTION_DAYS = "weekly_no_demotion_days"
    }
}
