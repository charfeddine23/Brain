package com.example.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.data.HabitPulseRepository
import com.example.model.AppCategory
import com.example.model.HabitPulseEngine
import com.example.model.LevelTimeShrinkEngine

/**
 * HabitPulseWidget:
 * A sleek Glance Home Screen Widget displaying:
 * - Live Brain Evolution Status (rotted vs glowing)
 * - Live Streak count (🔥 X Days)
 * - Remaining daily shrinking time allowance
 * - One-tap launcher action to open Habit Pulse
 */
class HabitPulseWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = HabitPulseRepository(context)
        val xp = repo.getTotalXp()
        val level = HabitPulseEngine.calculateLevel(xp)
        val streak = repo.getStreakDays()
        val flame = repo.getFlameScore()
        val rot = repo.getBrainRotPercentage()
        val isArabic = repo.isArabic()
        val rankTitle = HabitPulseEngine.getRankTitle(level, isArabic)
        val rankBadge = HabitPulseEngine.getRankBadgeEmoji(level)
        val socialLimit = LevelTimeShrinkEngine.calculateLimitMinutes(AppCategory.SOCIAL_MEDIA, level)
        val isRotted = rot > 60f

        provideContent {
            GlanceTheme {
                WidgetContent(
                    level = level,
                    streak = streak,
                    flame = flame,
                    rot = rot,
                    isRotted = isRotted,
                    rankTitle = rankTitle,
                    rankBadge = rankBadge,
                    socialLimit = socialLimit,
                    isArabic = isArabic
                )
            }
        }
    }

    @Composable
    private fun WidgetContent(
        level: Int,
        streak: Int,
        flame: Int,
        rot: Float,
        isRotted: Boolean,
        rankTitle: String,
        rankBadge: String,
        socialLimit: Int,
        isArabic: Boolean
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .cornerRadius(20.dp)
                .clickable(actionStartActivity<MainActivity>())
                .padding(14.dp)
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Header: Title and Streak
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧠 Habit Pulse",
                        style = TextStyle(
                            color = ColorProvider(Color.White),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )

                    Text(
                        text = "🔥 $streak ${if (isArabic) "أيام" else "Days"}",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFFF9800)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Level & Neural Rank Badge
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$rankBadge Lvl $level",
                        style = TextStyle(
                            color = ColorProvider(if (isRotted) Color(0xFFEF4444) else Color(0xFF00FFA3)),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    Text(
                        text = rankTitle,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF94A3B8)),
                            fontSize = 11.sp
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(6.dp))

                // Brain Health Status (Rotted vs Glowing)
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusText = if (isRotted) {
                        if (isArabic) "⚠️ تلف ذهني مرتفع (${rot.toInt()}%)" else "⚠️ Rotted (${rot.toInt()}%)"
                    } else {
                        if (isArabic) "✨ وعي عصبي متوهج" else "✨ Glowing Mind"
                    }
                    val statusColor = if (isRotted) Color(0xFFEF4444) else Color(0xFF38BDF8)

                    Text(
                        text = statusText,
                        style = TextStyle(
                            color = ColorProvider(statusColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Dynamic Shrinking Time Allowance
                Text(
                    text = if (isArabic) "حد التواصل: $socialLimit دقيقة" else "Social Allowance: $socialLimit mins",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFE2E8F0)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * AppWidgetReceiver to register HabitPulseWidget with the Android OS.
 */
class HabitPulseWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = HabitPulseWidget()
}
