package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.HabitPulseEngine
import com.example.ui.components.DynamicBrainCanvas
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.ShieldCyan
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

@Composable
fun BrainRotScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val isAr = uiState.isArabic
    val rot = uiState.brainRotPercentage

    val rotColor = when {
        rot <= 20f -> SafeGreen
        rot <= 50f -> WarningAmber
        rot <= 80f -> FlameOrange
        else -> AlertRed
    }

    val rotStatusTitle = when {
        rot <= 20f -> if (isAr) "صفاء ذهني ناصع (حالة ممتازة)" else "Pristine Clarity (Optimal)"
        rot <= 50f -> if (isAr) "تلف تصفح خفيف (تحذير)" else "Mild Doomscrolling Rot"
        rot <= 80f -> if (isAr) "اضمحلال عصبي متقدم (خطر)" else "Severe Brain Decay"
        else -> if (isAr) "تعفن دماغي حرج ونخر عضوي!" else "CRITICAL BRAIN ROT!"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = if (isAr) "متتبع تعفن الدماغ والمستويات" else "Brain Rot & Level Tracker",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = glassColors.onSurfaceColor
                )
                Text(
                    text = if (isAr) "مراقبة حية للنخر العصبي ونظام الـ 100 مستوى (${HabitPulseEngine.MAX_POINTS} XP)"
                           else "Live canvas neural decay & 100-Level Mastery system (${HabitPulseEngine.MAX_POINTS} XP)",
                    fontSize = 12.sp,
                    color = glassColors.onSurfaceVariantColor
                )
            }
        }

        // Live Brain Canvas Visualizer Card
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("brain_canvas_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = null,
                                tint = rotColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "مؤشر الدماغ الحي" else "Live Neural Cortex",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.onSurfaceColor
                            )
                        }

                        // Healing badge indicator
                        AnimatedVisibility(
                            visible = uiState.isHealingActive,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SafeGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isAr) "✨ جاري الشفاء..." else "✨ Healing Active...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SafeGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Custom Canvas Live Brain
                    DynamicBrainCanvas(
                        level = uiState.brainLevel,
                        rotPercentage = rot,
                        isShieldActive = uiState.isImmunityActive,
                        isHealing = uiState.isHealingActive,
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.brain_canvas_size))
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Rot Gauge Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = if (isAr) "نسبة تعفن الدماغ" else "Brain Rot Level",
                                fontSize = 12.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                            Text(
                                text = rotStatusTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = rotColor
                            )
                        }

                        Text(
                            text = String.format("%.1f%%", rot),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = rotColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linear progress bar for Rot
                    LinearProgressIndicator(
                        progress = { rot / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = rotColor,
                        trackColor = Color.White.copy(alpha = 0.1f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // 100-Level Achievement System Card (1,000 XP per level up to 99,000 XP)
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("level_system_card")
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(glassColors.accentColor.copy(alpha = 0.2f))
                                    .border(1.dp, glassColors.accentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.rankBadge,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${if (isAr) "المستوى" else "Level"} ${uiState.brainLevel} / ${HabitPulseEngine.MAX_LEVEL}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = glassColors.onSurfaceColor
                                )
                                Text(
                                    text = uiState.rankTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.accentColor
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isAr) "إجمالي نقاط الخبرة" else "Total XP",
                                fontSize = 11.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                            Text(
                                text = "${uiState.totalXp} XP",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.secondaryAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Level Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "التقدم نحو المستوى التالي:" else "Progress to next level:",
                            fontSize = 12.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                        Text(
                            text = "${uiState.pointsInCurrentLevel} / ${HabitPulseEngine.POINTS_PER_LEVEL} XP (${(uiState.levelProgress * 100).toInt()}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { uiState.levelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(3.5.dp)),
                        color = glassColors.accentColor,
                        trackColor = Color.White.copy(alpha = 0.1f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Tier Milestone Cards matching HabitPulseEngine ranks
                    Text(
                        text = if (isAr) "مسار المراتب العصبية (100 مستوى • 1,000 XP لكل مستوى):"
                               else "Neural Rank Path (100 Levels • 1,000 XP per Level):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.onSurfaceColor
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        RankTierItem(
                            badge = "🌫️",
                            title = if (isAr) "عقل غارق في الضباب (مستوى 1 - 25)" else "The Foggy Mind (Level 1 - 25)",
                            desc = if (isAr) "بداية التحرر من التشتت الرقمي" else "Initial recovery from digital distraction",
                            isCurrent = uiState.brainLevel in 1..25
                        )
                        RankTierItem(
                            badge = "⚡",
                            title = if (isAr) "عقل قيد الاستيقاظ (مستوى 26 - 50)" else "The Awakening Neural (Level 26 - 50)",
                            desc = if (isAr) "تسارع النبض العصبي وبناء الانضباط" else "Accelerating neural pulse & steady discipline",
                            isCurrent = uiState.brainLevel in 26..50
                        )
                        RankTierItem(
                            badge = "🛡️",
                            title = if (isAr) "العقل الحارس (مستوى 51 - 75)" else "The Guardian Mind (Level 51 - 75)",
                            desc = if (isAr) "تقليص الحدود اليومية وحماية التركيز" else "Tighter daily limits & resilient focus defense",
                            isCurrent = uiState.brainLevel in 51..75
                        )
                        RankTierItem(
                            badge = "🧬",
                            title = if (isAr) "الهندسة العصبية (مستوى 76 - 99)" else "The Neural Architect (Level 76 - 99)",
                            desc = if (isAr) "تحكم شبه مطلق ومناعة ضد الإدمان" else "Near-total mastery & immunity against brain rot",
                            isCurrent = uiState.brainLevel in 76..99
                        )
                        RankTierItem(
                            badge = "🌌",
                            title = if (isAr) "الوعي المطلق (مستوى 100)" else "The Ascended Singularity (Level 100)",
                            desc = if (isAr) "القمة الذهبية المطلقة (${HabitPulseEngine.MAX_POINTS} XP)" else "Ultimate golden mastery (${HabitPulseEngine.MAX_POINTS} XP)",
                            isCurrent = uiState.brainLevel >= HabitPulseEngine.MAX_LEVEL
                        )
                    }
                }
            }
        }

        // Gamified XP Sources breakdown
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "كيف تكسب نقاط الخبرة وترفع المستوى؟" else "How to Earn XP & Level Up?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) {
                            "• كل 1,000 XP ترفع دماغك +1 مستوى تلقائياً (حتى المستوى 100).\n" +
                                "• الالتزام اليومي بدون تجاوزات: +500 حتى +2,000 XP تصاعدياً.\n" +
                                "• إنجاز المهام اليومية والأسبوعية: +650 حتى +5,000 XP لكل مهمة.\n" +
                                "• فقس بيض التتابع والجرعات يمنح مستويات ونقاط خبرة فورية."
                        } else {
                            "• Every 1,000 XP automatically promotes your brain +1 Level (up to Lvl 100).\n" +
                                "• Clean Sweep days (0 breaches): +500 up to +2,000 escalating XP.\n" +
                                "• Daily & Weekly Quests: +650 to +5,000 XP per objective.\n" +
                                "• Hatching streak eggs & healing potions grant instant levels and XP."
                        },
                        fontSize = 12.sp,
                        color = glassColors.onSurfaceVariantColor,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RankTierItem(
    badge: String,
    title: String,
    desc: String,
    isCurrent: Boolean
) {
    val glassColors = LocalGlassColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isCurrent) glassColors.accentColor.copy(alpha = 0.15f)
                else Color.White.copy(alpha = 0.03f)
            )
            .border(
                1.dp,
                if (isCurrent) glassColors.accentColor else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = badge, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isCurrent) glassColors.accentColor else glassColors.onSurfaceColor
            )
            Text(
                text = desc,
                fontSize = 10.sp,
                color = glassColors.onSurfaceVariantColor
            )
        }
    }
}
