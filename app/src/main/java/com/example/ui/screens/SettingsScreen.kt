package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeType
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberAccent
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.LiquidAccent
import com.example.ui.theme.LiquidSecondary
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.MidnightAccent
import com.example.ui.theme.SafeGreen
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

@Composable
fun SettingsScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val context = LocalContext.current
    val isAr = uiState.isArabic

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = if (isAr) "الإعدادات والسمات" else "Settings & Theme Engine",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = glassColors.onSurfaceColor
                )
                Text(
                    text = if (isAr) "تخصيص الواجهة الزجاجية واللغة والتحكم بالنظام" else "Customize liquid glass aesthetic, language, and system controls",
                    fontSize = 12.sp,
                    color = glassColors.onSurfaceVariantColor
                )
            }
        }

        // Section: Day & Night Mode (الوضع الليلي والنهاري)
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth().testTag("day_night_mode_card")) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = glassColors.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "وضع المظهر (النهاري والليلي)" else "Appearance (Day & Night Mode)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Night Mode Tile
                        ModeTile(
                            icon = Icons.Filled.DarkMode,
                            title = if (isAr) "الوضع الليلي 🌙" else "Night Mode 🌙",
                            subtitle = if (isAr) "زجاج داكن عالي التباين" else "Dark Glass",
                            isSelected = uiState.isDarkMode,
                            modifier = Modifier.weight(1f).testTag("select_dark_mode_tile"),
                            onClick = { viewModel.setDarkMode(true) }
                        )

                        // Day Mode Tile
                        ModeTile(
                            icon = Icons.Filled.LightMode,
                            title = if (isAr) "الوضع النهاري ☀️" else "Day Mode ☀️",
                            subtitle = if (isAr) "كريستال ناصع ومشرق" else "Crystal Light",
                            isSelected = !uiState.isDarkMode,
                            modifier = Modifier.weight(1f).testTag("select_light_mode_tile"),
                            onClick = { viewModel.setDarkMode(false) }
                        )
                    }
                }
            }
        }

        // Section: Multi-Theme Engine
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth().testTag("theme_engine_card")) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = null,
                            tint = glassColors.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "محرك السمات الزجاجية (4 سمات)" else "Multi-Theme Engine (4 Themes)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ThemeType.entries.forEach { theme ->
                        val isSelected = uiState.selectedTheme == theme
                        ThemeOptionTile(
                            theme = theme,
                            isSelected = isSelected,
                            isArabic = isAr,
                            onSelect = { viewModel.setTheme(theme) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Section: Bilingual Localization (Arabic RTL vs English LTR)
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth().testTag("language_switch_card")) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            tint = glassColors.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "اللغة والاتجاه (Bilingual Localization)" else "Language & Direction (Bilingual)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // English option
                        LanguageTile(
                            title = "English (LTR)",
                            isSelected = !uiState.isArabic,
                            modifier = Modifier.weight(1f).testTag("lang_en_btn"),
                            onClick = { viewModel.setLanguage(false) }
                        )

                        // Arabic option
                        LanguageTile(
                            title = "العربية (RTL)",
                            isSelected = uiState.isArabic,
                            modifier = Modifier.weight(1f).testTag("lang_ar_btn"),
                            onClick = { viewModel.setLanguage(true) }
                        )
                    }
                }
            }
        }

        // Section: Permissions & System
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = if (uiState.hasUsagePermission) SafeGreen else glassColors.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "حالة إذن الوصول للاستخدام" else "Usage Access Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (uiState.hasUsagePermission) {
                            if (isAr) "إذن PACKAGE_USAGE_STATS مفعّل وجاهز لمراقبة أحداث ACTIVITY_RESUMED بدقة."
                            else "PACKAGE_USAGE_STATS permission is active, monitoring ACTIVITY_RESUMED events."
                        } else {
                            if (isAr) "الإذن غير ممنوح. يرجى منحه لتفعيل تتبع وقت استخدام التطبيقات بدقة."
                            else "Permission not granted. Please grant to track real screen time usage."
                        },
                        fontSize = 12.sp,
                        color = glassColors.onSurfaceVariantColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            context.startActivity(viewModel.getUsageAccessIntent())
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("open_settings_btn")
                    ) {
                        Text(
                            text = if (isAr) "إدارة الإذن في إعدادات النظام" else "Manage in Android Settings",
                            color = glassColors.accentColor
                        )
                    }
                }
            }
        }

        // Section: Interactive Tutorial & Evolution Guide
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_tutorial_card"),
                onClick = { viewModel.openTutorial() }
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = null,
                            tint = glassColors.accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isAr) "دليل تطور الدماغ والدروع التفاعلي" else "Brain Evolution & Shields Guide",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.onSurfaceColor
                            )
                            Text(
                                text = if (isAr) "شرح تفاعلي خطوة بخطوة للميكانيكا وحصن الحماية" else "Step-by-step interactive tooltips & mechanics guide",
                                fontSize = 11.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.openTutorial() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = glassColors.accentColor.copy(alpha = 0.2f),
                            contentColor = glassColors.accentColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("replay_tutorial_btn")
                    ) {
                        Text(
                            text = if (isAr) "فتح الدليل التفاعلي 🧠" else "Open Interactive Tutorial 🧠",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section: In-App Auto Update (https://github.com/charfeddine23/Brain)
        item {
            val available = uiState.availableUpdate
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_update_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.SystemUpdate,
                                contentDescription = null,
                                tint = if (available != null) SafeGreen else glassColors.accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAr) "التحديث التلقائي للتطبيق" else "In-App Auto Updater",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.onSurfaceColor
                                )
                                Text(
                                    text = if (isAr) "الإصدار المثبت: v${uiState.currentAppVersion}"
                                           else "Installed Version: v${uiState.currentAppVersion}",
                                    fontSize = 11.sp,
                                    color = glassColors.onSurfaceVariantColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) "مستودع التحديثات المباشر: github.com/charfeddine23/Brain"
                               else "Update Source: github.com/charfeddine23/Brain",
                        fontSize = 11.sp,
                        color = glassColors.onSurfaceVariantColor
                    )

                    if (!uiState.updateStatusMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.updateStatusMessage,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (available != null) SafeGreen else glassColors.accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.checkForUpdates(isManual = true) },
                            enabled = !uiState.isCheckingUpdate && !uiState.isDownloadingUpdate,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = glassColors.accentColor.copy(alpha = 0.2f),
                                contentColor = glassColors.accentColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("check_updates_btn")
                        ) {
                            if (uiState.isCheckingUpdate) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = glassColors.accentColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = if (uiState.isCheckingUpdate) {
                                    if (isAr) "جاري الفحص..." else "Checking..."
                                } else {
                                    if (isAr) "فحص التحديثات" else "Check Updates"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (available != null) {
                            Button(
                                onClick = { viewModel.openUpdateDialog() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SafeGreen,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("open_update_dialog_btn")
                            ) {
                                Text(
                                    text = if (isAr) "تحديث إلى v${available.versionName}" else "Update v${available.versionName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Reset Brain Level (تصفير المستوى)
        item {
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_brain_level_card")
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            tint = AlertRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "تصفير المستوى" else "Reset Brain Level",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) {
                            "إعادة ضبط مستوى الدماغ ونقاط الخبرة (XP) والستريك بالكامل إلى المستوى 1 للبدء من جديد."
                        } else {
                            "Reset brain level, accumulated XP, and streak back to Level 1 (0 XP) for a fresh start."
                        },
                        fontSize = 12.sp,
                        color = glassColors.onSurfaceVariantColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.resetBrainLevel() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AlertRed.copy(alpha = 0.2f),
                            contentColor = AlertRed
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_brain_level_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "تصفير المستوى إلى المستوى 1 🧠" else "Reset Brain to Level 1 🧠",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ThemeOptionTile(
    theme: ThemeType,
    isSelected: Boolean,
    isArabic: Boolean,
    onSelect: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    val previewColors = when (theme) {
        ThemeType.LIQUID_GLASS -> listOf(LiquidAccent, LiquidSecondary)
        ThemeType.CYBER_NEON -> listOf(CyberAccent, CyberSecondary)
        ThemeType.MIDNIGHT_OLED -> listOf(MidnightAccent, Color.White)
        ThemeType.EMERALD_GLASS -> listOf(EmeraldAccent, EmeraldSecondary)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) glassColors.accentColor.copy(alpha = 0.15f)
                else Color.White.copy(alpha = 0.04f)
            )
            .border(
                1.dp,
                if (isSelected) glassColors.accentColor else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("theme_tile_${theme.name}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Swatch circles
            Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(previewColors[0])
                        .border(1.dp, Color.Black, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(previewColors[1])
                        .border(1.dp, Color.Black, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (isArabic) theme.arabicName else theme.displayName,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = glassColors.onSurfaceColor
            )
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(glassColors.accentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun LanguageTile(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) glassColors.accentColor.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.dp,
                if (isSelected) glassColors.accentColor else Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) glassColors.accentColor else glassColors.onSurfaceColor
        )
    }
}

@Composable
fun ModeTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) glassColors.accentColor.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.5.dp,
                if (isSelected) glassColors.accentColor else Color.White.copy(alpha = 0.12f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) glassColors.accentColor.copy(alpha = 0.25f)
                        else Color.White.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) glassColors.accentColor else glassColors.onSurfaceVariantColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) glassColors.accentColor else glassColors.onSurfaceColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = glassColors.onSurfaceVariantColor
            )
        }
    }
}
