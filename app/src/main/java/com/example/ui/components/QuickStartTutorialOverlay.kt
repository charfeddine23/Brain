package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Shield
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.HabitPulseEngine
import com.example.ui.theme.AlertRed
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.ShieldCyan

/**
 * Interactive Liquid Glass Quick-Start Overlay Tutorial.
 * Guides the user through:
 * 1. The 100-Level Brain Evolution Engine & 1000-Point Pools.
 * 2. Flame Power & Strict Strike Penalties.
 * 3. The Neural Shield System with an interactive starter shield activation.
 * 4. The Egg Vault, Daily Quests & Detox Clinic.
 */
@Composable
fun QuickStartTutorialOverlay(
    isArabic: Boolean,
    brainLevel: Int,
    isShieldActive: Boolean,
    remainingShieldHours: Int,
    onActivateFirstShield: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 4

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF030712).copy(alpha = 0.88f))
                .padding(horizontal = dimensionResource(R.dimen.screen_padding), vertical = 24.dp)
                .testTag("quick_start_tutorial_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Main Glassmorphism Modal Card
            Surface(
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
                color = glassColors.surfaceColor.copy(alpha = 0.94f),
                border = BorderStroke(1.5.dp, glassColors.accentColor.copy(alpha = 0.6f)),
                modifier = Modifier
                    .widthIn(max = dimensionResource(R.dimen.screen_content_max_width))
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.card_padding))
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Header: Progress indicator + Step Count + Close (X) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Step Indicator Pills
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (step in 1..totalSteps) {
                                val isCurrent = step == currentStep
                                val isDone = step < currentStep
                                Box(
                                    modifier = Modifier
                                        .height(6.dp)
                                        .width(if (isCurrent) 24.dp else 10.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            when {
                                                isCurrent -> glassColors.accentColor
                                                isDone -> SafeGreen
                                                else -> Color.White.copy(alpha = 0.15f)
                                            }
                                        )
                                )
                            }
                        }

                        // Step Title
                        Text(
                            text = if (isArabic) "المرحلة $currentStep من $totalSteps" else "Step $currentStep of $totalSteps",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.accentColor
                        )

                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                                .testTag("tutorial_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Dynamic Animated Step Content ---
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {
                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut()
                            }
                        },
                        label = "tutorial_step_transition"
                    ) { step ->
                        when (step) {
                            1 -> TutorialStepOne(isArabic = isArabic, currentLevel = brainLevel)
                            2 -> TutorialStepTwo(isArabic = isArabic)
                            3 -> TutorialStepThree(
                                isArabic = isArabic,
                                isShieldActive = isShieldActive,
                                remainingHours = remainingShieldHours,
                                onActivateFirstShield = onActivateFirstShield
                            )
                            4 -> TutorialStepFour(isArabic = isArabic)
                            else -> TutorialStepOne(isArabic = isArabic, currentLevel = brainLevel)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- Bottom Navigation Actions: Back / Next / Skip ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back or Skip
                        if (currentStep > 1) {
                            OutlinedButton(
                                onClick = { currentStep-- },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                modifier = Modifier.testTag("tutorial_prev_btn")
                            ) {
                                Text(if (isArabic) "السابق" else "Back", fontSize = 13.sp)
                            }
                        } else {
                            OutlinedButton(
                                onClick = onDismiss,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                                modifier = Modifier.testTag("tutorial_skip_btn")
                            ) {
                                Text(if (isArabic) "تخطي" else "Skip", fontSize = 13.sp)
                            }
                        }

                        // Next or Finish
                        Button(
                            onClick = {
                                if (currentStep < totalSteps) {
                                    currentStep++
                                } else {
                                    onDismiss()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentStep == totalSteps) SafeGreen else glassColors.accentColor,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier.testTag("tutorial_next_btn")
                        ) {
                            Text(
                                text = when {
                                    currentStep < totalSteps -> if (isArabic) "التالي ➔" else "Next ➔"
                                    else -> if (isArabic) "ابدأ رحلتك 🚀" else "Launch App 🚀"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Step 1: Brain Evolution Engine (100 Levels + 1000-Point Pools)
 */
@Composable
private fun TutorialStepOne(isArabic: Boolean, currentLevel: Int) {
    val glassColors = LocalGlassColors.current
    var previewLevel by remember { mutableFloatStateOf(currentLevel.toFloat().coerceIn(1f, 100f)) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual Header Icon
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(glassColors.accentColor.copy(alpha = 0.18f))
                .border(1.5.dp, glassColors.accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = glassColors.accentColor,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isArabic) "محرك تطور الدماغ 100 مستوى" else "100-Level Brain Evolution Engine",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isArabic) {
                "عقلك الرقمي كائن حي يتنفس داخل التطبيق! يبدأ من المستوى 1 (عقل غارق في الضباب ونخر إدمان الشاشات) ويتطور حتى المستوى 100 (الوعي العصبي المطلق). كل مستوى يحتاج 1000 نقطة للارتقاء."
            } else {
                "Your neural cortex lives dynamically inside the app! Starting from Level 1 (Foggy Mind & doomscroll rot) up to Level 100 (The Ascended Singularity). Each level maintains a 1000-point threshold pool."
            },
            fontSize = 13.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Preview Slider
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.8f),
            border = BorderStroke(1.dp, glassColors.accentColor.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "تجربة محاكاة التطور:" else "Simulate Evolution:",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${HabitPulseEngine.getRankBadgeEmoji(previewLevel.toInt())} ${if (isArabic) "المستوى" else "Lvl"} ${previewLevel.toInt()}: ${HabitPulseEngine.getRankTitle(previewLevel.toInt(), isArabic)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.accentColor
                    )
                }

                Slider(
                    value = previewLevel,
                    onValueChange = { previewLevel = it },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = glassColors.accentColor,
                        activeTrackColor = glassColors.accentColor,
                        inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.testTag("tutorial_level_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isArabic) "مستوى 1: تعفن وضباب" else "Lvl 1: Decayed Fog", fontSize = 10.sp, color = AlertRed)
                    Text(if (isArabic) "مستوى 100: وعي خارق" else "Lvl 100: Singularity", fontSize = 10.sp, color = glassColors.accentColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic Shrinking Rule Callout
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) {
                        "قاعدة انكماش الوقت: كلما زاد مستواك، تتقلص حدود وسائل التواصل تلقائياً من 6 ساعات إلى 20 دقيقة!"
                    } else {
                        "Dynamic Shrinking Rule: Higher levels tighten social limits from 6 hours down to 20 focused mins!"
                    },
                    fontSize = 11.sp,
                    color = Color(0xFFBAE6FD),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/**
 * Step 2: Flame Power & Strict Strike Penalties
 */
@Composable
private fun TutorialStepTwo(isArabic: Boolean) {
    val glassColors = LocalGlassColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "flame_tutorial_anim")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_pulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pulsing Flame Icon
        Box(
            modifier = Modifier
                .size(68.dp)
                .scale(pulse)
                .clip(CircleShape)
                .background(FlameOrange.copy(alpha = 0.2f))
                .border(1.5.dp, FlameOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = FlameOrange,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isArabic) "طاقة الشعلة وقواعد العقوبات الصارمة" else "Flame Power & Strict Strike Rules",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isArabic) {
                "طاقة شعلتك تمثل قوة إرادتك الرقمية (100 نقطة). تجاوز حدود التطبيقات يفعل عقوبات تصاعدية قاسية تهدد تتابعك ومستواك:"
            } else {
                "Your Flame Power measures digital willpower (100 points). Breaching daily app thresholds triggers progressive penalties:"
            },
            fontSize = 13.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // The 3 Breaches Breakdown Cards
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BreachExplanationRow(
                badge = "1st",
                title = if (isArabic) "التجاوز الأول: إنذار أولي" else "1st Breach: Warning",
                penalty = "-10 Flame Pts",
                color = Color(0xFFF59E0B),
                isArabic = isArabic
            )
            BreachExplanationRow(
                badge = "2nd",
                title = if (isArabic) "التجاوز الثاني: خطر حرج" else "2nd Breach: Critical Risk",
                penalty = "-20 Flame Pts",
                color = FlameOrange,
                isArabic = isArabic
            )
            BreachExplanationRow(
                badge = "3rd",
                title = if (isArabic) "التجاوز الثالث: كارثة عصبية" else "3rd Breach: Neural Catastrophe",
                penalty = if (isArabic) "تصفير التتابع تماماً (0 أيام)" else "STREAK RESET TO 0!",
                color = AlertRed,
                isArabic = isArabic
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Anti-Cheat Confirmation
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF10B981).copy(alpha = 0.15f),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛡️", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isArabic) {
                        "تتبع دقيق ومضاد للغش: يتم احتساب الوقت النشط في الواجهة فقط، ويتم تجاهل الجلسات القصيرة التي تقل عن 60 ثانية!"
                    } else {
                        "Precision Anti-Cheat: Foreground active time only; continuous sessions under 60 seconds are discarded."
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF6EE7B7)
                )
            }
        }
    }
}

@Composable
private fun BreachExplanationRow(badge: String, title: String, penalty: String, color: Color, isArabic: Boolean) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.25f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Text(penalty, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

/**
 * Step 3: Neural Shield System & Interactive First Shield Activation
 */
@Composable
private fun TutorialStepThree(
    isArabic: Boolean,
    isShieldActive: Boolean,
    remainingHours: Int,
    onActivateFirstShield: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    val infiniteTransition = rememberInfiniteTransition(label = "shield_tutorial_anim")
    val shieldGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_glow"
    )
    val shieldPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing Animated Shield Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(shieldPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(ShieldCyan.copy(alpha = 0.45f * shieldGlowAlpha), Color.Transparent)
                    )
                )
                .border(
                    2.dp,
                    ShieldCyan.copy(alpha = shieldGlowAlpha),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = ShieldCyan,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isArabic) "درع الحماية العصبي وحصانة الانضباط" else "Neural Shield & Demotion Immunity",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isArabic) {
                "الدرع هو خط دفاعك الاستراتيجي! عند تفعيل الدرع، إذا تجاوزت الحد في أي تطبيق بالخطأ، يتحطم الدرع ويمتص العقوبة بالكامل، ويبقى تتابعك ونقاط الشعلة سالمين دون أي خصم!"
            } else {
                "Shields are your ultimate tactical defense! When active, if you breach a daily threshold, the shield shatters and absorbs the strike impact completely—preserving flame power and streak!"
            },
            fontSize = 13.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- INTERACTIVE ACTION: Equip & Activate First Shield ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isShieldActive) SafeGreen.copy(alpha = 0.15f) else ShieldCyan.copy(alpha = 0.15f),
            border = BorderStroke(
                1.5.dp,
                if (isShieldActive) SafeGreen else ShieldCyan.copy(alpha = shieldGlowAlpha)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isShieldActive) {
                    // Shield Active Feedback
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "درع الحماية نشط حالياً!" else "Starter Shield is ACTIVE!",
                            fontWeight = FontWeight.ExtraBold,
                            color = SafeGreen,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isArabic) {
                            "عقلك العصبي محصن ضد أول تجاوز لمدة $remainingHours ساعة قادمة!"
                        } else {
                            "Neural cortex protected from breach demotion for next $remainingHours hours!"
                        },
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Interactive Button to Equip First Shield
                    Text(
                        text = if (isArabic) "🎁 هدية البداية: درع حماية 24 ساعة جاهز!" else "🎁 Starter Gift: 24h Defense Shield Ready!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShieldCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) "اضغط الزر أدناه لتفعيل درعك الأول الآن وتجربة الحصانة:" else "Tap below to equip your first shield & activate live immunity:",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onActivateFirstShield,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ShieldCyan,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(shieldPulse)
                            .testTag("activate_starter_shield_btn")
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "🛡️ استلم وفعل درعك الأول الآن!" else "🛡️ Equip & Activate First Shield Now!",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Step 4: Egg Vault, Quests & Detox Clinic
 */
@Composable
private fun TutorialStepFour(isArabic: Boolean) {
    val glassColors = LocalGlassColors.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual Header Icon
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFFA855F7).copy(alpha = 0.2f))
                .border(1.5.dp, Color(0xFFA855F7), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFA855F7),
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isArabic) "خزينة البيض والمهام والعيادة العصبية" else "Egg Vault, Quests & Neural Clinic",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isArabic) {
                "حافظ على تتابعك اليومي لفقس بيض المكافآت النادر والحصول على عناصر الخزينة الـ 7 لترقية عقلك:"
            } else {
                "Maintain daily consistency to hatch rare mystery eggs and unlock all 7 vault items to power up:"
            },
            fontSize = 13.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Mini Showcase Grid of Eggs and Features
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VaultFeatureCard(
                emoji = "🥉",
                title = if (isArabic) "بيضة برونز" else "Bronze Egg",
                sub = if (isArabic) "تتابع 3 أيام" else "3-Day Streak",
                modifier = Modifier.weight(1f)
            )
            VaultFeatureCard(
                emoji = "🥈",
                title = if (isArabic) "بيضة فضة" else "Silver Egg",
                sub = if (isArabic) "تتابع 7 أيام" else "7-Day Streak",
                modifier = Modifier.weight(1f)
            )
            VaultFeatureCard(
                emoji = "🐉",
                title = if (isArabic) "بيضة التنين" else "Dragon Egg",
                sub = if (isArabic) "تتابع 30 يوماً" else "30-Day Streak",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Neural Clinic Note
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.8f),
            border = BorderStroke(1.dp, Color(0xFF00FFA3).copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🧪", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isArabic) "العيادة العصبية (Clinic):" else "Brain Clinic:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00FFA3)
                    )
                    Text(
                        text = if (isArabic) {
                            "استخدم جرعات التطهير والشفاء لإزالة التعفن الدماغي واستعادة صفاء عقلك في أي وقت!"
                        } else {
                            "Use detox & healing potions from your vault to purge brain rot and restore mental clarity anytime!"
                        },
                        fontSize = 10.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }
    }
}

@Composable
private fun VaultFeatureCard(emoji: String, title: String, sub: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            Text(sub, fontSize = 9.sp, color = Color.Gray)
        }
    }
}
