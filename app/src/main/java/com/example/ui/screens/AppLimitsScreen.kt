package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppThreshold
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

@Composable
fun AppLimitsScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val isAr = uiState.isArabic

    // List of apps: all phone apps automatically displayed sorted by most used first (الأكثر استعمالاً هو الأعلى)
    val displayList: List<AppThreshold> = remember(uiState.thresholds, uiState.installedApps, uiState.brainLevel) {
        val baseList: List<AppThreshold> = uiState.thresholds.ifEmpty {
            uiState.installedApps.map { app ->
                val cat = com.example.model.AppCategory.fromPackageName(app.packageName)
                val dynamicMins = com.example.model.HabitPulseEngine.calculateDynamicTimeLimit(cat, uiState.brainLevel)
                val dynamicLaunches = com.example.model.HabitPulseEngine.calculateDynamicLaunchLimit(cat, uiState.brainLevel)
                AppThreshold(
                    packageName = app.packageName,
                    appName = app.appName,
                    maxMinutes = dynamicMins,
                    maxLaunches = dynamicLaunches,
                    currentMinutes = 0,
                    currentLaunches = 0,
                    category = cat.name
                )
            }
        }
        baseList
            .distinctBy { it.packageName }
            .sortedWith(
                compareByDescending<AppThreshold> { it.currentMinutes }
                    .thenByDescending { it.currentLaunches }
                    .thenBy { it.appName.lowercase() }
            )
    }

    // Quick Stats: Total Screen Time & Breached Count
    val totalMinsUsed = remember(displayList) { displayList.sumOf { it.currentMinutes } }
    val breachedAppsCount = remember(displayList) { displayList.count { it.isBreached || (it.maxMinutes > 0 && it.currentMinutes >= it.maxMinutes) } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isAr) "حدود التطبيقات الذكية" else "Neural App Limits",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = glassColors.onSurfaceColor
                    )
                    Text(
                        text = if (isAr) "استهلاك اليوم: ${formatHoursMinutes(totalMinsUsed, isAr)} • مستوى $uiState.brainLevel"
                               else "Today's Total: ${formatHoursMinutes(totalMinsUsed, isAr)} • Level ${uiState.brainLevel}",
                        fontSize = 11.sp,
                        color = glassColors.onSurfaceVariantColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = glassColors.accentColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, glassColors.accentColor.copy(alpha = 0.35f)),
                    onClick = { viewModel.refreshUsageStats() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = glassColors.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${displayList.size} ${if (isAr) "تطبيق" else "Apps"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.accentColor
                        )
                    }
                }
            }
        }

        // Compact Section: High-Precision Monitoring System & 7-Day Memory Banner
        item {
            val context = LocalContext.current
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("precision_monitoring_card"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (uiState.hasUsagePermission) Color(0x2210B981) else Color(0x22F59E0B)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = if (uiState.hasUsagePermission) Color(0xFF10B981) else Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (uiState.hasUsagePermission) {
                                    if (isAr) "المراقبة الدقيقة نشطة • انكماش الوقت مفعل"
                                    else "Active Engine • Dynamic Time Shrink ON"
                                } else {
                                    if (isAr) "يتطلب إذن الوصول للاستخدام للعمل بدقة"
                                    else "Requires Usage Access to Track Apps"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.hasUsagePermission) Color(0xFF34D399) else Color(0xFFFBBF24)
                            )
                            if (breachedAppsCount > 0) {
                                Text(
                                    text = if (isAr) "$breachedAppsCount تطبيقات تجاوزت الحد المسموح اليوم ⚠️"
                                           else "$breachedAppsCount apps breached daily limits ⚠️",
                                    fontSize = 10.sp,
                                    color = AlertRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    if (!uiState.hasUsagePermission) {
                        Button(
                            onClick = { context.startActivity(viewModel.getUsageAccessIntent()) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("grant_usage_perm_btn")
                        ) {
                            Text(
                                text = if (isAr) "منح الإذن ⚙️" else "Grant ⚙️",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // List of all installed apps on the phone (Sleek, Compact, Glowing, Dynamic)
        items(
            items = displayList,
            key = { it.packageName }
        ) { threshold ->
            SleekDynamicAppCard(
                threshold = threshold,
                brainLevel = uiState.brainLevel,
                isArabic = isAr
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Ultra-Sleek & Compact Dynamic App Card:
 * - Redesigned for visual harmony, compactness, and high elegance.
 * - Dynamic glowing aura that pulses according to consumption severity:
 *     * Safe: Soft emerald neural glow
 *     * Warning (80%+): Warm amber cyber glow
 *     * Breach / Overtime: Deep pulsing crimson warning glow with subtle border shimmer
 * - Dynamic time breakdown: displays hours & minutes cleanly (e.g., 2h 15m).
 * - Compact horizontal layout with custom glowing neon progress bar.
 */
@Composable
fun SleekDynamicAppCard(
    threshold: AppThreshold,
    brainLevel: Int,
    isArabic: Boolean
) {
    val glassColors = LocalGlassColors.current
    val category = threshold.appCategory
    val maxMinutes = threshold.maxMinutes
    val currentMinutes = threshold.currentMinutes
    val overtimeMinutes = (currentMinutes - maxMinutes).coerceAtLeast(0)

    val isBreached = threshold.isBreached || (maxMinutes > 0 && currentMinutes >= maxMinutes)
    val isWarning = !isBreached && threshold.isWarning

    // Usage ratio
    val progressFraction = if (maxMinutes > 0) {
        (currentMinutes.toFloat() / maxMinutes.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Dynamic glowing animation
    val infiniteTransition = rememberInfiniteTransition(label = "sleek_glow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isBreached) 0.55f else if (isWarning) 0.40f else 0.20f,
        targetValue = if (isBreached) 1.0f else if (isWarning) 0.85f else 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isBreached) 900 else 1600,
                easing = if (isBreached) FastOutSlowInEasing else LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Primary status color & glow gradients
    val primaryColor = when {
        isBreached -> AlertRed
        isWarning -> WarningAmber
        else -> SafeGreen
    }

    // Dynamic card background tint & border gradient
    val borderGradient = Brush.horizontalGradient(
        if (isBreached) {
            listOf(
                AlertRed.copy(alpha = pulseAlpha),
                Color(0xFFFF5722).copy(alpha = pulseAlpha * 0.7f),
                AlertRed.copy(alpha = pulseAlpha * 0.4f)
            )
        } else if (isWarning) {
            listOf(
                WarningAmber.copy(alpha = pulseAlpha),
                Color(0xFFFFB300).copy(alpha = pulseAlpha * 0.6f),
                WarningAmber.copy(alpha = pulseAlpha * 0.3f)
            )
        } else {
            listOf(
                glassColors.cardBorderBrush?.let { SafeGreen.copy(alpha = 0.35f) } ?: Color.White.copy(alpha = 0.15f),
                glassColors.accentColor.copy(alpha = 0.25f)
            )
        }
    )

    val cardBg = when {
        isBreached -> Color(0xFF1F0D11).copy(alpha = 0.88f)
        isWarning -> Color(0xFF1E170A).copy(alpha = 0.85f)
        currentMinutes > 0 -> Color(0xFF0F172A).copy(alpha = 0.75f)
        else -> Color(0xFF0D1322).copy(alpha = 0.60f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_limit_card_${threshold.packageName}")
            .shadow(
                elevation = if (isBreached) 8.dp else 3.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = primaryColor.copy(alpha = pulseAlpha * 0.5f),
                spotColor = primaryColor.copy(alpha = pulseAlpha * 0.7f)
            ),
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(if (isBreached) 1.3.dp else 1.dp, borderGradient)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            // Main Content Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: App Icon & Name & Category
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Category Emoji Container with Glow Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(primaryColor.copy(alpha = 0.14f))
                            .border(1.dp, primaryColor.copy(alpha = pulseAlpha * 0.6f), RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text = category.emoji,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = threshold.appName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) category.titleAr else category.titleEn,
                                fontSize = 10.sp,
                                color = glassColors.onSurfaceVariantColor,
                                maxLines = 1
                            )
                            if (threshold.currentLaunches > 0) {
                                Text(
                                    text = " • ${threshold.currentLaunches} ${if (isArabic) "فتح" else "opens"}",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: Usage vs Limit Badge & Overtime Pill
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Current Usage Time formatted
                        Text(
                            text = formatHoursMinutes(currentMinutes, isArabic),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isBreached) AlertRed else if (isWarning) WarningAmber else Color.White
                        )
                        Text(
                            text = " / " + if (maxMinutes > 0) formatHoursMinutes(maxMinutes, isArabic) else (if (isArabic) "بلا حد" else "∞"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = glassColors.onSurfaceVariantColor
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Breach or Status Indicator
                    if (isBreached) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(AlertRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "+${formatHoursMinutes(overtimeMinutes, isArabic)} تجاوز"
                                       else "+${formatHoursMinutes(overtimeMinutes, isArabic)} breach",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                        }
                    } else if (isWarning) {
                        Text(
                            text = if (isArabic) "اقتراب (80%+)" else "Near limit (80%+)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningAmber
                        )
                    } else {
                        Text(
                            text = if (isArabic) "نطاق آمن" else "Safe range",
                            fontSize = 9.sp,
                            color = SafeGreen.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic Sleek Glowing Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                // Active Fill Bar with Gradient
                if (progressFraction > 0f) {
                    val fillBrush = Brush.horizontalGradient(
                        when {
                            isBreached -> listOf(Color(0xFFFF7043), AlertRed)
                            isWarning -> listOf(Color(0xFFFFD54F), WarningAmber)
                            else -> listOf(Color(0xFF38BDF8), SafeGreen)
                        }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(fillBrush)
                    )
                }

                // If breached: extra pulsing neon border line over whole bar
                if (isBreached) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, AlertRed.copy(alpha = pulseAlpha), RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}

/**
 * Formats minutes into human-readable hours and minutes (e.g. "2س 15د" or "2h 15m").
 */
private fun formatHoursMinutes(minutes: Int, isArabic: Boolean): String {
    if (minutes <= 0) return if (isArabic) "0 د" else "0m"
    val hours = minutes / 60
    val mins = minutes % 60
    return when {
        hours > 0 && mins > 0 -> if (isArabic) "${hours}س ${mins}د" else "${hours}h ${mins}m"
        hours > 0 -> if (isArabic) "${hours} ساعة" else "${hours}h"
        else -> if (isArabic) "${mins} د" else "${mins}m"
    }
}
