package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
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
import com.example.model.HabitPulseEngine
import com.example.model.InventoryItemType
import com.example.model.QuestItem
import com.example.model.ShieldEvent
import com.example.ui.components.DynamicBrainCanvas
import com.example.ui.components.FireParticleContainer
import com.example.ui.components.GlowingRewardsNavChip
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.QuestCodexDialog
import com.example.ui.components.StreakGuideDialog
import com.example.ui.theme.LocalGlassColors
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

@Composable
fun HabitPulseDashboardScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel,
    onNavigateToLimits: () -> Unit = {},
    onNavigateToRewards: () -> Unit = {},
    onNavigateToQuests: () -> Unit = {}
) {
    val glassColors = LocalGlassColors.current
    val isArabic = uiState.isArabic
    val scrollState = rememberScrollState()

    var showStreakGuideDialog by remember { mutableStateOf(false) }
    var showQuestCodexDialog by remember { mutableStateOf(false) }

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

                // Beautifully Styled Flame Streak Chip matching GlowingRewardsNavChip height (34.dp)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { showStreakGuideDialog = true }
                        .testTag("flame_streak_chip")
                ) {
                    FireParticleContainer(
                        streakDays = uiState.streakDays
                    ) {
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
                            modifier = Modifier.height(34.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x33FF5722))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = if (isArabic) "اضغط لشرح كيفية الحصول على السترايك" else "Tap to learn how to earn streaks",
                                        tint = Color(0xFFFF7043),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${uiState.streakDays}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFE0B2),
                                    fontSize = 13.sp
                                )
                            }
                        }
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
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Elegant, Compact & Luxurious Level & Rank Container (Shifted close to top edge)
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

                // Generous spacing above brain to prevent level/title overlap (clears title completely)
                Spacer(modifier = Modifier.height(34.dp))

                // The Organic Interactive Canvas Brain (Proportionately sized with complete clearance)
                DynamicBrainCanvas(
                    level = uiState.brainLevel,
                    rotPercentage = uiState.brainRotPercentage,
                    isShieldActive = uiState.isImmunityActive,
                    isHealing = uiState.isHealingActive,
                    isGlitching = uiState.isGlitchingActive,
                    modifier = Modifier
                        .size(198.dp)
                        .testTag("dynamic_brain_canvas")
                )

                // Generous spacing below brain so metrics and pool bars do not collide
                Spacer(modifier = Modifier.height(22.dp))

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
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .clickable { showStreakGuideDialog = true }
                .testTag("streak_milestone_card"),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🔥", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isArabic) "سلسلة الانضباط: ${uiState.streakDays} أيام" else "Streak: ${uiState.streakDays} Days",
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.onSurfaceColor,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = if (isArabic) "شرح السترايك" else "Streak Rules",
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 6B. The 3 Daily Evaluation Outcomes (Clean Sweep / Single Breach / Multi-Breach) ---
        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth().testTag("daily_evaluation_card"),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                // Header with title and Evaluate button (Compact & Balanced)
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
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(glassColors.accentColor.copy(alpha = 0.14f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = glassColors.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isArabic) "نتائج التقييم اليومي" else "Daily Evaluation",
                                fontWeight = FontWeight.Bold,
                                color = glassColors.onSurfaceColor,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isArabic) "يتم التقييم تلقائياً عند نهاية كل يوم" else "Evaluates automatically at day's end",
                                fontSize = 10.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = glassColors.accentColor.copy(alpha = 0.14f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, glassColors.accentColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isArabic) "تلقائي ✓" else "Auto ✓",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.accentColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Three compact, harmonious mini-cards (صغيرة وأنيقة ومتناسقة)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Case A: Clean Sweep
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isArabic) "أ: فوز ناصع" else "A: Clean",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399)
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = if (isArabic) "0 تجاوز (+1 يوم)" else "0 breaches (+1)",
                                fontSize = 8.5.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                maxLines = 1
                            )
                        }
                    }

                    // Case B: Buffer Survived (> 0 left)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF38BDF8).copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isArabic) "ب: نجا المخزن" else "B: Protected",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = if (isArabic) "> 0 متبقي (+1 يوم)" else "> 0 left (+1)",
                                fontSize = 8.5.sp,
                                color = Color.White.copy(alpha = 0.75f),
                                maxLines = 1
                            )
                        }
                    }

                    // Case C: Buffer Exhausted (0 left -> Reset)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEF4444).copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.35f)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isArabic) "ج: نفد المخزن" else "C: Drained",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF87171)
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = if (isArabic) "0 متبقي (تصفير)" else "0 left (Reset)",
                                fontSize = 8.5.sp,
                                color = Color.White.copy(alpha = 0.75f),
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

        // --- 7. Daily & Weekly Quests Section ---
        val totalAllQuestsCount = uiState.allDailyQuests.size + uiState.allWeeklyQuests.size
        val completedAllQuestsCount = uiState.allDailyQuests.count { it.isCompleted } + uiState.allWeeklyQuests.count { it.isCompleted }

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "المهمات اليومية والأسبوعية" else "Daily & Weekly Quests",
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isArabic) "المنجز: $completedAllQuestsCount / $totalAllQuestsCount • استبدال تلقائي"
                                   else "Cleared: $completedAllQuestsCount / $totalAllQuestsCount • Auto-Rotation",
                            fontSize = 10.5.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                    }

                    // Action buttons: 300 Quests Hub + Codex
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = onNavigateToQuests,
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0A1628),
                            border = androidx.compose.foundation.BorderStroke(
                                1.2.dp,
                                Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF38BDF8)))
                            ),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("open_300_quests_screen_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚡", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "300 مهمة" else "300 Hub",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }

                        // RPG Quest Codex Button showing all pending vs completed quests
                        Surface(
                            onClick = { showQuestCodexDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF0F172A),
                            border = androidx.compose.foundation.BorderStroke(
                                1.2.dp,
                                Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFFA855F7)))
                            ),
                            modifier = Modifier
                                .height(38.dp)
                                .testTag("open_quest_codex_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = if (isArabic) "عرض كل المهام" else "View All Quests",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isArabic) "كل المهام" else "All Quests",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isArabic) "المهمات اليومية (تمنح بيضة يومية)" else "Daily Quests (Awards Daily Egg)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = glassColors.accentColor
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (uiState.dailyQuests.isEmpty()) {
                    QuestPoolClearedBanner(
                        title = if (isArabic) "تم إنجاز كافة المهام اليومية بنجاح!" else "All Daily Quests Cleared!",
                        subtitle = if (isArabic) "تجدد الدورة اليومية بعد ${HabitPulseEngine.formatCountdown(millisUntilNextMidnight(uiState.currentTimeMillis))}"
                                   else "Next daily rotation in ${HabitPulseEngine.formatCountdown(millisUntilNextMidnight(uiState.currentTimeMillis))}",
                        accent = Color(0xFF10B981)
                    )
                } else {
                    uiState.dailyQuests.forEach { quest ->
                        QuestRow(
                            quest = quest,
                            currentTimeMillis = uiState.currentTimeMillis,
                            isArabic = isArabic,
                            hasSkipKey = (uiState.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0) > 0,
                            onUseSkipKey = { viewModel.skipQuestWithKey(quest.id, isWeekly = false) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isArabic) "المهمات الأسبوعية الشاقة (تمنح بيضة التنين)" else "Hardcore Weekly Quests (Awards Dragon Egg)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFA855F7)
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (uiState.weeklyQuests.isEmpty()) {
                    QuestPoolClearedBanner(
                        title = if (isArabic) "تم إنجاز كافة المهام الأسبوعية الأسطورية!" else "All Weekly Quests Cleared!",
                        subtitle = if (isArabic) "بداية أسبوع جديد خلال ${HabitPulseEngine.formatCountdown(millisUntilNextWeek(uiState.currentTimeMillis))}"
                                   else "Next weekly cycle in ${HabitPulseEngine.formatCountdown(millisUntilNextWeek(uiState.currentTimeMillis))}",
                        accent = Color(0xFFA855F7)
                    )
                } else {
                    uiState.weeklyQuests.forEach { quest ->
                        QuestRow(
                            quest = quest,
                            currentTimeMillis = uiState.currentTimeMillis,
                            isArabic = isArabic,
                            hasSkipKey = (uiState.inventory[InventoryItemType.WEEKLY_QUEST_SKIP_KEY] ?: 0) > 0,
                            onUseSkipKey = { viewModel.skipQuestWithKey(quest.id, isWeekly = true) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showStreakGuideDialog) {
        StreakGuideDialog(
            streakDays = uiState.streakDays,
            dailyBufferPool = uiState.dailyBufferPool,
            nextMilestoneTarget = uiState.nextMilestoneTarget,
            isShieldActive = uiState.isImmunityActive,
            isArabic = isArabic,
            onDismiss = { showStreakGuideDialog = false }
        )
    }

    if (showQuestCodexDialog) {
        QuestCodexDialog(
            allDailyQuests = uiState.allDailyQuests,
            allWeeklyQuests = uiState.allWeeklyQuests,
            activeDailyIds = uiState.dailyQuests.map { it.id }.toSet(),
            activeWeeklyIds = uiState.weeklyQuests.map { it.id }.toSet(),
            currentTimeMillis = uiState.currentTimeMillis,
            hasDailySkipKey = (uiState.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0) > 0,
            hasWeeklySkipKey = (uiState.inventory[InventoryItemType.WEEKLY_QUEST_SKIP_KEY] ?: 0) > 0,
            isArabic = isArabic,
            onSkipQuestWithKey = { questId, isWeekly -> viewModel.skipQuestWithKey(questId, isWeekly) },
            onDismiss = { showQuestCodexDialog = false }
        )
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
    val percentage = if (limitMins <= 0) 0 else ((currentMins.toFloat() / limitMins.toFloat()) * 100).toInt()
    val isOver = limitMins > 0 && currentMins >= limitMins
    val overtimePercent = if (isOver && limitMins > 0) percentage - 100 else 0
    val overtimeMins = (currentMins - limitMins).coerceAtLeast(0)

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isOver) Color(0x1DEF4444) else Color(0xFF1E293B).copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isOver) Color(0x66EF4444) else Color(0x22FFFFFF)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
            // Header Row: Category on left, Used / Limit & Badges on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = categoryName,
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "$currentMins / $limitMins " + (if (isArabic) "د" else "m"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isOver) Color(0xFFF87171) else Color(0xFFCBD5E1)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Percentage Progress Bar with clean gradient fill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                if (progress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    if (isOver) listOf(Color(0xFFFF7043), Color(0xFFEF4444))
                                    else if (percentage >= 80) listOf(Color(0xFFFFD54F), Color(0xFFF59E0B))
                                    else listOf(Color(0xFF38BDF8), Color(0xFF10B981))
                                )
                            )
                    )
                }

                if (isOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestPoolClearedBanner(
    title: String,
    subtitle: String,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.HourglassTop,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun millisUntilNextMidnight(nowMillis: Long): Long {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = nowMillis
    cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return (cal.timeInMillis - nowMillis).coerceAtLeast(0L)
}

private fun millisUntilNextWeek(nowMillis: Long): Long {
    val cal = java.util.Calendar.getInstance()
    cal.timeInMillis = nowMillis
    cal.add(java.util.Calendar.WEEK_OF_YEAR, 1)
    cal.set(java.util.Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
    cal.set(java.util.Calendar.MINUTE, 0)
    cal.set(java.util.Calendar.SECOND, 0)
    cal.set(java.util.Calendar.MILLISECOND, 0)
    return (cal.timeInMillis - nowMillis).coerceAtLeast(0L)
}

@Composable
fun QuestRow(
    quest: QuestItem,
    currentTimeMillis: Long,
    isArabic: Boolean,
    hasSkipKey: Boolean,
    onUseSkipKey: () -> Unit
) {
    val isLockedCooldown = quest.isLocked(currentTimeMillis)
    val cooldownMillis = quest.remainingCooldownMillis(currentTimeMillis)

    val cardBg = when {
        quest.isCompleted -> Color(0x1A10B981)
        isLockedCooldown -> Color(0x1AF59E0B)
        else -> Color(0xFF1E293B).copy(alpha = 0.6f)
    }
    val cardBorder = when {
        quest.isCompleted -> Color(0xFF10B981).copy(alpha = 0.6f)
        isLockedCooldown -> Color(0xFFF59E0B).copy(alpha = 0.55f)
        else -> Color(0xFF334155)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quest_row_${quest.id}")
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
                        when {
                            quest.isCompleted -> Color(0xFF10B981).copy(alpha = 0.2f)
                            isLockedCooldown -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                            else -> Color.White.copy(alpha = 0.05f)
                        }
                    )
            ) {
                Icon(
                    imageVector = when {
                        quest.isCompleted -> Icons.Default.CheckCircle
                        isLockedCooldown -> Icons.Default.HourglassTop
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = when {
                        quest.isCompleted -> "Completed"
                        isLockedCooldown -> "Queued Cooldown"
                        else -> "Incomplete"
                    },
                    tint = when {
                        quest.isCompleted -> Color(0xFF10B981)
                        isLockedCooldown -> Color(0xFFF59E0B)
                        else -> Color(0xFF94A3B8)
                    },
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) quest.titleAr else quest.titleEn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            quest.isCompleted -> Color(0xFF34D399)
                            isLockedCooldown -> Color(0xFFFDE68A)
                            else -> Color.White
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (isLockedCooldown) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = if (isArabic) "تبدأ خلال ${HabitPulseEngine.formatCountdown(cooldownMillis)}"
                                       else "Starts ${HabitPulseEngine.formatCountdown(cooldownMillis)}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFBBF24),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (quest.isCompleted) {
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
                    } else if (quest.targetProgress > 1) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF38BDF8).copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "${quest.currentProgress}/${quest.targetProgress}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isArabic) quest.descriptionAr else quest.descriptionEn,
                    fontSize = 11.sp,
                    color = if (isLockedCooldown) Color(0xFF94A3B8) else Color(0xFFCBD5E1)
                )
                if (!quest.isCompleted && !isLockedCooldown && quest.targetProgress > 1) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { quest.progressPercent },
                        color = Color(0xFF38BDF8),
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        quest.isCompleted -> if (isArabic) "تم استلام +${quest.xpReward} XP بنجاح" else "+${quest.xpReward} XP Claimed"
                        isLockedCooldown -> if (isArabic) "⏳ مهمة بديلة قيد التجهيز • متبقي ${HabitPulseEngine.formatCountdown(cooldownMillis)} للبدء"
                                            else "⏳ Replacement quest spawning • ${HabitPulseEngine.formatCountdown(cooldownMillis)} left to start"
                        hasSkipKey -> if (isArabic) "يُحتسب تلقائياً عند التنفيذ • متاح التخطي بالمفتاح (+${quest.xpReward} XP)"
                                      else "Auto-tracks on completion • Skip Key available (+${quest.xpReward} XP)"
                        else -> if (isArabic) "يُحتسب تلقائياً عند تطبيق الشرط (+${quest.xpReward} XP)"
                                else "Must be completed in real usage (+${quest.xpReward} XP)"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        quest.isCompleted -> Color(0xFF10B981)
                        isLockedCooldown -> Color(0xFFF59E0B)
                        else -> Color(0xFF38BDF8)
                    }
                )
            }

            if (!quest.isCompleted && !isLockedCooldown && hasSkipKey) {
                IconButton(
                    onClick = onUseSkipKey,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("skip_quest_btn_${quest.id}")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.16f))
                            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.55f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = if (isArabic) "تخطي المهمة بالمفتاح" else "Skip Quest with Key",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
