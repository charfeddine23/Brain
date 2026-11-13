package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.quests.QuestCategory
import com.example.data.quests.QuestEntity
import com.example.data.quests.QuestFrequency
import com.example.ui.viewmodel.QuestTab
import com.example.ui.viewmodel.QuestViewModel

private val CyberBgTop = Color(0xFF060A14)
private val CyberBgBottom = Color(0xFF0E162B)
private val CyberGlassCard = Color(0xCC11192E)
private val CyberCyan = Color(0xFF38BDF8)
private val CyberPurple = Color(0xFFA855F7)
private val CyberEmerald = Color(0xFF10B981)
private val CyberGold = Color(0xFFF59E0B)

@Composable
fun QuestsScreen(
    isArabic: Boolean,
    onNavigateBack: () -> Unit,
    onXpSynced: () -> Unit,
    questViewModel: QuestViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by questViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        questViewModel.syncXpAndLevelFromRepo()
    }

    val tabCategories = if (uiState.selectedTab == QuestTab.DAILY) {
        QuestCategory.entries.filter { it.frequency == QuestFrequency.DAILY }
    } else {
        QuestCategory.entries.filter { it.frequency == QuestFrequency.WEEKLY }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CyberBgTop, CyberBgBottom)))
            .testTag("quests_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Cybernetic App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("quests_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isArabic) "رجوع" else "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isArabic) "نظام المهام الأسطوري" else "Cybernetic Quest Matrix",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isArabic) "${uiState.allDailyQuests.size} مهمة يومية • ${uiState.allWeeklyQuests.size} أسبوعية"
                            else "${uiState.allDailyQuests.size} Daily • ${uiState.allWeeklyQuests.size} Weekly Quests",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Live Level + XP Neon Pill
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(CyberCyan, CyberPurple))),
                    modifier = Modifier.testTag("quests_level_pill")
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isArabic) "المستوى ${uiState.brainLevel}" else "LVL ${uiState.brainLevel}",
                            color = CyberCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${uiState.totalXp} XP",
                            color = CyberGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mastery Progress & Rotation Control Glass Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CyberGlassCard,
                border = BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        listOf(CyberCyan.copy(alpha = 0.45f), CyberPurple.copy(alpha = 0.45f))
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = CyberGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "إجمالي الإنجاز: ${uiState.totalClaimedCount} / ${uiState.totalCatalogCount}"
                                else "Codex Claimed: ${uiState.totalClaimedCount} / ${uiState.totalCatalogCount}",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (uiState.selectedTab == QuestTab.DAILY && !uiState.showFullCatalog) {
                            Surface(
                                onClick = { questViewModel.rerollDailyRotation() },
                                shape = RoundedCornerShape(12.dp),
                                color = CyberCyan.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("quests_reroll_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Casino,
                                        contentDescription = if (isArabic) "تبديل المهام" else "Reroll Daily Rotation",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "تدوير" else "Reroll",
                                        color = CyberCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle: Active 3-Quest Rotation vs Full 300 Codex
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeChip(
                            label = if (isArabic) "المهام النشطة اليوم" else "Active Rotation",
                            selected = !uiState.showFullCatalog,
                            accent = CyberCyan,
                            onClick = { questViewModel.toggleCatalogView(false) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mode_active_rotation")
                        )
                        ModeChip(
                            label = if (isArabic) "كل المهام (${uiState.totalCatalogCount})" else "All Quests (${uiState.totalCatalogCount})",
                            selected = uiState.showFullCatalog,
                            accent = CyberPurple,
                            onClick = { questViewModel.toggleCatalogView(true) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("mode_full_codex")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Claim Status Banner
            AnimatedVisibility(
                visible = uiState.statusBannerMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                uiState.statusBannerMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CyberEmerald.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { questViewModel.clearBannerMessage() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = if (isArabic) "إغلاق" else "Dismiss",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Daily / Weekly Neon Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FrequencyTabButton(
                    title = if (isArabic) "المهام اليومية (${uiState.allDailyQuests.size})" else "Daily (${uiState.allDailyQuests.size})",
                    selected = uiState.selectedTab == QuestTab.DAILY,
                    accent = CyberCyan,
                    onClick = { questViewModel.selectTab(QuestTab.DAILY) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_daily_quests")
                )
                FrequencyTabButton(
                    title = if (isArabic) "الأسبوعية (${uiState.allWeeklyQuests.size})" else "Weekly (${uiState.allWeeklyQuests.size})",
                    selected = uiState.selectedTab == QuestTab.WEEKLY,
                    accent = CyberPurple,
                    onClick = { questViewModel.selectTab(QuestTab.WEEKLY) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_weekly_quests")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryFilterChip(
                    label = if (isArabic) "الكل" else "All",
                    emoji = "✨",
                    accent = CyberCyan,
                    selected = uiState.selectedCategory == null,
                    onClick = { questViewModel.selectCategoryFilter(null) }
                )
                tabCategories.forEach { category ->
                    CategoryFilterChip(
                        label = if (isArabic) category.titleAr else category.titleEn,
                        emoji = category.emoji,
                        accent = Color(category.accentHex),
                        selected = uiState.selectedCategory == category,
                        onClick = { questViewModel.selectCategoryFilter(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quest List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyberCyan)
                }
            } else if (uiState.displayedQuests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "لا توجد مهام مطابقة لهذا التصنيف في العرض الحالي"
                        else "No quests in this category for the current view.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.displayedQuests, key = { it.id }) { quest ->
                        CyberQuestCard(
                            quest = quest,
                            isArabic = isArabic,
                            onAdvanceProgress = {
                                val step = if (quest.targetProgress >= 60) 15 else 1
                                questViewModel.advanceQuestProgress(quest.id, step)
                            },
                            onClaimReward = {
                                questViewModel.claimQuest(quest.id, onXpSynced = onXpSynced)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) accent.copy(alpha = 0.2f) else Color(0xFF0A1020),
        border = BorderStroke(1.dp, if (selected) accent else Color(0xFF23304D)),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = label,
                color = if (selected) Color.White else Color(0xFF94A3B8),
                fontSize = 11.5.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FrequencyTabButton(
    title: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) accent.copy(alpha = 0.18f) else CyberGlassCard,
        border = BorderStroke(1.4.dp, if (selected) accent else Color(0xFF24314F)),
        shadowElevation = if (selected) 8.dp else 0.dp,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = if (selected) Color.White else Color(0xFF94A3B8),
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun CategoryFilterChip(
    label: String,
    emoji: String,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) accent.copy(alpha = 0.24f) else Color(0xFF0F172A),
        border = BorderStroke(1.dp, if (selected) accent else Color(0xFF28354E)),
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(emoji, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = if (selected) Color.White else Color(0xFFCBD5E1),
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CyberQuestCard(
    quest: QuestEntity,
    isArabic: Boolean,
    onAdvanceProgress: () -> Unit,
    onClaimReward: () -> Unit
) {
    val catColor = Color(quest.category.accentHex)
    val animatedProgress by animateFloatAsState(
        targetValue = quest.progressFraction,
        animationSpec = spring(stiffness = 280f),
        label = "quest_progress_anim"
    )
    val borderColor = when {
        quest.isClaimed -> CyberEmerald.copy(alpha = 0.55f)
        quest.isReadyToClaim -> CyberGold.copy(alpha = 0.85f)
        else -> catColor.copy(alpha = 0.42f)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CyberGlassCard,
        border = BorderStroke(1.2.dp, borderColor),
        shadowElevation = if (quest.isReadyToClaim) 12.dp else 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cyber_quest_card_${quest.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Metadata Row: ID + Category Badge + XP Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = quest.id,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = catColor.copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, catColor.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(quest.category.emoji, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) quest.category.titleAr else quest.category.titleEn,
                                color = catColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // XP Reward Glow Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyberGold.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, CyberGold.copy(alpha = 0.55f))
                ) {
                    Text(
                        text = "+${quest.xpReward} XP",
                        color = Color(0xFFFDE68A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isArabic) quest.titleAr else quest.title,
                color = if (quest.isClaimed) Color(0xFF6EE7B7) else Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isArabic) quest.descriptionAr else quest.description,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Neon Progress Row + Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "التقدم" else "Progress",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${quest.currentProgress} / ${quest.targetProgress}",
                    color = if (quest.isCompleted || quest.isClaimed) CyberEmerald else catColor,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                if (animatedProgress > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    if (quest.isClaimed) listOf(CyberEmerald, Color(0xFF34D399))
                                    else listOf(catColor, CyberPurple)
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Action / Claim Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!quest.isClaimed && !quest.isReadyToClaim) {
                    Surface(
                        onClick = onAdvanceProgress,
                        shape = RoundedCornerShape(14.dp),
                        color = catColor.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, catColor.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("quest_step_btn_${quest.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = if (isArabic) "تسجيل تقدم" else "Log Progress",
                                tint = catColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "تسجيل التقدم" else "Log Progress",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Button(
                    onClick = onClaimReward,
                    enabled = quest.isReadyToClaim,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (quest.isClaimed) CyberEmerald.copy(alpha = 0.18f)
                        else if (quest.isReadyToClaim) CyberEmerald
                        else Color(0xFF1E293B),
                        disabledContainerColor = if (quest.isClaimed) CyberEmerald.copy(alpha = 0.16f)
                        else Color(0xFF1E293B).copy(alpha = 0.6f),
                        contentColor = Color.White,
                        disabledContentColor = if (quest.isClaimed) Color(0xFF34D399) else Color(0xFF64748B)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .then(
                            if (quest.isReadyToClaim) {
                                Modifier.shadow(10.dp, RoundedCornerShape(14.dp), ambientColor = CyberEmerald)
                            } else {
                                Modifier
                            }
                        )
                        .testTag("quest_claim_btn_${quest.id}")
                ) {
                    Icon(
                        imageVector = when {
                            quest.isClaimed -> Icons.Default.CheckCircle
                            quest.isReadyToClaim -> Icons.Default.AutoAwesome
                            else -> Icons.Default.Lock
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            quest.isClaimed -> if (isArabic) "تم الاستلام ✓" else "Claimed ✓"
                            quest.isReadyToClaim -> if (isArabic) "استلام +${quest.xpReward} XP" else "Claim +${quest.xpReward} XP"
                            else -> if (isArabic) "أكمل المهمة" else "Incomplete"
                        },
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
