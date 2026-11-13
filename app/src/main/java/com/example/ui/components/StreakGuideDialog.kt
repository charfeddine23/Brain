package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val StreakPopupBg = Color(0xFF13131A)
private val StreakCardBg = Color(0xFF1B1D29)
private val FlameOrange = Color(0xFFFF6D00)
private val FlameGold = Color(0xFFFFB300)
private val FlameCrimson = Color(0xFFE53935)
private val EmeraldSafe = Color(0xFF10B981)
private val CyanShield = Color(0xFF38BDF8)
private val MutedSlate = Color(0xFF9CA3AF)

@Composable
fun StreakGuideDialog(
    streakDays: Int,
    dailyBufferPool: Int,
    nextMilestoneTarget: Int,
    isShieldActive: Boolean,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val scaleAnim = remember { Animatable(0.88f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }
    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
        )
    }

    val bufferColor = when {
        dailyBufferPool > 500 -> EmeraldSafe
        dailyBufferPool > 0 -> FlameGold
        else -> FlameCrimson
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = StreakPopupBg,
            border = BorderStroke(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(
                        FlameOrange.copy(alpha = 0.55f),
                        Color(0xFF2A2636),
                        FlameCrimson.copy(alpha = 0.25f)
                    )
                )
            ),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 368.dp)
                .heightIn(max = 650.dp)
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                    alpha = alphaAnim.value
                }
                .testTag("streak_guide_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Row: Close button (48.dp accessible target) + subtle live badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("streak_guide_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isArabic) "إغلاق" else "Close",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = FlameOrange.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isArabic) "دليل السلسلة" else "STREAK GUIDE",
                            color = FlameGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Glowing Animated Flame Emblem
                StreakHeroFlameBadge(streakDays = streakDays)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isArabic) "كيف تحصل على السترايك وتحافظ عليه؟"
                           else "How to Earn & Protect Your Streak",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isArabic) "نظام المخزن الوقائي (1,000 نقطة يومياً)"
                           else "Powered by the 1,000-Pt Daily Safety Buffer",
                    color = FlameGold.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Live Telemetry Strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StreakMetricChip(
                        label = if (isArabic) "سلسلتك" else "Streak",
                        value = if (isArabic) "$streakDays يوم" else "$streakDays d",
                        accent = FlameOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StreakMetricChip(
                        label = if (isArabic) "المخزن اليوم" else "Buffer",
                        value = "$dailyBufferPool/1000",
                        accent = bufferColor,
                        modifier = Modifier.weight(1.15f)
                    )
                    StreakMetricChip(
                        label = if (isArabic) "المحطة" else "Next Drop",
                        value = if (isArabic) "يوم $nextMilestoneTarget" else "Day $nextMilestoneTarget",
                        accent = CyanShield,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Rules Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StreakRuleCard(
                        icon = Icons.Default.LocalFireDepartment,
                        accent = EmeraldSafe,
                        title = if (isArabic) "1. شرط كسب +1 سترايك يومياً"
                                else "1. How to Earn +1 Streak Day",
                        body = if (isArabic) {
                            "كل يوم يبدأ بمخزن حماية من 1,000 نقطة. تحصل على +1 سترايك في نهاية اليوم طالما بقي أكثر من 0 نقطة في مخزنك — حتى لو وقعت بعض التجاوزات البسيطة."
                        } else {
                            "You start every day with a 1,000-point protective buffer. You earn +1 Streak Day at day's end as long as > 0 points remain in your buffer."
                        }
                    )

                    StreakRuleCard(
                        icon = Icons.Default.TrendingUp,
                        accent = FlameGold,
                        title = if (isArabic) "2. اليوم النظيف والمكافأة التصاعدية"
                                else "2. Clean Sweep & Escalating Bonus",
                        body = if (isArabic) {
                            "الالتزام بدون أي تجاوز لحدود تطبيقات الـ Boss يحفظ مخزنك 1000/1000 ويمنحك نقاط XP تصاعدية: +500 (أيام 1-89)، +750 (90+)، +1,000 (180+)، +2,000 (360+)."
                        } else {
                            "Finishing with 0 Boss App breaches keeps your buffer at 1000/1000 and grants escalating daily XP: +500 (Days 1–89), +750 (90+), +1,000 (180+), +2,000 (360+)."
                        }
                    )

                    StreakRuleCard(
                        icon = Icons.Default.WarningAmber,
                        accent = FlameCrimson,
                        title = if (isArabic) "3. متى ينكسر السترايك؟"
                                else "3. When Does the Streak Reset?",
                        body = if (isArabic) {
                            "تجاوز حدود التطبيقات يخصم -50 نقطة لكل 10 دقائق تجاوز من مخزن الـ 1,000 نقطة. يعود السترايك إلى 0 فقط إذا استُنزف المخزن بالكامل ووصل إلى 0."
                        } else {
                            "Breaching app limits deducts -50 pts per 10-minute block from your 1,000-pt buffer. Your streak resets to 0 ONLY if the buffer is completely drained to 0."
                        }
                    )

                    StreakRuleCard(
                        icon = Icons.Default.Shield,
                        accent = CyanShield,
                        title = if (isArabic) {
                            if (isShieldActive) "4. الدروع والجوائز (الدرع نشط 🛡️)"
                            else "4. الحماية بالدروع وجوائز السلسلة"
                        } else {
                            if (isShieldActive) "4. Shields & Rewards (Shield Active 🛡️)"
                            else "4. Shield Protection & Streak Rewards"
                        },
                        body = if (isArabic) {
                            "• الدرع (24 ساعة / أسبوعي): يمتص التجاوزات ويمنع نزول المستوى أو ضياع السلسلة.\n• كل 5 أيام سترايك: صندوق غنيمة (مفاتيح، جرعات، دروع).\n• البيض: البرونزية (3 أيام)، الفضية (7 أيام)، بيضة التنين (30 يوماً بدون تجاوز)."
                        } else {
                            "• Aegis Shields (24h / 1-Week): Absorb breaches and prevent demotion or streak loss.\n• Every 5 Streak Days: Unlock a Vault Drop (Keys, Potions, Shields).\n• Eggs: Bronze (3d), Silver (7d), Dragon Egg (30d clean streak)."
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Primary Pill CTA Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(50),
                            ambientColor = FlameOrange.copy(alpha = 0.5f),
                            spotColor = FlameCrimson.copy(alpha = 0.5f)
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(FlameOrange, FlameCrimson)
                            ),
                            shape = RoundedCornerShape(50)
                        )
                        .testTag("streak_guide_got_it_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "فهمت! لنواصل الشعلة" else "Got It! Keep Burning",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakHeroFlameBadge(streakDays: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_hero_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_pulse"
    )
    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flame_orbit"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(86.dp)
    ) {
        Canvas(modifier = Modifier.size(86.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f

            // Outer warm radial aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        FlameOrange.copy(alpha = 0.32f),
                        FlameCrimson.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * pulse
                ),
                radius = baseRadius * pulse,
                center = center
            )

            // Crisp flame ring
            drawCircle(
                color = FlameOrange.copy(alpha = 0.65f),
                radius = baseRadius * 0.74f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Subtle rotating ember dot along the ring
            val rad = Math.toRadians(orbitAngle.toDouble())
            val orbitR = baseRadius * 0.74f
            val dotX = center.x + (orbitR * kotlin.math.cos(rad)).toFloat()
            val dotY = center.y + (orbitR * kotlin.math.sin(rad)).toFloat()
            drawCircle(
                color = FlameGold,
                radius = 3.2.dp.toPx(),
                center = Offset(dotX, dotY)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF3A1A0D),
                            Color(0xFF1F1112)
                        )
                    )
                )
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = if (streakDays > 0) "Active Streak Flame" else "Streak Flame",
                tint = FlameGold,
                modifier = Modifier
                    .size(34.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    }
            )
        }
    }
}

@Composable
private fun StreakMetricChip(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = accent.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MutedSlate,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                color = accent,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StreakRuleCard(
    icon: ImageVector,
    accent: Color,
    title: String,
    body: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StreakCardBg,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.28f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.16f))
                    .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = body,
                    color = Color(0xFFD1D5DB),
                    fontSize = 11.5.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
