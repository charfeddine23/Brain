package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.HabitPulseEngine
import com.example.model.QuestItem

private val CodexBgDark = Color(0xFF0E1320)
private val CodexCardDark = Color(0xFF161D2F)
private val CodexCyan = Color(0xFF38BDF8)
private val CodexPurple = Color(0xFFA855F7)
private val CodexEmerald = Color(0xFF10B981)
private val CodexGold = Color(0xFFF59E0B)

enum class QuestCodexFilter {
    ALL,
    DAILY,
    WEEKLY
}

@Composable
fun QuestCodexDialog(
    allDailyQuests: List<QuestItem>,
    allWeeklyQuests: List<QuestItem>,
    activeDailyIds: Set<String>,
    activeWeeklyIds: Set<String>,
    currentTimeMillis: Long,
    hasDailySkipKey: Boolean,
    hasWeeklySkipKey: Boolean,
    isArabic: Boolean,
    onSkipQuestWithKey: (questId: String, isWeekly: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(QuestCodexFilter.ALL) }

    val scaleAnim = remember { Animatable(0.9f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }
    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )
    }

    val combinedPool = remember(allDailyQuests, allWeeklyQuests, selectedFilter) {
        when (selectedFilter) {
            QuestCodexFilter.ALL -> allDailyQuests + allWeeklyQuests
            QuestCodexFilter.DAILY -> allDailyQuests
            QuestCodexFilter.WEEKLY -> allWeeklyQuests
        }
    }

    val pendingQuests = remember(combinedPool) { combinedPool.filter { !it.isCompleted } }
    val completedQuests = remember(combinedPool) { combinedPool.filter { it.isCompleted } }

    val totalCount = combinedPool.size
    val completedCount = completedQuests.size
    val completionRatio = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = CodexBgDark,
            border = BorderStroke(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(
                        CodexCyan.copy(alpha = 0.55f),
                        Color(0xFF28324D),
                        CodexPurple.copy(alpha = 0.4f)
                    )
                )
            ),
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 420.dp)
                .heightIn(max = 700.dp)
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                    alpha = alphaAnim.value
                }
                .testTag("quest_codex_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(listOf(CodexCyan.copy(alpha = 0.25f), CodexPurple.copy(alpha = 0.25f)))
                                )
                                .border(1.dp, CodexCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = CodexGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "سجل المهام الأسطوري" else "RPG Quest Codex",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (isArabic) "تصنيف المهام المتبقية والمنجزة" else "Pending & Completed Quests Archive",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("quest_codex_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isArabic) "إغلاق" else "Close",
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mastery Progress Summary Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CodexCardDark,
                    border = BorderStroke(1.dp, Color(0xFF28354E)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "نسبة الإنجاز الكلية" else "Codex Completion",
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$completedCount / $totalCount",
                                color = CodexCyan,
                                fontSize = 13.sp,
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
                            if (completionRatio > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(completionRatio.coerceIn(0f, 1f))
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Brush.horizontalGradient(listOf(CodexCyan, CodexEmerald)))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CodexStatBadge(
                                label = if (isArabic) "غير منجزة" else "Pending",
                                value = "${pendingQuests.size}",
                                color = CodexGold,
                                modifier = Modifier.weight(1f)
                            )
                            CodexStatBadge(
                                label = if (isArabic) "منجزة ✓" else "Completed ✓",
                                value = "${completedQuests.size}",
                                color = CodexEmerald,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Tabs: All / Daily / Weekly
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CodexFilterPill(
                        label = if (isArabic) "الكل" else "All",
                        selected = selectedFilter == QuestCodexFilter.ALL,
                        accent = CodexCyan,
                        onClick = { selectedFilter = QuestCodexFilter.ALL },
                        modifier = Modifier.weight(1f).testTag("codex_filter_all")
                    )
                    CodexFilterPill(
                        label = if (isArabic) "يومية" else "Daily",
                        selected = selectedFilter == QuestCodexFilter.DAILY,
                        accent = CodexGold,
                        onClick = { selectedFilter = QuestCodexFilter.DAILY },
                        modifier = Modifier.weight(1f).testTag("codex_filter_daily")
                    )
                    CodexFilterPill(
                        label = if (isArabic) "أسبوعية" else "Weekly",
                        selected = selectedFilter == QuestCodexFilter.WEEKLY,
                        accent = CodexPurple,
                        onClick = { selectedFilter = QuestCodexFilter.WEEKLY },
                        modifier = Modifier.weight(1f).testTag("codex_filter_weekly")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Classified Scrollable Quest List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 6.dp)
                ) {
                    // Section 1: Pending / Incomplete Quests
                    item {
                        CodexSectionHeader(
                            title = if (isArabic) "⚔️ المهام التي لم تُنجز بعد (${pendingQuests.size})"
                                    else "⚔️ Pending Quests (${pendingQuests.size})",
                            accent = CodexGold
                        )
                    }

                    if (pendingQuests.isEmpty()) {
                        item {
                            CodexEmptyBanner(
                                text = if (isArabic) "أنت أسطورة! تم إنجاز جميع المهام في هذا القسم 🏆"
                                       else "Legend! Every quest in this category is complete 🏆",
                                accent = CodexEmerald
                            )
                        }
                    } else {
                        items(pendingQuests, key = { "pending_${it.id}" }) { quest ->
                            val isActiveSlot = if (quest.isWeekly) quest.id in activeWeeklyIds else quest.id in activeDailyIds
                            val hasSkipKey = if (quest.isWeekly) hasWeeklySkipKey else hasDailySkipKey
                            CodexQuestItemCard(
                                quest = quest,
                                isActiveSlot = isActiveSlot,
                                currentTimeMillis = currentTimeMillis,
                                hasSkipKey = hasSkipKey,
                                isArabic = isArabic,
                                onSkipWithKey = { onSkipQuestWithKey(quest.id, quest.isWeekly) }
                            )
                        }
                    }

                    // Section 2: Completed Quests
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        CodexSectionHeader(
                            title = if (isArabic) "🏆 المهام المنجزة (${completedQuests.size})"
                                    else "🏆 Completed Quests (${completedQuests.size})",
                            accent = CodexEmerald
                        )
                    }

                    if (completedQuests.isEmpty()) {
                        item {
                            CodexEmptyBanner(
                                text = if (isArabic) "لم تنجز أي مهمة هنا بعد — أنجز المهام تلقائياً لتظهر هنا!"
                                       else "No completed quests here yet — complete objectives to fill your hall of fame!",
                                accent = Color(0xFF64748B)
                            )
                        }
                    } else {
                        items(completedQuests, key = { "done_${it.id}" }) { quest ->
                            CodexQuestItemCard(
                                quest = quest,
                                isActiveSlot = false,
                                currentTimeMillis = currentTimeMillis,
                                hasSkipKey = false,
                                isArabic = isArabic,
                                onSkipWithKey = {}
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom CTA Button
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(8.dp, RoundedCornerShape(50), ambientColor = CodexCyan.copy(alpha = 0.4f))
                        .background(Brush.horizontalGradient(listOf(CodexCyan, CodexPurple)), RoundedCornerShape(50))
                        .testTag("quest_codex_done_btn")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "العودة للمعركة" else "Back to Dashboard",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CodexStatBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 11.sp, color = Color(0xFFE2E8F0), fontWeight = FontWeight.Medium)
            Text(value, fontSize = 13.sp, color = color, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun CodexFilterPill(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (selected) accent.copy(alpha = 0.22f) else CodexCardDark,
        border = BorderStroke(1.dp, if (selected) accent else Color(0xFF28354E)),
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                color = if (selected) Color.White else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun CodexSectionHeader(title: String, accent: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = accent,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun CodexEmptyBanner(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = Color(0xFFCBD5E1),
            fontSize = 11.5.sp,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun CodexQuestItemCard(
    quest: QuestItem,
    isActiveSlot: Boolean,
    currentTimeMillis: Long,
    hasSkipKey: Boolean,
    isArabic: Boolean,
    onSkipWithKey: () -> Unit
) {
    val isLockedCooldown = quest.isLocked(currentTimeMillis)
    val remainingMillis = quest.remainingCooldownMillis(currentTimeMillis)
    val typeAccent = if (quest.isWeekly) CodexPurple else CodexCyan
    val statusBorder = when {
        quest.isCompleted -> CodexEmerald.copy(alpha = 0.55f)
        isLockedCooldown -> CodexGold.copy(alpha = 0.55f)
        isActiveSlot -> typeAccent.copy(alpha = 0.5f)
        else -> Color(0xFF28354E)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (quest.isCompleted) CodexEmerald.copy(alpha = 0.10f) else CodexCardDark,
        border = BorderStroke(1.dp, statusBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            quest.isCompleted -> CodexEmerald.copy(alpha = 0.2f)
                            isLockedCooldown -> CodexGold.copy(alpha = 0.16f)
                            isActiveSlot -> typeAccent.copy(alpha = 0.16f)
                            else -> Color.White.copy(alpha = 0.05f)
                        }
                    )
            ) {
                Icon(
                    imageVector = when {
                        quest.isCompleted -> Icons.Default.CheckCircle
                        isLockedCooldown -> Icons.Default.HourglassTop
                        isActiveSlot -> Icons.Default.RadioButtonUnchecked
                        else -> Icons.Default.LockClock
                    },
                    contentDescription = null,
                    tint = when {
                        quest.isCompleted -> CodexEmerald
                        isLockedCooldown -> CodexGold
                        isActiveSlot -> typeAccent
                        else -> Color(0xFF64748B)
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Daily / Weekly type pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = typeAccent.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = if (quest.isWeekly) (if (isArabic) "أسبوعية" else "WEEKLY")
                                   else (if (isArabic) "يومية" else "DAILY"),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = typeAccent,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    // Active / Cooldown / Queued / Done status pill
                    val (statusLabel, statusColor) = when {
                        quest.isCompleted -> (if (isArabic) "منجزة ✓" else "DONE ✓") to CodexEmerald
                        isLockedCooldown -> (if (isArabic) "تبدأ خلال ${HabitPulseEngine.formatCountdown(remainingMillis)}" else "Starts in ${HabitPulseEngine.formatCountdown(remainingMillis)}") to CodexGold
                        isActiveSlot -> (if (isArabic) "نشطة الآن" else "ACTIVE") to CodexCyan
                        else -> (if (isArabic) "في الانتظار" else "QUEUED") to Color(0xFF94A3B8)
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.18f)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isArabic) quest.titleAr else quest.titleEn,
                    color = if (quest.isCompleted) Color(0xFF6EE7B7) else Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (isArabic) quest.descriptionAr else quest.descriptionEn,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                if (!quest.isCompleted && !isLockedCooldown && isActiveSlot && quest.targetProgress > 1) {
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = { quest.progressPercent },
                        color = typeAccent,
                        trackColor = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+${quest.xpReward} XP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (quest.isCompleted) CodexEmerald else CodexGold
                    )
                    if (!quest.isCompleted && quest.targetProgress > 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${quest.currentProgress} / ${quest.targetProgress}",
                            fontSize = 10.sp,
                            color = Color(0xFFCBD5E1),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Skip Key allowed ONLY for active & unlocked pending quests when owning a key
            if (!quest.isCompleted && isActiveSlot && !isLockedCooldown && hasSkipKey) {
                IconButton(
                    onClick = onSkipWithKey,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CodexCyan.copy(alpha = 0.16f))
                            .border(1.dp, CodexCyan.copy(alpha = 0.55f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = if (isArabic) "تخطي بالمفتاح" else "Skip with Key",
                            tint = CodexCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
