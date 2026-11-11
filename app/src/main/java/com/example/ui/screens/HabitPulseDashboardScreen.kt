package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.InventoryItemType
import com.example.model.QuestItem
import com.example.model.ShieldEvent
import com.example.ui.components.DynamicBrainCanvas
import com.example.ui.components.GlowingRewardsNavChip
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.LocalGlassColors
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

@Composable
fun HabitPulseDashboardScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel,
    onNavigateToLimits: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {}
) {
    val glassColors = LocalGlassColors.current
    val isArabic = uiState.isArabic
    val scrollState = rememberScrollState()

    var showInventorySheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Top Header Bar: Rewards Glow Chip, Flame Strike ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isArabic) "نبض الدماغ" else "Brain Pulse",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = glassColors.onSurfaceColor
                )
                Text(
                    text = if (isArabic) "محرك تطور الدماغ 100 مستوى" else "100-Level Brain Evolution Engine",
                    fontSize = 12.sp,
                    color = glassColors.onSurfaceVariantColor
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Dynamic Glowing Rewards Icon (on the LEFT side of the Flame chip!)
                GlowingRewardsNavChip(
                    onClick = onNavigateToRewards,
                    isArabic = isArabic,
                    streakDays = uiState.streakDays,
                    canClaimMilestone = uiState.isMilestoneRewardClaimable,
                    eggStates = uiState.eggStates,
                    isBlockedToday = uiState.isEggBlockedToday
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Beautifully Styled Flame Streak Chip (Showing correct Streak Days)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x33FF5722),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                        )
                    ),
                    shadowElevation = 4.dp,
                    modifier = Modifier.testTag("flame_streak_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FF5722))
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF7043),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${uiState.streakDays}",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFE0B2),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. Shield Immunity Status Banner ---
        if (uiState.isImmunityActive) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0284C7).copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                modifier = Modifier.fillMaxWidth().testTag("shield_active_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "حصن الحماية نشط (Aegis Active)" else "Aegis Shield Barrier Active",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE0F2FE),
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isArabic) "متبقي ${uiState.immunityRemainingHours} ساعة من الحصانة التامة ضد هبوط المستوى"
                                   else "${uiState.immunityRemainingHours} hours of complete demotion immunity remaining",
                            color = Color(0xFFBAE6FD),
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // --- 3. Notification / Snack Event Bar ---
        AnimatedVisibility(
            visible = uiState.itemEventMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = glassColors.accentColor.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, glassColors.accentColor),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = glassColors.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.itemEventMessage ?: "",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.clearItemMessage() },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // --- 4. The 100-Level Brain Evolution Canvas (Core Hero) ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Elegant, Compact & Luxurious Level & Rank Container
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    // Sleek & Compact Current Level Pill (shows current level only, elegant & small)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = glassColors.accentColor.copy(alpha = 0.14f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            glassColors.accentColor.copy(alpha = 0.55f)
                        ),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = uiState.rankBadge,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "مستوى ${uiState.brainLevel}" else "LVL ${uiState.brainLevel}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = glassColors.accentColor,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Luxurious, Sleek & Compact Rank Title Container (فخم وصغير وأنيق)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(
                                    glassColors.accentColor.copy(alpha = 0.6f),
                                    Color(0xFFA855F7).copy(alpha = 0.5f),
                                    Color(0xFF38BDF8).copy(alpha = 0.6f)
                                )
                            )
                        ),
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(glassColors.accentColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.rankTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.3.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Generous spacing above brain to prevent level/title overlap
                Spacer(modifier = Modifier.height(18.dp))

                // The Organic Interactive Canvas Brain (Proportionately sized)
                DynamicBrainCanvas(
                    level = uiState.brainLevel,
                    rotPercentage = uiState.brainRotPercentage,
                    isShieldActive = uiState.isImmunityActive,
                    isHealing = uiState.isHealingActive,
                    isGlitching = uiState.isGlitchingActive,
                    modifier = Modifier
                        .size(210.dp)
                        .testTag("dynamic_brain_canvas")
                )

                // Generous spacing below brain so metrics and pool bars do not collide
                Spacer(modifier = Modifier.height(20.dp))

                // 1000-Point Level Pool & Buffer Pool Metrics (Shifted down)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "مسبح المستوى الحالي (1000 نقطة)" else "Current Level Pool (1000 pts)",
                            fontSize = 11.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                        Text(
                            text = "${uiState.pointsInCurrentLevel} / 1000 pts (${uiState.totalPoints} total)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.accentColor
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { uiState.levelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = glassColors.accentColor,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1000-Point Buffer Pool Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛡️", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "المخزن المؤقت اليومي ضد التجاوزات:" else "Daily 1000-Pt Breach Buffer:",
                                fontSize = 11.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                        }
                        Text(
                            text = "${uiState.dailyBufferPool} / 1000 pts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.dailyBufferPool > 500) Color(0xFF10B981) else if (uiState.dailyBufferPool > 0) Color(0xFFF59E0B) else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 5. 5-Day Streak Milestone Bar ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (isArabic) "سلسلة الانضباط: ${uiState.streakDays} أيام" else "Streak: ${uiState.streakDays} Days",
                                fontWeight = FontWeight.Bold,
                                color = glassColors.onSurfaceColor,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isArabic) "محطة المكافأة القادمة: يوم ${uiState.nextMilestoneTarget}" 
                                       else "Next Item Milestone: Day ${uiState.nextMilestoneTarget}",
                                fontSize = 11.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                        }
                    }

                    if (uiState.isMilestoneRewardClaimable) {
                        Button(
                            onClick = { viewModel.claimStreakMilestoneReward() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("claim_milestone_btn")
                        ) {
                            Text(if (isArabic) "استلم المكافأة 🎁" else "Claim Drop 🎁", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 6. Dynamic Shrinking Time Rule Section ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) "قاعدة انكماش الوقت الديناميكية" else "Dynamic Shrinking Time Engine",
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isArabic) "تتقلص الحدود تلقائياً مع صعودك من المستوى 1 إلى 100" 
                                   else "Limits tighten automatically as Brain Level scales (1 -> 100)",
                            fontSize = 11.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = glassColors.accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Category Shrink Benchmarks
                ShrinkCategoryRow(
                    categoryName = if (isArabic) "وسائل التواصل والبث (Social/Video)" else "Social Media & Streams",
                    emoji = "📱",
                    currentMins = uiState.thresholds.filter { it.appCategory == AppCategory.SOCIAL_MEDIA }.sumOf { it.currentMinutes },
                    limitMins = com.example.model.HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.SOCIAL_MEDIA, uiState.brainLevel),
                    targetShrink = "20m @ Lvl 100",
                    isArabic = isArabic
                )

                Spacer(modifier = Modifier.height(8.dp))

                ShrinkCategoryRow(
                    categoryName = if (isArabic) "الألعاب والترفيه (Games/Fun)" else "Games & Entertainment",
                    emoji = "🎮",
                    currentMins = uiState.thresholds.filter { it.appCategory == AppCategory.ENTERTAINMENT_GAMES }.sumOf { it.currentMinutes },
                    limitMins = com.example.model.HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.ENTERTAINMENT_GAMES, uiState.brainLevel),
                    targetShrink = "40m @ Lvl 100",
                    isArabic = isArabic
                )

                Spacer(modifier = Modifier.height(8.dp))

                ShrinkCategoryRow(
                    categoryName = if (isArabic) "الإنتاجية والتعلم (Productive Focus)" else "Productivity & Deep Work",
                    emoji = "💼",
                    currentMins = uiState.thresholds.filter { it.appCategory == AppCategory.PRODUCTIVITY_TOOLS }.sumOf { it.currentMinutes },
                    limitMins = com.example.model.HabitPulseEngine.calculateDynamicTimeLimit(AppCategory.PRODUCTIVITY_TOOLS, uiState.brainLevel),
                    targetShrink = "60m Focused Goal",
                    isArabic = isArabic
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Anti-Cheat confirmation badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isArabic) "✓ حماية ضد الغش: تتبع الوقت النشط في الواجهة فقط • الجلسات الأقل من دقيقة يتم تجاهلها" 
                               else "✓ Anti-Cheat: Active foreground time only • Opens under 60 continuous seconds discarded",
                        fontSize = 10.sp,
                        color = Color(0xFF34D399),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 6B. The 3 Daily Evaluation Outcomes (Clean Sweep / Single Breach / Multi-Breach) ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().testTag("daily_evaluation_card"),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with title and Evaluate button
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(glassColors.accentColor.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = glassColors.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "نتائج التقييم اليومي الثلاث" else "3 Daily Evaluation Outcomes",
                                fontWeight = FontWeight.Bold,
                                color = glassColors.onSurfaceColor,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (isArabic) "ميكانيكا نهاية اليوم وتأثيرها على المخزن والدروع" else "End-of-day mechanics & buffer impact",
                                fontSize = 11.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.triggerDailyEvaluation() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = glassColors.accentColor,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("evaluate_day_btn")
                    ) {
                        Text(
                            text = if (isArabic) "تقييم اليوم" else "Evaluate Day",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Three distinct, elegant mini-cards showing the 3 cases
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Case A: Clean Sweep
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isArabic) "أ: فوز ناصع" else "A: Clean Sweep",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic) "0 تجاوزات (+1 ستريك)" else "0 breaches (Streak+1)",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
                    }

                    // Case B: Single Breach
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF38BDF8).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isArabic) "ب: تجاوز فردي" else "B: Single Breach",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic) "تجميد الستريك (+500)" else "Freeze streak (+500)",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
                    }

                    // Case C: Multi-Breach
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isArabic) "ج: تجاوزات متعددة" else "C: Multi-Breach",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic) "خصم المخزن أو الدرع" else "Buffer penalty / shield",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
                    }
                }

                if (uiState.latestDailyOutcome != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val outcome = uiState.latestDailyOutcome
                    val outcomeColor = when (outcome.outcomeType) {
                        com.example.model.DailyOutcomeType.CLEAN_SWEEP -> Color(0xFF10B981)
                        com.example.model.DailyOutcomeType.SINGLE_BREACH -> Color(0xFF38BDF8)
                        com.example.model.DailyOutcomeType.MULTI_BREACH -> if (outcome.wasShieldConsumed) Color(0xFFF59E0B) else Color(0xFFEF4444)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = outcomeColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, outcomeColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isArabic) outcome.summaryAr else outcome.summaryEn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.dismissDailyOutcome() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) "المخزن المؤقت المتبقي: ${outcome.bufferPoolRemaining} / 1000 نقطة"
                                       else "Remaining Daily Buffer: ${outcome.bufferPoolRemaining} / 1000 pts",
                                fontSize = 11.sp,
                                color = outcomeColor
                            )
                        }
                    }
                }
            }
        }

        // --- 7. Inventory & Potions Vault Section ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎒", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "خزينة الجرعات والعناصر (7 عناصر)" else "Inventory & Potions Vault",
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor,
                            fontSize = 15.sp
                        )
                    }

                    Text(
                        text = "${uiState.inventory.values.sum()} ${if (isArabic) "عنصر" else "items"}",
                        fontSize = 12.sp,
                        color = glassColors.accentColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Horizontal scrollable cards for all 7 reward items
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InventoryItemType.entries.forEach { item ->
                        val count = uiState.inventory[item] ?: 0
                        InventoryItemCard(
                            item = item,
                            count = count,
                            isArabic = isArabic,
                            onUse = { viewModel.useInventoryItem(item) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 8. Daily & Weekly Quests Section ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isArabic) "المهمات اليومية والأسبوعية" else "Daily & Weekly Quests",
                    fontWeight = FontWeight.Bold,
                    color = glassColors.onSurfaceColor,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isArabic) "المهمات اليومية (تمنح بيضة يومية)" else "Daily Quests (Awards Daily Egg)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = glassColors.accentColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                uiState.dailyQuests.forEach { quest ->
                    QuestRow(
                        quest = quest,
                        isArabic = isArabic,
                        hasSkipKey = (uiState.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0) > 0,
                        onComplete = { viewModel.completeQuestManually(quest.id, isWeekly = false) },
                        onUseSkipKey = { viewModel.useInventoryItem(InventoryItemType.DAILY_QUEST_SKIP_KEY) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isArabic) "المهمات الأسبوعية الشاقة (تمنح بيضة التنين)" else "Hardcore Weekly Quests (Awards Dragon Egg)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFA855F7)
                )

                Spacer(modifier = Modifier.height(6.dp))

                uiState.weeklyQuests.forEach { quest ->
                    QuestRow(
                        quest = quest,
                        isArabic = isArabic,
                        hasSkipKey = (uiState.inventory[InventoryItemType.WEEKLY_QUEST_SKIP_KEY] ?: 0) > 0,
                        onComplete = { viewModel.completeQuestManually(quest.id, isWeekly = true) },
                        onUseSkipKey = { viewModel.useInventoryItem(InventoryItemType.WEEKLY_QUEST_SKIP_KEY) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ShrinkCategoryRow(
    categoryName: String,
    emoji: String,
    currentMins: Int,
    limitMins: Int,
    targetShrink: String,
    isArabic: Boolean
) {
    val progress = if (limitMins <= 0) 0f else (currentMins.toFloat() / limitMins.toFloat()).coerceIn(0f, 1f)
    val isOver = currentMins >= limitMins && limitMins > 0

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(categoryName, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
            }

            Text(
                text = "$currentMins / $limitMins m ($targetShrink)",
                fontSize = 11.sp,
                color = if (isOver) Color(0xFFEF4444) else Color(0xFF94A3B8),
                fontWeight = if (isOver) FontWeight.Bold else FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (isOver) Color(0xFFEF4444) else Color(0xFF38BDF8),
            trackColor = Color(0xFF1E293B)
        )
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItemType,
    count: Int,
    isArabic: Boolean,
    onUse: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(item.rarity.colorHex).copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(item.rarity.colorHex).copy(alpha = 0.5f)),
        modifier = Modifier.width(145.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(item.iconEmoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isArabic) item.titleAr else item.titleEn,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = if (isArabic) item.rarity.labelAr else item.rarity.labelEn,
                fontSize = 9.sp,
                color = Color(item.rarity.colorHex)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = "x$count",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Button(
                onClick = onUse,
                enabled = count > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(item.rarity.colorHex),
                    disabledContainerColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(30.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text(
                    text = if (count > 0) (if (isArabic) "استخدام" else "Use") else (if (isArabic) "فارغ" else "Empty"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun QuestRow(
    quest: QuestItem,
    isArabic: Boolean,
    hasSkipKey: Boolean,
    onComplete: () -> Unit,
    onUseSkipKey: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (quest.isCompleted) Color(0x1A10B981) else Color(0xFF1E293B).copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (quest.isCompleted) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFF334155)
        ),
        onClick = onComplete,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        if (quest.isCompleted) Color(0xFF10B981).copy(alpha = 0.2f)
                        else Color.White.copy(alpha = 0.05f)
                    )
            ) {
                Icon(
                    imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (quest.isCompleted) "Completed" else "Incomplete",
                    tint = if (quest.isCompleted) Color(0xFF10B981) else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) quest.titleAr else quest.titleEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (quest.isCompleted) Color(0xFF34D399) else Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (quest.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isArabic) "منجز ✓" else "DONE ✓",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isArabic) quest.descriptionAr else quest.descriptionEn,
                    fontSize = 11.sp,
                    color = if (quest.isCompleted) Color(0xFF94A3B8) else Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (quest.isCompleted) (if (isArabic) "تم استلام +${quest.xpReward} XP بنجاح" else "+${quest.xpReward} XP Claimed")
                           else (if (isArabic) "انقر للإنجاز المباشر (+${quest.xpReward} XP)" else "Tap to complete (+${quest.xpReward} XP)"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (quest.isCompleted) Color(0xFF10B981) else Color(0xFF38BDF8)
                )
            }

            if (!quest.isCompleted && hasSkipKey) {
                IconButton(
                    onClick = onUseSkipKey,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "Skip Key",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
