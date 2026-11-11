package com.example.model

/**
 * Psychologically-backed default thresholds based on behavioral neuroscience:
 * - SOCIAL_MEDIA: Max 45 mins/day, Max 15 launches/day, Rot Weight: 1.5x (High dopamine loop)
 * - ENTERTAINMENT_GAMES: Max 60 mins/day, Max 10 launches/day, Rot Weight: 1.2x (Hyper-stimulation)
 * - BROWSING_STREAMING: Max 90 mins/day, Max 12 launches/day, Rot Weight: 1.0x (Passive consumption)
 * - PRODUCTIVITY_TOOLS: Unlimited, Rot Weight: 0.0f (Immune to rot/penalties)
 */
enum class AppCategory(
    val titleEn: String,
    val titleAr: String,
    val defaultMaxMinutes: Int,
    val defaultMaxLaunches: Int,
    val rotWeight: Float,
    val neuroRiskEn: String,
    val neuroRiskAr: String,
    val emoji: String
) {
    SOCIAL_MEDIA(
        titleEn = "Social Media",
        titleAr = "وسائل التواصل الاجتماعي",
        defaultMaxMinutes = 45,
        defaultMaxLaunches = 15,
        rotWeight = 1.5f,
        neuroRiskEn = "High dopamine hook & variable reward loop. Heavy rot multiplier (1.5x).",
        neuroRiskAr = "حلقات دوبامين متكررة وسريعة. مضاعف تعفن مرتفع (1.5x).",
        emoji = "📱"
    ),
    ENTERTAINMENT_GAMES(
        titleEn = "Entertainment & Games",
        titleAr = "الألعاب والترفيه",
        defaultMaxMinutes = 60,
        defaultMaxLaunches = 10,
        rotWeight = 1.2f,
        neuroRiskEn = "Hyper-stimulation & deep escapism. Accelerated rot weight (1.2x).",
        neuroRiskAr = "إثارة مفرطة واستغراق زمني طويل. وزن تعفن (1.2x).",
        emoji = "🎮"
    ),
    BROWSING_STREAMING(
        titleEn = "Browsing & Streaming",
        titleAr = "التصفح والبث",
        defaultMaxMinutes = 90,
        defaultMaxLaunches = 12,
        rotWeight = 1.0f,
        neuroRiskEn = "Passive doomscrolling & binge consumption. Baseline rot weight (1.0x).",
        neuroRiskAr = "تصفح مستمر واستهلاك سلبي للمحتوى. وزن تعفن أساسي (1.0x).",
        emoji = "🌐"
    ),
    PRODUCTIVITY_TOOLS(
        titleEn = "Productivity & Utilities",
        titleAr = "الإنتاجية والأدوات",
        defaultMaxMinutes = 0, // Unlimited
        defaultMaxLaunches = 0, // Unlimited
        rotWeight = 0.0f, // Completely Immune
        neuroRiskEn = "Deep work, study & creative focus. Fully immune to brain rot & penalties (0.0x).",
        neuroRiskAr = "عمل عميق وتعلم وأدوات إنجاز. محصن تماماً من التعفن والعقوبات (0.0x).",
        emoji = "💼"
    );

    val isImmune: Boolean
        get() = rotWeight == 0.0f || defaultMaxMinutes == 0

    companion object {
        fun fromString(value: String?): AppCategory {
            if (value.isNullOrBlank()) return SOCIAL_MEDIA
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) ||
                it.titleEn.equals(value, ignoreCase = true)
            } ?: SOCIAL_MEDIA
        }

        fun fromPackageName(packageName: String): AppCategory {
            val lower = packageName.lowercase()
            return when {
                lower.contains("instagram") || lower.contains("facebook") || lower.contains("tiktok") ||
                lower.contains("snapchat") || lower.contains("twitter") || lower.contains("x.corp") ||
                lower.contains("threads") || lower.contains("reddit") || lower.contains("social") -> SOCIAL_MEDIA

                lower.contains("youtube") || lower.contains("netflix") || lower.contains("spotify") ||
                lower.contains("twitch") || lower.contains("game") || lower.contains("play") ||
                lower.contains("disney") || lower.contains("primevideo") || lower.contains("video") -> ENTERTAINMENT_GAMES

                lower.contains("chrome") || lower.contains("browser") || lower.contains("firefox") ||
                lower.contains("opera") || lower.contains("safari") || lower.contains("edge") -> BROWSING_STREAMING

                lower.contains("doc") || lower.contains("sheet") || lower.contains("note") ||
                lower.contains("office") || lower.contains("drive") || lower.contains("calculator") ||
                lower.contains("clock") || lower.contains("calendar") || lower.contains("camera") ||
                lower.contains("settings") || lower.contains("tool") -> PRODUCTIVITY_TOOLS

                else -> SOCIAL_MEDIA
            }
        }
    }
}
