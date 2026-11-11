package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppCategory
import com.example.model.DailyOutcomeType
import com.example.model.HabitPulseEngine
import com.example.model.InventoryItemType
import com.example.ui.theme.LocalGlassColors

/**
 * Total steps in the Brain Evolution & Shield Management Tutorial.
 */
const val TUTORIAL_TOTAL_STEPS = 7

/**
 * Interactive TutorialOverlay component.
 * Provides step-by-step interactive tooltips explaining the Brain Evolution
 * mechanics, dynamic shrinking limits, 1000-pt buffer pool, and comprehensive
 * shield management (Aegis protection, auto-trigger, and inventory vault).
 */
@Composable
fun TutorialOverlay(
    currentStep: Int,
    isArabic: Boolean,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onSelectStep: (Int) -> Unit,
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    // System BackHandler integration
    BackHandler {
        if (currentStep > 0) {
            onPreviousStep()
        } else {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xE6050B14))
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .testTag("tutorial_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Main Glassmorphic Tutorial Card Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 620.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        BorderStroke(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    glassColors.accentColor.copy(alpha = 0.8f),
                                    Color(0xFF38BDF8).copy(alpha = 0.4f),
                                    glassColors.accentColor.copy(alpha = 0.2f)
                                )
                            )
                        ),
                        RoundedCornerShape(28.dp)
                    )
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(28.dp), spotColor = glassColors.accentColor),
                color = Color(0xF00A1120),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- 1. Top Header: Title, Step Counter, and Close Button ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(glassColors.accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, glassColors.accentColor.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = glassColors.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "دليل إتقان نبض الدماغ" else "Brain Pulse Masterclass",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isArabic) "الخطوة ${currentStep + 1} من $TUTORIAL_TOTAL_STEPS" 
                                           else "Step ${currentStep + 1} of $TUTORIAL_TOTAL_STEPS",
                                    fontSize = 11.sp,
                                    color = glassColors.accentColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .testTag("tutorial_skip_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isArabic) "إغلاق الدليل" else "Close Tutorial",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { (currentStep + 1).toFloat() / TUTORIAL_TOTAL_STEPS.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = glassColors.accentColor,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Clickable Step Dots Navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (stepIndex in 0 until TUTORIAL_TOTAL_STEPS) {
                            val isCurrent = stepIndex == currentStep
                            val isCompleted = stepIndex < currentStep
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 6.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSelectStep(stepIndex) }
                                    .background(
                                        when {
                                            isCurrent -> glassColors.accentColor
                                            isCompleted -> Color(0xFF10B981)
                                            else -> Color(0xFF334155)
                                        }
                                    )
                                    .size(
                                        width = if (isCurrent) 24.dp else 10.dp,
                                        height = 8.dp
                                    )
                                    .testTag("tutorial_dot_$stepIndex")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // --- 2. Animated Step Content ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AnimatedContent(
                            targetState = currentStep,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> -width } + fadeOut()
                                    )
                                } else {
                                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> width } + fadeOut()
                                    )
                                }
                            },
                            label = "tutorial_step_content"
                        ) { step ->
                            when (step) {
                                0 -> StepBrainEvolutionEngine(isArabic = isArabic)
                                1 -> StepDynamicBrainCanvas(isArabic = isArabic)
                                2 -> StepShrinkingLimits(isArabic = isArabic)
                                3 -> StepBufferPoolAndOutcomes(isArabic = isArabic)
                                4 -> StepShieldManagement(isArabic = isArabic)
                                5 -> StepInventoryAndEggs(isArabic = isArabic)
                                6 -> StepAscensionReady(isArabic = isArabic)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- 3. Bottom Action Controls ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentStep > 0) {
                            OutlinedButton(
                                onClick = onPreviousStep,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE2E8F0)),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("tutorial_back_btn")
                            ) {
                                Icon(
                                    imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "السابق" else "Back",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Text(
                                text = if (isArabic) "تخطي للبدء مباشرة" else "Skip to start directly",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier
                                    .clickable { onDismiss() }
                                    .padding(8.dp)
                            )
                        }

                        if (currentStep < TUTORIAL_TOTAL_STEPS - 1) {
                            Button(
                                onClick = onNextStep,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = glassColors.accentColor),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("tutorial_next_btn")
                            ) {
                                Text(
                                    text = if (isArabic) "التالي" else "Next Step",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = if (isArabic) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = onComplete,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981)
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("tutorial_finish_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RocketLaunch,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "ابدأ الارتقاء الذهني 🚀" else "Begin Ascension 🚀",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// STEP 1: Brain Evolution Mechanics & 1000-Point Level Pool
// ============================================================================
@Composable
private fun StepBrainEvolutionEngine(isArabic: Boolean) {
    var previewLevel by remember { mutableIntStateOf(1) }

    TutorialCardContent(
        badge = if (isArabic) "المحرك الأساسي" else "CORE ENGINE",
        badgeColor = Color(0xFF38BDF8),
        title = if (isArabic) "محرك تطور الدماغ 100 مستوى" else "100-Level Brain Evolution Engine",
        subtitle = if (isArabic) "نظام المستويات ومسبح الـ 1000 نقطة لكل مستوى" 
                   else "Neural ascension powered by a 1,000-point progression pool",
        icon = Icons.Default.Psychology
    ) {
        // Interactive Level Previewer
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${HabitPulseEngine.getRankBadgeEmoji(previewLevel)} Level $previewLevel / 100",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF38BDF8).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = HabitPulseEngine.getRankTitle(previewLevel, isArabic),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFE0F2FE),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = previewLevel.toFloat(),
                    onValueChange = { previewLevel = it.toInt() },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF38BDF8),
                        activeTrackColor = Color(0xFF38BDF8),
                        inactiveTrackColor = Color(0xFF334155)
                    ),
                    modifier = Modifier.testTag("tutorial_evolution_slider")
                )

                Text(
                    text = if (isArabic) "اسحب لتجربة تحول الرتب العصبية (1 -> 100)" else "Drag to preview neural rank scaling (1 -> 100)",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bullet Explanations
        TutorialBullet(
            icon = Icons.Default.Bolt,
            title = if (isArabic) "مسبح 1000 نقطة لكل مستوى" else "1,000 Points per Level",
            description = if (isArabic) "كل ارتقاء يتطلب 1,000 نقطة بالضبط. تراكم النقاط عبر الانضباط اليومي والمكافآت المتصاعدة."
                          else "Every level requires exactly 1,000 points. Points accrue via daily compliance and streak bonuses."
        )
        TutorialBullet(
            icon = Icons.Default.AutoAwesome,
            title = if (isArabic) "5 رتب عصبية كبرى" else "5 Major Neural Ranks",
            description = if (isArabic) "من العقل الغارق في الضباب (1-25) إلى العقل المستيقظ (26-50)، الحارس (51-75)، المهندس (76-99)، والوعي المطلق (100)!"
                          else "Progress from Foggy Mind (1-25), Awakening (26-50), Guardian (51-75), Architect (76-99), to Ascended Singularity (100)!"
        )
    }
}

// ============================================================================
// STEP 2: The Living Dynamic Brain Canvas & Visual Telemetry
// ============================================================================
@Composable
private fun StepDynamicBrainCanvas(isArabic: Boolean) {
    var previewRot by remember { mutableFloatStateOf(20f) }
    var simulateBreach by remember { mutableStateOf(false) }

    TutorialCardContent(
        badge = if (isArabic) "الرؤية الحيوية" else "DYNAMIC TELEMETRY",
        badgeColor = Color(0xFFA855F7),
        title = if (isArabic) "اللوحة العصبية الحية وتعفن الدماغ" else "Interactive Dynamic Brain Canvas",
        subtitle = if (isArabic) "تجسيد بصري حي لحالة دماغك ونبضه وعلاجه الفوري"
                   else "Real-time neurogenesis feedback: pulse rate, synaptic aura & rot corruption",
        icon = Icons.Default.Psychology
    ) {
        // Mini Interactive Canvas Preview
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0B132B),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DynamicBrainCanvas(
                    level = if (simulateBreach) 10 else 75,
                    rotPercentage = previewRot,
                    isShieldActive = !simulateBreach,
                    isHealing = previewRot < 10f,
                    isGlitching = simulateBreach,
                    modifier = Modifier
                        .size(150.dp)
                        .testTag("tutorial_mini_brain_canvas")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "نسبة تعفن الدماغ: ${previewRot.toInt()}%" else "Brain Rot: ${previewRot.toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (previewRot > 50f) Color(0xFFEF4444) else Color(0xFF10B981)
                    )

                    Button(
                        onClick = {
                            simulateBreach = !simulateBreach
                            previewRot = if (simulateBreach) 75f else 15f
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (simulateBreach) Color(0xFF10B981) else Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp).testTag("tutorial_breach_toggle_btn")
                    ) {
                        Text(
                            text = if (simulateBreach) (if (isArabic) "تطهير الدماغ ✨" else "Purge Rot ✨")
                                   else (if (isArabic) "محاكاة التجاوز ⚠️" else "Test Breach ⚠️"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TutorialBullet(
            icon = Icons.Default.Warning,
            title = if (isArabic) "تعفن الدماغ (Brain Rot)" else "Brain Rot Corruption",
            description = if (isArabic) "تجاوز الحدود المحددة لتطبيقات الترفيه والتواصل يغزو الدماغ بوميض الخلل واللون الرمادي الفاسد."
                          else "Breaching thresholds on dopamine apps triggers visual glitches and corruption on the canvas."
        )
        TutorialBullet(
            icon = Icons.Default.AutoAwesome,
            title = if (isArabic) "التجدد العصبي والحلقات المدارية" else "Neurogenesis & Quantum Orbitals",
            description = if (isArabic) "مع صعودك للمستويات العليا، يتسارع نبض الدماغ وتتولد حلقات كمية مدارية تحيط به كإشعاع فائق."
                          else "High levels unlock accelerated synaptic pulse rhythms and glowing quantum orbital rings."
        )
    }
}

// ============================================================================
// STEP 3: Dynamic Shrinking Time Rule (Level 1 -> 100)
// ============================================================================
@Composable
private fun StepShrinkingLimits(isArabic: Boolean) {
    TutorialCardContent(
        badge = if (isArabic) "قواعد السلوك العصبي" else "DYNAMIC LIMITS",
        badgeColor = Color(0xFFF59E0B),
        title = if (isArabic) "قاعدة انكماش الوقت الديناميكية" else "Dynamic Shrinking Time Engine",
        subtitle = if (isArabic) "تتقلص الحصص المسموحة تلقائياً كلما ارتقيت من المستوى 1 إلى 100"
                   else "Behavioral quotas automatically tighten as your brain level ascends",
        icon = Icons.Default.HourglassTop
    ) {
        // Comparison Matrix
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (isArabic) "جدول انكماش الحصص بين المستوى 1 و 100:" else "Quota Tightening (Level 1 vs Level 100):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                QuotaRowComparison(
                    title = if (isArabic) "وسائل التواصل والبث" else "Social & Live Media",
                    emoji = "📱",
                    start = "6 hours (360m)",
                    end = "20 minutes!",
                    color = Color(0xFFEF4444)
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuotaRowComparison(
                    title = if (isArabic) "الألعاب والترفيه" else "Games & Entertainment",
                    emoji = "🎮",
                    start = "6 hours (360m)",
                    end = "40 minutes!",
                    color = Color(0xFFF59E0B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                QuotaRowComparison(
                    title = if (isArabic) "الإنتاجية والتعليم" else "Productivity & Learning",
                    emoji = "💼",
                    start = "Unlimited (480m)",
                    end = "60m Focused Hour",
                    color = Color(0xFF10B981)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TutorialBullet(
            icon = Icons.Default.Timer,
            title = if (isArabic) "حماية صارمة ضد الغش (Anti-Cheat)" else "1-Minute Anti-Cheat Guard",
            description = if (isArabic) "يتم احتساب وقت الواجهة الأمامي الحقيقي فقط. أي فتح عارض للتطبيق أقل من 60 ثانية يتم تجاهله لحمايتك."
                          else "Only real foreground time counts. Accidental opens under 60 continuous seconds are automatically discarded."
        )
    }
}

// ============================================================================
// STEP 4: Daily 1000-Point Buffer Pool & The 3 Outcomes
// ============================================================================
@Composable
private fun StepBufferPoolAndOutcomes(isArabic: Boolean) {
    var selectedOutcome by remember { mutableStateOf(DailyOutcomeType.CLEAN_SWEEP) }

    TutorialCardContent(
        badge = if (isArabic) "درع الأمان اليومي" else "DAILY EVALUATION",
        badgeColor = Color(0xFF10B981),
        title = if (isArabic) "المخزن المؤقت 1000 نقطة والنتائج الثلاث" else "1,000-Pt Buffer & The 3 Outcomes",
        subtitle = if (isArabic) "مخزن يومي يمتص التجاوزات قبل التأثير على مستواك"
                   else "Daily resilience buffer absorbs minor breaches before touching your level",
        icon = Icons.Default.Shield
    ) {
        // Interactive 3 Outcome Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutcomeTabButton(
                label = if (isArabic) "الحالة أ" else "Case A",
                sublabel = if (isArabic) "فوز ناصع" else "Clean Sweep",
                isSelected = selectedOutcome == DailyOutcomeType.CLEAN_SWEEP,
                color = Color(0xFF10B981),
                onClick = { selectedOutcome = DailyOutcomeType.CLEAN_SWEEP },
                modifier = Modifier.weight(1f)
            )
            OutcomeTabButton(
                label = if (isArabic) "الحالة ب" else "Case B",
                sublabel = if (isArabic) "تجاوز فردي" else "Single Breach",
                isSelected = selectedOutcome == DailyOutcomeType.SINGLE_BREACH,
                color = Color(0xFF38BDF8),
                onClick = { selectedOutcome = DailyOutcomeType.SINGLE_BREACH },
                modifier = Modifier.weight(1f)
            )
            OutcomeTabButton(
                label = if (isArabic) "الحالة ج" else "Case C",
                sublabel = if (isArabic) "تجاوزات متعددة" else "Multi-Breach",
                isSelected = selectedOutcome == DailyOutcomeType.MULTI_BREACH,
                color = Color(0xFFEF4444),
                onClick = { selectedOutcome = DailyOutcomeType.MULTI_BREACH },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Outcome Details Surface
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(
                1.dp,
                when (selectedOutcome) {
                    DailyOutcomeType.CLEAN_SWEEP -> Color(0xFF10B981)
                    DailyOutcomeType.SINGLE_BREACH -> Color(0xFF38BDF8)
                    DailyOutcomeType.MULTI_BREACH -> Color(0xFFEF4444)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                when (selectedOutcome) {
                    DailyOutcomeType.CLEAN_SWEEP -> {
                        Text(
                            text = if (isArabic) "🌟 الحالة أ: فوز ناصع (0 تجاوزات)" else "🌟 Case A: Clean Sweep (0 Breaches)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) "• تتابع الأيام +1 يوم\n• مكافأة التتابع المتصاعدة (+500 إلى +2,000 نقطة!)\n• ارتقاء فوري بالمستويات وتقدم نحو فقس البيض"
                                   else "• Streak +1 Day\n• Escalating Streak Bonus (+500 up to +2,000 pts!)\n• Unconstrained level up & egg incubation progress",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                    DailyOutcomeType.SINGLE_BREACH -> {
                        Text(
                            text = if (isArabic) "❄️ الحالة ب: تجاوز فردي (تطبيق واحد)" else "❄️ Case B: Single Breach (1 App)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) "• تجميد آمن للتتابع (لا زيادة ولا تصفير)\n• لا هبوط في المستوى\n• منح +500 نقطة أمان لمسبحك العصبي"
                                   else "• Streak FROZEN safely (no reset, no demotion)\n• Safe harbor protection\n• +500 safety points awarded to current pool",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                    DailyOutcomeType.MULTI_BREACH -> {
                        Text(
                            text = if (isArabic) "⚡ الحالة ج: تجاوزات متعددة (تطبيقين أو أكثر)" else "⚡ Case C: Multi-Breach (2+ Apps)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isArabic) "• خصم -50 نقطة لكل 10 دقائق تجاوز من مخزن الـ 1000 نقطة\n• تصفير التتابع إلى 0\n• إذا نفد المخزن بدون درع حماية: هبوط حاد في المستويات!"
                                   else "• Deducts -50 pts per 10-min block from 1000-pt buffer\n• Streak resets to 0\n• If buffer depletes without Aegis Shield: Unrestricted demotion!",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// STEP 5: Shield Management, Aegis Barrier & Demotion Immunity
// ============================================================================
@Composable
private fun StepShieldManagement(isArabic: Boolean) {
    var isShieldActivePreview by remember { mutableStateOf(true) }

    TutorialCardContent(
        badge = if (isArabic) "نظام الدفاع المطلق" else "SHIELD MANAGEMENT",
        badgeColor = Color(0xFF0284C7),
        title = if (isArabic) "إدارة الدروع والحصانة ضد الهبوط" else "Aegis Shield Management & Demotion Immunity",
        subtitle = if (isArabic) "حصن عصبي يمنع تدمير المستويات وتصفير السلسلة"
                   else "Impenetrable barrier against level demotions and breach penalties",
        icon = Icons.Default.Security
    ) {
        // Interactive Shield Forcefield Simulator
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isShieldActivePreview) Color(0xFF0369A1).copy(alpha = 0.2f) else Color(0xFF1E293B).copy(alpha = 0.4f),
            border = BorderStroke(
                1.5.dp,
                if (isShieldActivePreview) Color(0xFF38BDF8) else Color(0xFF475569)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isShieldActivePreview) "🛡️" else "⚪", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isShieldActivePreview) (if (isArabic) "حصن الحماية نشط" else "Aegis Shield Barrier ACTIVE")
                                       else (if (isArabic) "غير محمي (معرض للهبوط)" else "Unprotected (Vulnerable)"),
                                fontWeight = FontWeight.Bold,
                                color = if (isShieldActivePreview) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isShieldActivePreview) (if (isArabic) "حصانة تامة لمدة 24 ساعة" else "100% Demotion Immunity Enabled")
                                       else (if (isArabic) "أي تجاوز ينفد المخزن سيؤدي لهبوط مستواك" else "Buffer exhaustion causes instant demotion"),
                                fontSize = 10.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Button(
                        onClick = { isShieldActivePreview = !isShieldActivePreview },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isShieldActivePreview) Color(0xFF38BDF8) else Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp).testTag("tutorial_shield_toggle_btn")
                    ) {
                        Text(
                            text = if (isShieldActivePreview) (if (isArabic) "تعطيل" else "Disable") else (if (isArabic) "تفعيل الدرع" else "Deploy"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isShieldActivePreview) Color(0xFF0F172A) else Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TutorialBullet(
            icon = Icons.Default.Shield,
            title = if (isArabic) "نوعان من الدروع الأسطورية" else "2 Types of Aegis Shields",
            description = if (isArabic) "• درع 24 ساعة (🛡️): يمتص كل التجاوزات ويمنح حصانة تامة ليوم كامل.\n• درع الحصانة الأسبوعي (🔰): قلعة حصينة تدوم 7 أيام متواصلة."
                          else "• 24-Hour Shield (🛡️): Absorbs breaches and grants total immunity for 24h.\n• 1-Week Aegis Shield (🔰): Legendary fortress protecting for 7 full days."
        )

        TutorialBullet(
            icon = Icons.Default.LockReset,
            title = if (isArabic) "الاستهلاك التلقائي الذكي (Auto-Trigger)" else "Smart Auto-Consumption",
            description = if (isArabic) "إذا حدثت تجاوزات متعددة ونفد مخزن الـ 1000 نقطة، سيستهلك النظام تلقائياً درعاً من خزينة حقيبتك لوقف الهبوط وإنقاذ مستواك!"
                          else "If the 1000-pt buffer depletes during a multi-breach, your stored shield AUTO-CONSUMES to absorb the disaster and preserve your level!"
        )

        TutorialBullet(
            icon = Icons.Default.CheckCircle,
            title = if (isArabic) "التفعيل اليدوي المسبق" else "Manual Preemptive Activation",
            description = if (isArabic) "يمكنك فتح خزينة العناصر وتفعيل الدرع مسبقاً في الأيام المزدحمة التي تتوقع فيها استخداماً مرتفعاً لهاتفك."
                          else "Deploy a shield anytime from your Inventory Vault ahead of demanding days or long travel."
        )
    }
}

// ============================================================================
// STEP 6: Inventory Vault, Reward Items & Egg Incubation
// ============================================================================
@Composable
private fun StepInventoryAndEggs(isArabic: Boolean) {
    TutorialCardContent(
        badge = if (isArabic) "الخزينة والمكافآت" else "VAULT & EGGS",
        badgeColor = Color(0xFFFF7043),
        title = if (isArabic) "خزينة الجرعات والعناصر وبيوض المكافآت" else "Inventory Vault & Egg Rewards",
        subtitle = if (isArabic) "7 عناصر استراتيجية ومكافآت محطات التتابع كل 5 أيام"
                   else "7 consumable power items & mystery drops earned through discipline",
        icon = Icons.Default.Egg
    ) {
        // Items Showcase Row
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ItemBadgeMini("🛡️", "24h Shield", "درع 24س")
                ItemBadgeMini("🔰", "1-Wk Aegis", "درع أسبوعي")
                ItemBadgeMini("🧪", "Healing Potion", "جرعة تعافي")
                ItemBadgeMini("⚗️", "Legend Potion", "جرعة أسطورية")
                ItemBadgeMini("🗝️", "Skip Key", "مفتاح تخطي")
                ItemBadgeMini("🎁", "Mystery Box", "صندوق غامض")
                ItemBadgeMini("🥚", "Dragon Egg", "بيضة التنين")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TutorialBullet(
            icon = Icons.Default.AutoAwesome,
            title = if (isArabic) "محطات مكافآت التتابع كل 5 أيام" else "5-Day Streak Milestone Drops",
            description = if (isArabic) "كل 5 أيام متتالية من الانضباط (يوم 5، 10، 15...) تفتح لك هدية نادرة تضم دروعاً أو جرعات أو صناديق غامضة!"
                          else "Every 5 consecutive days of adherence drops rare loot including Shields, Potions, and Mystery Boxes!"
        )

        TutorialBullet(
            icon = Icons.Default.Egg,
            title = if (isArabic) "بيوض التنين والسمات النادرة" else "Dragon Eggs & Themes",
            description = if (isArabic) "حافظ على تتابع 3، 7، و 30 يوماً لفقس البيض البرونزي، الفضي، وبيضة التنين الذهبية التي تمنح دروعاً مؤكدة وسمات زجاجية نادرة."
                          else "Maintain 3, 7, and 30-day streaks to hatch Bronze, Silver, and Gold Dragon eggs for guaranteed shields and premium themes."
        )
    }
}

// ============================================================================
// STEP 7: Ascension Ready & Quick Replay
// ============================================================================
@Composable
private fun StepAscensionReady(isArabic: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rocket_scale"
    )

    TutorialCardContent(
        badge = if (isArabic) "أنت مستعد الآن!" else "MISSION READY",
        badgeColor = Color(0xFF10B981),
        title = if (isArabic) "أنت الآن جاهز لبدء التطور الذهني!" else "You Are Ready to Ascend!",
        subtitle = if (isArabic) "ملخص سريع وقواعد الانطلاق نحو المستوى 100"
                   else "Your roadmap to reclaiming deep focus and mastering neural power",
        icon = Icons.Default.RocketLaunch
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF10B981).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🚀 👑 🌌",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isArabic) "عقلك في المستوى 1 اليوم.. وجهتك هي المستوى 100!" else "Start at Level 1, conquer the journey to Level 100!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isArabic) "التزم بالحصص اليومية، احمِ تتابعك بالدروع، واختبر قوة التركيز العصبي التام."
                           else "Adhere to shrinking limits, shield your streak, and unlock the peak power of an uncompromised mind.",
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TutorialBullet(
            icon = Icons.Default.Info,
            title = if (isArabic) "إعادة هذا الدليل في أي وقت" else "Replay Anytime via Help (?)",
            description = if (isArabic) "يمكنك دائماً مراجعة هذا الدليل بالنقر على أيقونة المساعدة (?) في أعلى الشاشة الرئيسية أو عبر قائمة الإعدادات."
                          else "Revisit this interactive masterclass anytime via the Help (?) icon in the top header or within Settings."
        )
    }
}

// ============================================================================
// HELPER COMPONENTS FOR TUTORIAL
// ============================================================================
@Composable
private fun TutorialCardContent(
    badge: String,
    badgeColor: Color,
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(14.dp))

        content()
    }
}

@Composable
private fun TutorialBullet(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF1E293B),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun QuotaRowComparison(
    title: String,
    emoji: String,
    start: String,
    end: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
        }

        Text(
            text = "$start ➔ $end",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun OutcomeTabButton(
    label: String,
    sublabel: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.25f) else Color(0xFF0F172A),
        border = BorderStroke(
            1.dp,
            if (isSelected) color else Color(0xFF1E293B)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else Color(0xFF94A3B8)
            )
            Text(
                text = sublabel,
                fontSize = 9.sp,
                color = if (isSelected) Color.White else Color(0xFF64748B),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ItemBadgeMini(emoji: String, titleEn: String, titleAr: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.width(90.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = titleEn,
                fontSize = 9.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
