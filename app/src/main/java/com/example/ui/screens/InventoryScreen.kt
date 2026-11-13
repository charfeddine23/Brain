package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.InventoryItemType
import com.example.ui.components.CraftedRewardFeedback
import com.example.ui.components.CraftingCelebrationDialog
import com.example.ui.components.FusionBlueprintCard
import com.example.ui.components.GameInventoryItemTile
import com.example.ui.components.ItemExplainerDialog
import com.example.ui.components.VaultShieldStatusHud
import com.example.ui.theme.LocalGlassColors
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

enum class InventoryCategoryFilter(val titleEn: String, val titleAr: String, val emoji: String) {
    ALL("All Items", "الكل", "🎒"),
    SHIELDS("Shields", "الدروع", "🛡️"),
    POTIONS("Potions", "العقاقير", "🧪"),
    KEYS("Skip Keys", "المفاتيح", "🗝️"),
    BOXES("Boxes", "الصناديق", "🎁")
}

@Composable
fun InventoryScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel
) {
    val glassColors = LocalGlassColors.current
    val isArabic = uiState.isArabic

    var itemExplaining by remember { mutableStateOf<InventoryItemType?>(null) }
    var selectedCategory by remember { mutableStateOf(InventoryCategoryFilter.ALL) }
    var craftedFeedback by remember { mutableStateOf<CraftedRewardFeedback?>(null) }

    val filteredItems = remember(selectedCategory) {
        when (selectedCategory) {
            InventoryCategoryFilter.ALL -> InventoryItemType.entries
            InventoryCategoryFilter.SHIELDS -> listOf(
                InventoryItemType.SHIELD_24H,
                InventoryItemType.SHIELD_1WEEK
            )
            InventoryCategoryFilter.POTIONS -> listOf(
                InventoryItemType.BRAIN_HEALING_POTION,
                InventoryItemType.LEGENDARY_POTION
            )
            InventoryCategoryFilter.KEYS -> listOf(
                InventoryItemType.DAILY_QUEST_SKIP_KEY,
                InventoryItemType.WEEKLY_QUEST_SKIP_KEY
            )
            InventoryCategoryFilter.BOXES -> listOf(
                InventoryItemType.MYSTERY_BOX
            )
        }
    }

    val chunkedItems = remember(filteredItems) { filteredItems.chunked(2) }
    val totalItemsCount = uiState.inventory.values.sum()

    // Subtle animated aura for the Fusion Lab header
    val labTransition = rememberInfiniteTransition(label = "fusion_lab_aura")
    val labBorderAlpha by labTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lab_border_alpha"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding), vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Modern RPG Vault Header HUD ---
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF0E1526).copy(alpha = 0.9f),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.55f),
                            Color(0xFFA855F7).copy(alpha = 0.45f)
                        )
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.14f),
                                    Color(0xFFA855F7).copy(alpha = 0.10f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(Color(0xFF38BDF8).copy(alpha = 0.35f), Color(0xFF0B1222))
                                    )
                                )
                                .border(1.2.dp, Color(0xFF38BDF8).copy(alpha = 0.8f), RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isArabic) "خزينة الترسانة العصبية" else "Neural Vault Arsenal",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = if (isArabic) "المقتنيات النادرة • الدمج والتخليق" else "Rare Loot • Fusion & Crafting Chamber",
                                fontSize = 11.5.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = "$totalItemsCount ${if (isArabic) "عنصر" else "Items"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF59E0B).copy(alpha = 0.14f),
                            border = BorderStroke(0.8.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "⚡ ${uiState.totalXp} XP",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Smooth Animated Event Banner ---
        item {
            AnimatedVisibility(
                visible = uiState.itemEventMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = glassColors.accentColor.copy(alpha = 0.22f),
                    border = BorderStroke(1.dp, glassColors.accentColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearItemMessage() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = if (isArabic) "إغلاق" else "Close",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 3. Active Shield Barrier HUD ---
        item {
            VaultShieldStatusHud(
                shieldCount = uiState.shieldCount,
                isImmunityActive = uiState.isImmunityActive,
                immunityRemainingHours = uiState.immunityRemainingHours,
                isArabic = isArabic
            )
        }

        // --- 4. Modern RPG Loot Grid Header + Category Chips ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "⚔️ ترسانة العناصر النادرة" else "⚔️ Vault Arsenal Grid",
                        fontWeight = FontWeight.ExtraBold,
                        color = glassColors.onSurfaceColor,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (isArabic) "اضغط ℹ️ للتفاصيل" else "Tap ℹ️ for details",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(InventoryCategoryFilter.entries) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = "${category.emoji} ${if (isArabic) category.titleAr else category.titleEn}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF38BDF8).copy(alpha = 0.24f),
                                selectedLabelColor = Color(0xFF38BDF8),
                                containerColor = Color(0xFF121B30),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Color(0xFF38BDF8) else Color(0xFF283552)
                            )
                        )
                    }
                }
            }
        }

        // --- 5. Modern 2-Column RPG Game Loot Grid ---
        items(chunkedItems) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (item in rowItems) {
                    val count = uiState.inventory[item] ?: 0
                    GameInventoryItemTile(
                        item = item,
                        count = count,
                        isArabic = isArabic,
                        onInfoClick = { itemExplaining = item },
                        onUse = {
                            if (item == InventoryItemType.MYSTERY_BOX && count > 0) {
                                viewModel.useInventoryItem(item)
                                craftedFeedback = CraftedRewardFeedback(
                                    resultEmoji = "🎁",
                                    titleEn = "Mystery Box Opened!",
                                    titleAr = "تم فتح الصندوق الغامض!",
                                    subtitleEn = "Your cyber loot reward has been added to your vault.",
                                    subtitleAr = "تم استخراج جائزتك العشوائية وإضافتها لخزينتك!",
                                    glowColor = Color(0xFF38BDF8)
                                )
                            } else {
                                viewModel.useInventoryItem(item)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // --- 6. Redesigned Holographic Neural Fusion & Crafting Lab ---
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0B1222).copy(alpha = 0.95f),
                border = BorderStroke(
                    width = 1.4.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF38BDF8).copy(alpha = labBorderAlpha),
                            Color(0xFFA855F7).copy(alpha = labBorderAlpha * 0.75f),
                            Color(0xFF38BDF8).copy(alpha = 0.25f)
                        )
                    )
                ),
                shadowElevation = 14.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fusion_lab_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1D4ED8).copy(alpha = 0.14f),
                                    Color(0xFF7E22CE).copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Lab Chamber Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF0284C7), Color(0xFF9333EA))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Science,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "مختبر الدمج والصياغة العصبية" else "Neural Fusion & Crafting Lab",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 15.5.sp
                                )
                                Text(
                                    text = if (isArabic) "حوّل الموارد إلى عناصر خارقة فوراً" else "Transmute materials into elite gear",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFA855F7).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "4 ${if (isArabic) "وصفات" else "Recipes"}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD8B4FE),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }

                    val dailyKeys = uiState.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0
                    val weeklyKeys = uiState.inventory[InventoryItemType.WEEKLY_QUEST_SKIP_KEY] ?: 0

                    // Recipe 1: 3 Daily Keys -> 1 Weekly Key
                    FusionBlueprintCard(
                        ingredientEmoji = "🗝️",
                        ingredientLabel = "×3",
                        resultEmoji = "🔑",
                        resultTitle = if (isArabic) "مفتاح أسبوعي نادر" else "Weekly Quest Skip Key",
                        resultAccent = Color(0xFF38BDF8),
                        currentAmountText = "$dailyKeys / 3 ${if (isArabic) "مفتاح يومي" else "Daily Keys"}",
                        progressFraction = dailyKeys / 3f,
                        canAfford = dailyKeys >= 3,
                        actionLabel = if (isArabic) "دمج" else "Fuse",
                        testTag = "craft_weekly_key_btn",
                        onCraft = {
                            if (viewModel.fuseDailyKeysToWeeklyKey()) {
                                craftedFeedback = CraftedRewardFeedback(
                                    resultEmoji = "🔑",
                                    titleEn = "Weekly Key Forged!",
                                    titleAr = "تم دمج المفتاح الأسبوعي!",
                                    subtitleEn = "3 Daily Keys were fused into 1 Rare Weekly Quest Skip Key.",
                                    subtitleAr = "تم صهر 3 مفاتيح يومية للحصول على مفتاح تخطي أسبوعي نادر.",
                                    glowColor = Color(0xFF38BDF8)
                                )
                            }
                        }
                    )

                    // Recipe 2: 2 Weekly Keys -> 1 Brain Healing Potion
                    FusionBlueprintCard(
                        ingredientEmoji = "🔑",
                        ingredientLabel = "×2",
                        resultEmoji = "🧪",
                        resultTitle = if (isArabic) "جرعة التعافي (+1 مستوى)" else "Brain Healing Potion (+1 Lvl)",
                        resultAccent = Color(0xFF10B981),
                        currentAmountText = "$weeklyKeys / 2 ${if (isArabic) "مفتاح أسبوعي" else "Weekly Keys"}",
                        progressFraction = weeklyKeys / 2f,
                        canAfford = weeklyKeys >= 2,
                        actionLabel = if (isArabic) "تخليق" else "Brew",
                        testTag = "craft_healing_potion_btn",
                        onCraft = {
                            if (viewModel.synthesizeHealingPotion()) {
                                craftedFeedback = CraftedRewardFeedback(
                                    resultEmoji = "🧪",
                                    titleEn = "Healing Potion Synthesized!",
                                    titleAr = "تم تخليق جرعة التعافي!",
                                    subtitleEn = "1 Brain Healing Potion (+1 Level & Rot Purge) added to your arsenal.",
                                    subtitleAr = "أُضيفت جرعة التعافي الذهني (+1 مستوى وإزالة التعفن) لترسانتك.",
                                    glowColor = Color(0xFF10B981)
                                )
                            }
                        }
                    )

                    // Recipe 3: 2 Daily Keys -> 1 24H Aegis Shield
                    FusionBlueprintCard(
                        ingredientEmoji = "🗝️",
                        ingredientLabel = "×2",
                        resultEmoji = "🛡️",
                        resultTitle = if (isArabic) "درع حماية 24 ساعة" else "24-Hour Aegis Shield",
                        resultAccent = Color(0xFF0EA5E9),
                        currentAmountText = "$dailyKeys / 2 ${if (isArabic) "مفتاح يومي" else "Daily Keys"}",
                        progressFraction = dailyKeys / 2f,
                        canAfford = dailyKeys >= 2,
                        actionLabel = if (isArabic) "صياغة" else "Forge",
                        testTag = "craft_shield_24h_btn",
                        onCraft = {
                            if (viewModel.forgeShield()) {
                                craftedFeedback = CraftedRewardFeedback(
                                    resultEmoji = "🛡️",
                                    titleEn = "Aegis Shield Forged!",
                                    titleAr = "تمت صياغة الدرع!",
                                    subtitleEn = "1 24-Hour Defense Shield forged from 2 Daily Keys.",
                                    subtitleAr = "تمت صياغة درع حماية 24 ساعة بنجاح من مفتاحين يوميين.",
                                    glowColor = Color(0xFF0EA5E9)
                                )
                            }
                        }
                    )

                    // Recipe 4: 15,000 XP -> 1 Mystery Cyber Box
                    FusionBlueprintCard(
                        ingredientEmoji = "⚡",
                        ingredientLabel = "15K XP",
                        resultEmoji = "🎁",
                        resultTitle = if (isArabic) "صندوق السايبر الغامض" else "Mystery Cyber Box",
                        resultAccent = Color(0xFFF59E0B),
                        currentAmountText = "${uiState.totalXp} / 15,000 XP",
                        progressFraction = uiState.totalXp / 15_000f,
                        canAfford = uiState.totalXp >= 15_000L,
                        actionLabel = if (isArabic) "استخراج" else "Craft",
                        testTag = "craft_mystery_box_btn",
                        onCraft = {
                            if (viewModel.craftMysteryBox()) {
                                craftedFeedback = CraftedRewardFeedback(
                                    resultEmoji = "🎁",
                                    titleEn = "Mystery Box Extracted!",
                                    titleAr = "تم استخراج الصندوق الغامض!",
                                    subtitleEn = "1 Mystery Cyber Box crafted using 15,000 XP!",
                                    subtitleAr = "تم استخراج صندوق السايبر الغامض مقابل 15,000 XP!",
                                    glowColor = Color(0xFFF59E0B)
                                )
                            }
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // --- Interactive Pedestal Item Explainer Dialog ---
    itemExplaining?.let { item ->
        val count = uiState.inventory[item] ?: 0
        ItemExplainerDialog(
            item = item,
            count = count,
            isArabic = isArabic,
            onUseNow = {
                viewModel.useInventoryItem(item)
                itemExplaining = null
                if (item == InventoryItemType.MYSTERY_BOX && count > 0) {
                    craftedFeedback = CraftedRewardFeedback(
                        resultEmoji = "🎁",
                        titleEn = "Mystery Box Opened!",
                        titleAr = "تم فتح الصندوق الغامض!",
                        subtitleEn = "Your cyber loot reward has been added to your vault.",
                        subtitleAr = "تم استخراج جائزتك العشوائية وإضافتها لخزينتك!",
                        glowColor = Color(0xFF38BDF8)
                    )
                }
            },
            onDismiss = { itemExplaining = null }
        )
    }

    // --- Dazzling Crafting & Loot Celebration VFX Overlay ---
    craftedFeedback?.let { reward ->
        CraftingCelebrationDialog(
            reward = reward,
            isArabic = isArabic,
            onDismiss = { craftedFeedback = null }
        )
    }
}
