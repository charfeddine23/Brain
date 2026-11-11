package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.LocalPharmacy
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TutorialOverlay
import com.example.ui.screens.AppLimitsScreen
import com.example.ui.screens.BrainClinicScreen
import com.example.ui.screens.BrainRotScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EggRewardsScreen
import com.example.ui.screens.HabitPulseDashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.HabitPulseTheme
import com.example.ui.theme.LocalGlassColors
import com.example.ui.viewmodel.HabitPulseViewModel

enum class NavigationTab(
    val titleEn: String,
    val titleAr: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD(
        titleEn = "Dashboard",
        titleAr = "الرئيسية",
        activeIcon = Icons.Filled.Dashboard,
        inactiveIcon = Icons.Outlined.Dashboard,
        testTag = "nav_tab_dashboard"
    ),
    CLINIC(
        titleEn = "Clinic",
        titleAr = "العيادة",
        activeIcon = Icons.Filled.LocalPharmacy,
        inactiveIcon = Icons.Outlined.LocalPharmacy,
        testTag = "nav_tab_clinic"
    ),
    BRAIN_ROT(
        titleEn = "Brain Rot",
        titleAr = "الدماغ",
        activeIcon = Icons.Filled.Psychology,
        inactiveIcon = Icons.Outlined.Psychology,
        testTag = "nav_tab_brain_rot"
    ),
    LIMITS(
        titleEn = "Limits",
        titleAr = "الحدود",
        activeIcon = Icons.Filled.Timer,
        inactiveIcon = Icons.Outlined.Timer,
        testTag = "nav_tab_limits"
    ),
    REWARDS(
        titleEn = "Rewards",
        titleAr = "المكافآت",
        activeIcon = Icons.Filled.AutoAwesome,
        inactiveIcon = Icons.Outlined.AutoAwesome,
        testTag = "nav_tab_rewards"
    ),
    SETTINGS(
        titleEn = "Settings",
        titleAr = "الإعدادات",
        activeIcon = Icons.Filled.Settings,
        inactiveIcon = Icons.Outlined.Settings,
        testTag = "nav_tab_settings"
    )
}

class MainActivity : ComponentActivity() {

    private val viewModel: HabitPulseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val lifecycleOwner = LocalLifecycleOwner.current

            // Check usage permission on resume (e.g. after returning from Settings)
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        viewModel.checkPermission()
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            HabitPulseTheme(
                selectedTheme = uiState.selectedTheme,
                isDarkMode = uiState.isDarkMode,
                isArabic = uiState.isArabic
            ) {
                HabitPulseApp(
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun HabitPulseApp(
    viewModel: HabitPulseViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val glassColors = LocalGlassColors.current
    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    // Back handler for returning to dashboard from secondary tabs
    if (currentTab != NavigationTab.DASHBOARD) {
        BackHandler {
            currentTab = NavigationTab.DASHBOARD
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(glassColors.backgroundBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                LiquidGlassBottomBar(
                    currentTab = currentTab,
                    isArabic = uiState.isArabic,
                    onSelectTab = { currentTab = it }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) { paddingValues ->
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                label = "screen_transition"
            ) { tab ->
                when (tab) {
                    NavigationTab.DASHBOARD -> HabitPulseDashboardScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onNavigateToLimits = { currentTab = NavigationTab.LIMITS },
                        onNavigateToRewards = { currentTab = NavigationTab.REWARDS }
                    )
                    NavigationTab.CLINIC -> BrainClinicScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    NavigationTab.BRAIN_ROT -> BrainRotScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    NavigationTab.LIMITS -> AppLimitsScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    NavigationTab.REWARDS -> EggRewardsScreen(
                        uiState = uiState,
                        viewModel = viewModel,
                        onNavigateBack = { currentTab = NavigationTab.DASHBOARD }
                    )
                    NavigationTab.SETTINGS -> SettingsScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        }

        // Interactive Step-by-Step Tutorial Overlay
        if (uiState.isTutorialVisible) {
            TutorialOverlay(
                currentStep = uiState.tutorialCurrentStep,
                isArabic = uiState.isArabic,
                onNextStep = { viewModel.nextTutorialStep() },
                onPreviousStep = { viewModel.previousTutorialStep() },
                onSelectStep = { viewModel.setTutorialStep(it) },
                onDismiss = { viewModel.dismissTutorial() },
                onComplete = { viewModel.completeTutorial() }
            )
        }
    }
}

@Composable
fun LiquidGlassBottomBar(
    currentTab: NavigationTab,
    isArabic: Boolean,
    onSelectTab: (NavigationTab) -> Unit
) {
    val glassColors = LocalGlassColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(glassColors.cardBackgroundBrush)
            .border(1.dp, glassColors.cardBorderBrush, RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("liquid_glass_bottom_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTab.entries.filter { it != NavigationTab.REWARDS }.forEach { tab ->
                val isSelected = currentTab == tab
                val title = if (isArabic) tab.titleAr else tab.titleEn

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag(tab.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSelected) glassColors.accentColor.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                            contentDescription = title,
                            tint = if (isSelected) glassColors.accentColor else glassColors.onSurfaceVariantColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) glassColors.accentColor else glassColors.onSurfaceVariantColor
                    )
                }
            }
        }
    }
}
