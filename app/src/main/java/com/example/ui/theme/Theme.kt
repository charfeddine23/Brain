package com.example.ui.theme

import androidx.compose.runtime.Composable
import com.example.model.ThemeType

@Composable
fun MyApplicationTheme(
    selectedTheme: ThemeType = ThemeType.LIQUID_GLASS,
    isDarkMode: Boolean = true,
    isArabic: Boolean = false,
    content: @Composable () -> Unit
) {
    HabitPulseTheme(
        selectedTheme = selectedTheme,
        isDarkMode = isDarkMode,
        isArabic = isArabic,
        content = content
    )
}
