package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

/**
 * Backward compatibility wrapper for BrainClinicScreen, delegating to the unified InventoryScreen.
 */
@Composable
fun BrainClinicScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel
) {
    InventoryScreen(uiState = uiState, viewModel = viewModel)
}
