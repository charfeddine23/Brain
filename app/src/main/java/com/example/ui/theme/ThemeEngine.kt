package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ThemeType

@Immutable
data class HabitPulseGlassColors(
    val themeType: ThemeType,
    val isDarkMode: Boolean,
    val backgroundBrush: Brush,
    val cardBackgroundBrush: Brush,
    val cardBorderBrush: Brush,
    val accentColor: Color,
    val secondaryAccent: Color,
    val flameGradient: Brush,
    val shieldColor: Color,
    val surfaceColor: Color,
    val onSurfaceColor: Color,
    val onSurfaceVariantColor: Color,
    val warningColor: Color,
    val breachedColor: Color
)

val LocalGlassColors = staticCompositionLocalOf<HabitPulseGlassColors> {
    error("No GlassColors provided")
}

fun getGlassColorsForTheme(themeType: ThemeType, isDarkMode: Boolean = true): HabitPulseGlassColors {
    return if (isDarkMode) {
        when (themeType) {
            ThemeType.LIQUID_GLASS -> HabitPulseGlassColors(
                themeType = ThemeType.LIQUID_GLASS,
                isDarkMode = true,
                backgroundBrush = Brush.verticalGradient(listOf(LiquidBgStart, LiquidBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(LiquidCardStart, LiquidCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(LiquidBorderStart, LiquidBorderEnd)),
                accentColor = LiquidAccent,
                secondaryAccent = LiquidSecondary,
                flameGradient = Brush.horizontalGradient(listOf(FlameOrange, FlameYellow)),
                shieldColor = ShieldCyan,
                surfaceColor = Color(0xFF0F172A),
                onSurfaceColor = Color(0xFFF8FAFC),
                onSurfaceVariantColor = Color(0xFF94A3B8),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
            ThemeType.CYBER_NEON -> HabitPulseGlassColors(
                themeType = ThemeType.CYBER_NEON,
                isDarkMode = true,
                backgroundBrush = Brush.verticalGradient(listOf(CyberBgStart, CyberBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(CyberCardStart, CyberCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(CyberBorderStart, CyberBorderEnd)),
                accentColor = CyberAccent,
                secondaryAccent = CyberSecondary,
                flameGradient = Brush.horizontalGradient(listOf(CyberAccent, FlameYellow)),
                shieldColor = CyberSecondary,
                surfaceColor = Color(0xFF16062B),
                onSurfaceColor = Color(0xFFFDF4FF),
                onSurfaceVariantColor = Color(0xFFC084FC),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
            ThemeType.MIDNIGHT_OLED -> HabitPulseGlassColors(
                themeType = ThemeType.MIDNIGHT_OLED,
                isDarkMode = true,
                backgroundBrush = Brush.verticalGradient(listOf(MidnightBgStart, MidnightBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(MidnightCardStart, MidnightCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(MidnightBorderStart, MidnightBorderEnd)),
                accentColor = MidnightAccent,
                secondaryAccent = MidnightSecondary,
                flameGradient = Brush.horizontalGradient(listOf(MidnightAccent, Color(0xFFCCFF00))),
                shieldColor = Color(0xFFFFFFFF),
                surfaceColor = Color(0xFF000000),
                onSurfaceColor = Color(0xFFFFFFFF),
                onSurfaceVariantColor = Color(0xFFA3A3A3),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
            ThemeType.EMERALD_GLASS -> HabitPulseGlassColors(
                themeType = ThemeType.EMERALD_GLASS,
                isDarkMode = true,
                backgroundBrush = Brush.verticalGradient(listOf(EmeraldBgStart, EmeraldBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(EmeraldCardStart, EmeraldCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(EmeraldBorderStart, EmeraldBorderEnd)),
                accentColor = EmeraldAccent,
                secondaryAccent = EmeraldSecondary,
                flameGradient = Brush.horizontalGradient(listOf(EmeraldAccent, Color(0xFFFFD600))),
                shieldColor = EmeraldAccent,
                surfaceColor = Color(0xFF062B1F),
                onSurfaceColor = Color(0xFFECFDF5),
                onSurfaceVariantColor = Color(0xFF6EE7B7),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
        }
    } else {
        // LIGHT / DAY MODE
        when (themeType) {
            ThemeType.LIQUID_GLASS -> HabitPulseGlassColors(
                themeType = ThemeType.LIQUID_GLASS,
                isDarkMode = false,
                backgroundBrush = Brush.verticalGradient(listOf(LiquidLightBgStart, LiquidLightBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(LiquidLightCardStart, LiquidLightCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(LiquidLightBorderStart, LiquidLightBorderEnd)),
                accentColor = LiquidLightAccent,
                secondaryAccent = LiquidLightSecondary,
                flameGradient = Brush.horizontalGradient(listOf(FlameOrange, FlameYellow)),
                shieldColor = LiquidLightAccent,
                surfaceColor = Color(0xFFFFFFFF),
                onSurfaceColor = Color(0xFF0F172A),
                onSurfaceVariantColor = Color(0xFF475569),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
            ThemeType.CYBER_NEON -> HabitPulseGlassColors(
                themeType = ThemeType.CYBER_NEON,
                isDarkMode = false,
                backgroundBrush = Brush.verticalGradient(listOf(CyberLightBgStart, CyberLightBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(CyberLightCardStart, CyberLightCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(CyberLightBorderStart, CyberLightBorderEnd)),
                accentColor = CyberLightAccent,
                secondaryAccent = CyberLightSecondary,
                flameGradient = Brush.horizontalGradient(listOf(CyberLightAccent, FlameYellow)),
                shieldColor = CyberLightAccent,
                surfaceColor = Color(0xFFFFFFFF),
                onSurfaceColor = Color(0xFF1E1B4B),
                onSurfaceVariantColor = Color(0xFF701A75),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
            ThemeType.MIDNIGHT_OLED -> HabitPulseGlassColors(
                themeType = ThemeType.MIDNIGHT_OLED,
                isDarkMode = false,
                backgroundBrush = Brush.verticalGradient(listOf(MidnightLightBgStart, MidnightLightBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(MidnightLightCardStart, MidnightLightCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(MidnightLightBorderStart, MidnightLightBorderEnd)),
                accentColor = MidnightLightAccent,
                secondaryAccent = MidnightLightSecondary,
                flameGradient = Brush.horizontalGradient(listOf(MidnightLightAccent, Color(0xFF16A34A))),
                shieldColor = MidnightLightSecondary,
                surfaceColor = Color(0xFFFFFFFF),
                onSurfaceColor = Color(0xFF0F172A),
                onSurfaceVariantColor = Color(0xFF475569),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
            ThemeType.EMERALD_GLASS -> HabitPulseGlassColors(
                themeType = ThemeType.EMERALD_GLASS,
                isDarkMode = false,
                backgroundBrush = Brush.verticalGradient(listOf(EmeraldLightBgStart, EmeraldLightBgEnd)),
                cardBackgroundBrush = Brush.verticalGradient(listOf(EmeraldLightCardStart, EmeraldLightCardEnd)),
                cardBorderBrush = Brush.linearGradient(listOf(EmeraldLightBorderStart, EmeraldLightBorderEnd)),
                accentColor = EmeraldLightAccent,
                secondaryAccent = EmeraldLightSecondary,
                flameGradient = Brush.horizontalGradient(listOf(EmeraldLightAccent, Color(0xFFD97706))),
                shieldColor = EmeraldLightAccent,
                surfaceColor = Color(0xFFFFFFFF),
                onSurfaceColor = Color(0xFF064E3B),
                onSurfaceVariantColor = Color(0xFF047857),
                warningColor = WarningAmber,
                breachedColor = AlertRed
            )
        }
    }
}

@Composable
fun HabitPulseTheme(
    selectedTheme: ThemeType = ThemeType.LIQUID_GLASS,
    isDarkMode: Boolean = true,
    isArabic: Boolean = false,
    content: @Composable () -> Unit
) {
    val glassColors = getGlassColorsForTheme(selectedTheme, isDarkMode)

    val colorScheme = if (isDarkMode) {
        darkColorScheme(
            primary = glassColors.accentColor,
            secondary = glassColors.secondaryAccent,
            background = if (selectedTheme == ThemeType.MIDNIGHT_OLED) Color.Black else Color(0xFF0A0D14),
            surface = glassColors.surfaceColor,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onBackground = glassColors.onSurfaceColor,
            onSurface = glassColors.onSurfaceColor,
            error = glassColors.breachedColor
        )
    } else {
        lightColorScheme(
            primary = glassColors.accentColor,
            secondary = glassColors.secondaryAccent,
            background = Color(0xFFF1F5F9),
            surface = glassColors.surfaceColor,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = glassColors.onSurfaceColor,
            onSurface = glassColors.onSurfaceColor,
            error = glassColors.breachedColor
        )
    }

    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    val titleLargeSp = dimensionResource(R.dimen.text_title_large).value.sp
    val titleMediumSp = dimensionResource(R.dimen.text_title_medium).value.sp
    val bodyLargeSp = dimensionResource(R.dimen.text_body_large).value.sp
    val bodyMediumSp = dimensionResource(R.dimen.text_body_medium).value.sp
    val captionSp = dimensionResource(R.dimen.text_caption).value.sp

    val adaptiveTypography = Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = titleLargeSp),
        titleMedium = Typography.titleMedium.copy(fontSize = titleMediumSp),
        bodyLarge = Typography.bodyLarge.copy(fontSize = bodyLargeSp),
        bodyMedium = Typography.bodyMedium.copy(fontSize = bodyMediumSp),
        labelSmall = Typography.labelSmall.copy(fontSize = captionSp)
    )

    CompositionLocalProvider(
        LocalGlassColors provides glassColors,
        LocalLayoutDirection provides layoutDirection
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = adaptiveTypography,
            content = content
        )
    }
}
