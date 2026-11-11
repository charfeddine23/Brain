package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.dimensionResource
import com.example.R
import com.example.model.ShieldEvent
import com.example.ui.theme.AlertRed
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.ShieldCyan
import com.example.ui.theme.WarningAmber

/**
 * Base translucent liquid glass card container with responsive padding & radius.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    borderWidth: Dp = 1.dp,
    borderColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val glassColors = LocalGlassColors.current
    val effectiveShape = shape ?: RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius))
    val effectivePadding = dimensionResource(R.dimen.card_padding)

    val clickableMod = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    val strokeBorder = if (borderColor != null) {
        androidx.compose.foundation.BorderStroke(borderWidth, borderColor)
    } else {
        androidx.compose.foundation.BorderStroke(borderWidth, glassColors.cardBorderBrush)
    }

    Box(
        modifier = clickableMod
            .clip(effectiveShape)
            .background(glassColors.cardBackgroundBrush)
            .border(strokeBorder, effectiveShape)
            .padding(effectivePadding)
    ) {
        content()
    }
}

/**
 * Top Flame Strike and Shields Power Gauge.
 */
@Composable
fun FlameStrikeHeader(
    flameScore: Int,
    streakDays: Int,
    shieldCount: Int,
    dailyBreaches: Int,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val flamePulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_scale"
    )

    LiquidGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("flame_strike_header")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flame & Score
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(flamePulse)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(FlameYellow.copy(alpha = 0.4f), Color.Transparent)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Flame Power",
                            tint = FlameOrange,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isArabic) "طاقة الشعلة" else "Flame Power",
                            fontSize = 12.sp,
                            color = glassColors.onSurfaceVariantColor,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$flameScore",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (flameScore > 50) glassColors.accentColor else AlertRed
                            )
                            Text(
                                text = " / 100",
                                fontSize = 14.sp,
                                color = glassColors.onSurfaceVariantColor,
                                modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                            )
                        }
                    }
                }

                // Shields Inventory (Max 2)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isArabic) "الدروع (الأقصى 2)" else "Shields (Max 2)",
                        fontSize = 11.sp,
                        color = glassColors.onSurfaceVariantColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(2) { index ->
                            val isActive = index < shieldCount
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isActive) ShieldCyan.copy(alpha = 0.2f)
                                        else Color.White.copy(alpha = 0.05f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isActive) ShieldCyan else Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isActive) Icons.Filled.Shield else Icons.Outlined.Shield,
                                    contentDescription = "Shield slot",
                                    tint = if (isActive) ShieldCyan else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (shieldCount > 0) {
                            if (isArabic) "مفعل: جاهز للامتصاص" else "Active: Ready to Absorb"
                        } else {
                            if (isArabic) "مستنفد: خطر العقوبة" else "Depleted: At Risk"
                        },
                        fontSize = 10.sp,
                        color = if (shieldCount > 0) SafeGreen else AlertRed,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar for Flame Points
            LinearProgressIndicator(
                progress = { flameScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (flameScore > 50) glassColors.accentColor else AlertRed,
                trackColor = Color.White.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-bar: Streak days & Breaches today counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.07f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "🔥", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic) "$streakDays أيام متتالية" else "$streakDays Day Streak",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.onSurfaceColor
                    )
                }

                // Daily Breaches status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (dailyBreaches > 0) AlertRed.copy(alpha = 0.15f)
                            else SafeGreen.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (dailyBreaches == 0) "🛡️ " else "⚠️ ",
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (isArabic) {
                            if (dailyBreaches == 0) "لا تجاوزات اليوم" else "$dailyBreaches تجاوزات اليوم"
                        } else {
                            if (dailyBreaches == 0) "Zero Breaches" else "$dailyBreaches Breach(es) Today"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (dailyBreaches > 0) AlertRed else SafeGreen
                    )
                }
            }
        }
    }
}

/**
 * Visual dramatic dialog shown when a shield absorbs penalty or when penalties hit.
 */
@Composable
fun ShieldEventDialog(
    event: ShieldEvent,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.95f),
            border = BorderStroke(
                2.dp,
                if (event is ShieldEvent.ShieldAbsorbed) ShieldCyan else AlertRed
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("shield_event_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (event) {
                    is ShieldEvent.ShieldAbsorbed -> {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(ShieldCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = "Shield Absorbed",
                                tint = ShieldCyan,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isArabic) "تحطم الدرع وتصدى للعقوبة!" else "SHIELD SHATTERED & ABSORBED!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = ShieldCyan
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isArabic) {
                                "تم تجاوز الحد في تطبيق '${event.appName}'.\nانكسر درعك وامتص العقوبة بالكامل! لم يتم خصم أي نقاط وتم الحفاظ على التتابع.\nالدروع المتبقية: ${event.remainingShields}/2"
                            } else {
                                "Limit breached on '${event.appName}'.\nYour shield took the impact and shattered! 0 points deducted, streak fully preserved.\nRemaining Shields: ${event.remainingShields}/2"
                            },
                            fontSize = 14.sp,
                            color = glassColors.onSurfaceColor,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    is ShieldEvent.PenaltyApplied -> {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(AlertRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Penalty Applied",
                                tint = AlertRed,
                                modifier = Modifier.size(46.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isArabic) "تم تطبيق العقوبة الصارمة!" else "STRIKE PENALTY APPLIED!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = AlertRed
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isArabic) {
                                val streakMsg = if (event.streakReset) "\n🚨 التجاوز الثالث: تمت إعادة ضبط التتابع بالكامل إلى 0!" else ""
                                "تجاوزت الحد في '${event.appName}' بدون درع حماية!\nتم خصم -${event.penalty} نقطة من طاقة الشعلة.$streakMsg"
                            } else {
                                val streakMsg = if (event.streakReset) "\n🚨 3rd Breach: STREAK RESET TO 0!" else ""
                                "Threshold breached on '${event.appName}' with NO active shield!\nDeducted -${event.penalty} Flame Points.$streakMsg"
                            },
                            fontSize = 14.sp,
                            color = glassColors.onSurfaceColor,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (event is ShieldEvent.ShieldAbsorbed) ShieldCyan else AlertRed,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("dismiss_dialog_btn")
                ) {
                    Text(
                        text = if (isArabic) "فهمت ذلك" else "Understood",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
