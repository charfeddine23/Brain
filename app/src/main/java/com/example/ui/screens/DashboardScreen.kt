package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppThreshold
import com.example.model.BreachRecord
import com.example.ui.components.FlameStrikeHeader
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.ShieldEventDialog
import com.example.ui.theme.AlertRed
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.ShieldCyan
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel,
    onNavigateToLimits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val context = LocalContext.current
    val isAr = uiState.isArabic

    // Shield / Penalty alert popup
    if (uiState.activeShieldEvent != null) {
        ShieldEventDialog(
            event = uiState.activeShieldEvent,
            isArabic = isAr,
            onDismiss = { viewModel.dismissShieldEvent() }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Top Bar: Title & Refresh button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isAr) "نبض العادات" else "Habit Pulse",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = glassColors.onSurfaceColor
                    )
                    Text(
                        text = if (isAr) "تتبع الشاشة بنظام الشعلة والدرع" else "Screen time with Flame & Shield",
                        fontSize = 12.sp,
                        color = glassColors.onSurfaceVariantColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Day / Night Toggle button
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("toggle_dark_mode_btn")
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = if (uiState.isDarkMode) "Switch to Day Mode" else "Switch to Night Mode",
                            tint = glassColors.accentColor
                        )
                    }
                }
            }
        }

        // Flame and Strike Header
        item {
            FlameStrikeHeader(
                flameScore = uiState.flameScore,
                streakDays = uiState.streakDays,
                shieldCount = uiState.shieldCount,
                dailyBreaches = uiState.dailyBreachesCount,
                isArabic = isAr
            )
        }

        // Permission Card (shown if PACKAGE_USAGE_STATS is not active)
        if (!uiState.hasUsagePermission) {
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth().testTag("permission_card"),
                    borderWidth = 1.5.dp
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "إذن تتبع الاستخدام مطلوب" else "Usage Access Required",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAr) {
                                "يحتاج التطبيق لإذن UsageStatsManager لحساب دقائق الشاشة ومرات الفتح بدقة عبر أحداث ACTIVITY_RESUMED."
                            } else {
                                "Habit Pulse tracks foreground durations and ACTIVITY_RESUMED launch events accurately via Android UsageStatsManager."
                            },
                            fontSize = 12.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                context.startActivity(viewModel.getUsageAccessIntent())
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = glassColors.accentColor,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("grant_permission_btn")
                        ) {
                            Text(
                                text = if (isAr) "فتح إعدادات إذن الاستخدام" else "Grant Usage Access",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section: Monitored App Limits
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "التطبيقات المراقبة وحدودها" else "Monitored App Limits",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.onSurfaceColor
                )

                OutlinedButton(
                    onClick = onNavigateToLimits,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, glassColors.cardBorderBrush),
                    modifier = Modifier.testTag("manage_limits_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = glassColors.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAr) "تعديل" else "Manage",
                        fontSize = 12.sp,
                        color = glassColors.accentColor
                    )
                }
            }
        }

        // App Threshold Cards
        if (uiState.thresholds.isEmpty()) {
            item {
                LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isAr) "لا توجد حدود مضافة بعد" else "No app thresholds configured yet",
                            color = glassColors.onSurfaceVariantColor,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(uiState.thresholds, key = { it.packageName }) { threshold ->
                MonitoredAppCard(
                    threshold = threshold,
                    isArabic = isAr
                )
            }
        }

        // Section: Strict Strike & Flame Rules Guide
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = glassColors.accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "قواعد العقوبات الصارمة والدروع" else "Strict Strike & Shield Mechanics",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAr) {
                            "• المخالفة الأولى: -10 نقاط شعلة\n• المخالفة الثانية في نفس اليوم: -20 نقطة شعلة\n• المخالفة الثالثة: إنهاء التتابع بالكامل (إعادة ضبط إلى 0)!\n• الدرع النشط: يمتص المخالفة بنسبة 100% ويتحطم بدون أي خصم نقاط."
                        } else {
                            "• 1st Breach: -10 Strike Points\n• 2nd Breach same day: -20 Strike Points\n• 3rd Breach: Instant Streak Reset to 0!\n• Active Shield: Absorbs 100% penalty & shatters, streak saved."
                        },
                        fontSize = 12.sp,
                        color = glassColors.onSurfaceVariantColor,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Recent Breach Logs
        if (uiState.breachLogs.isNotEmpty()) {
            item {
                Text(
                    text = if (isAr) "سجل التجاوزات الأخير" else "Recent Breach Activity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = glassColors.onSurfaceColor
                )
            }

            items(uiState.breachLogs.take(5), key = { it.id }) { log ->
                BreachLogCard(log = log, isArabic = isAr)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MonitoredAppCard(
    threshold: AppThreshold,
    isArabic: Boolean
) {
    val glassColors = LocalGlassColors.current

    val statusColor = when {
        threshold.isBreached -> AlertRed
        threshold.isWarning -> WarningAmber
        else -> SafeGreen
    }

    val statusText = when {
        threshold.isBreached -> if (isArabic) "تم التجاوز!" else "BREACHED"
        threshold.isWarning -> if (isArabic) "تحذير (80%)" else "WARNING"
        else -> if (isArabic) "آمن" else "SAFE"
    }

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_threshold_${threshold.packageName}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = threshold.appName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.onSurfaceColor
                    )
                    Text(
                        text = threshold.packageName,
                        fontSize = 11.sp,
                        color = glassColors.onSurfaceVariantColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Limits stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isArabic) {
                        "الوقت: ${threshold.currentMinutes} / ${threshold.maxMinutes} دقيقة"
                    } else {
                        "Time: ${threshold.currentMinutes} / ${threshold.maxMinutes} mins"
                    },
                    fontSize = 12.sp,
                    color = if (threshold.isMinuteBreached) AlertRed else glassColors.onSurfaceColor,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = if (isArabic) {
                        "مرات الفتح: لا تؤثر ✓"
                    } else {
                        "Opens: No Impact ✓"
                    },
                    fontSize = 11.sp,
                    color = glassColors.onSurfaceVariantColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { threshold.usageProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = Color.White.copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun BreachLogCard(log: BreachRecord, isArabic: Boolean) {
    val glassColors = LocalGlassColors.current
    val timeFormatted = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp))

    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderWidth = 0.8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (log.wasAbsorbedByShield) "🛡️" else "⚠️",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = log.appName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.onSurfaceColor
                    )
                    Text(
                        text = if (log.wasAbsorbedByShield) {
                            if (isArabic) "امتصه الدرع بالكامل (0 خصم)" else "Absorbed by Shield (0 loss)"
                        } else {
                            if (isArabic) "المخالفة ${log.breachIndexToday}: خصم -${log.penaltyDeducted} نقطة"
                            else "Breach #${log.breachIndexToday}: -${log.penaltyDeducted} pts"
                        },
                        fontSize = 12.sp,
                        color = if (log.wasAbsorbedByShield) ShieldCyan else AlertRed
                    )
                }
            }

            Text(
                text = timeFormatted,
                fontSize = 11.sp,
                color = glassColors.onSurfaceVariantColor
            )
        }
    }
}
