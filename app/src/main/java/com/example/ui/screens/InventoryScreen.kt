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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.InventoryItemType
import com.example.model.ItemRarity
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.ShieldCyan
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

enum class InventoryCategoryFilter(val titleEn: String, val titleAr: String, val emoji: String) {
    ALL("All Items", "الكل", "🎒"),
    SHIELDS("Shields", "الدروع", "🛡️"),
    POTIONS("Potions", "العقاقير", "🧪"),
    KEYS("Skip Keys", "المفاتيح", "🗝️"),
    BOXES("Mystery Boxes", "الصناديق", "🎁")
}

@Composable
fun InventoryScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel
) {
    val glassColors = LocalGlassColors.current
    val isArabic = uiState.isArabic

    // State for info dialog
    var itemExplaining by remember { mutableStateOf<InventoryItemType?>(null) }
    var selectedCategory by remember { mutableStateOf(InventoryCategoryFilter.ALL) }

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

    val totalItemsCount = uiState.inventory.values.sum()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding), vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Inventory Header ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF38BDF8).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF38BDF8), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isArabic) "المستودع والخزينة العصبية" else "Neural Inventory & Vault",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = glassColors.onSurfaceColor
                        )
                        Text(
                            text = if (isArabic) "جميع العقاقير والمفاتيح والدروع ومختبر الدمج" else "Potions, Skip Keys, Shields & Fusion Lab",
                            fontSize = 12.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                    }
                }

                // Balance of Total Items & XP
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = glassColors.accentColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, glassColors.accentColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "$totalItemsCount ${if (isArabic) "عناصر" else "Items"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.accentColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.totalXp} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // --- 2. Notification Message Banner ---
        item {
            AnimatedVisibility(
                visible = uiState.itemEventMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = glassColors.accentColor.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, glassColors.accentColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
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
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.clearItemMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 3. Active Shields & Immunity Status Card ---
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth().testTag("active_shields_card")) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ShieldCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "حالة الدروع والحصانة النشطة" else "Active Shields & Immunity",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = glassColors.onSurfaceColor
                                )
                            }
                            Text(
                                text = if (uiState.isImmunityActive) {
                                    if (isArabic) "حصانة درع نشطة: متبقي ${uiState.immunityRemainingHours} ساعة"
                                    else "Active Immunity: ${uiState.immunityRemainingHours} hours remaining"
                                } else {
                                    if (isArabic) "السعة الحالية: ${uiState.shieldCount} / 2 دروع حماية"
                                    else "Capacity: ${uiState.shieldCount} / 2 active defense shields"
                                },
                                fontSize = 12.sp,
                                color = if (uiState.isImmunityActive) Color(0xFF34D399) else glassColors.onSurfaceVariantColor
                            )
                        }

                        // Shield slots visualizer
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(2) { index ->
                                val filled = index < uiState.shieldCount || uiState.isImmunityActive
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (filled) ShieldCyan.copy(alpha = 0.25f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .border(
                                            1.dp,
                                            if (filled) ShieldCyan else Color.White.copy(alpha = 0.15f),
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (filled) Icons.Filled.Shield else Icons.Outlined.Shield,
                                        contentDescription = null,
                                        tint = if (filled) ShieldCyan else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Synthesis & Fusion Lab (مختبر الدمج والصياغة) ---
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth().testTag("fusion_lab_card")) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "مختبر الدمج والصياغة العصبية" else "Neural Fusion & Crafting Lab",
                                fontWeight = FontWeight.Bold,
                                color = glassColors.onSurfaceColor,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fusion Trade 1: 3 Daily Keys -> 1 Weekly Key
                    val dailyKeys = uiState.inventory[InventoryItemType.DAILY_QUEST_SKIP_KEY] ?: 0
                    ExchangeRow(
                        title = if (isArabic) "دمج 3 مفاتيح يومية ➔ 1 مفتاح أسبوعي نادر" else "Fuse 3 Daily Keys ➔ 1 Weekly Key",
                        costText = "$dailyKeys / 3 ${if (isArabic) "متوفر" else "Available"}",
                        canAfford = dailyKeys >= 3,
                        actionLabel = if (isArabic) "دمج المفتاح" else "Fuse Key",
                        isArabic = isArabic,
                        onTrade = { viewModel.fuseDailyKeysToWeeklyKey() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fusion Trade 2: 2 Weekly Keys -> 1 Brain Healing Potion
                    val weeklyKeys = uiState.inventory[InventoryItemType.WEEKLY_QUEST_SKIP_KEY] ?: 0
                    ExchangeRow(
                        title = if (isArabic) "تخليق جرعة تعافي (+1 مستوى) ➔ 2 مفاتيح أسبوعية" else "Synthesize Healing Potion (+1 Lvl) ➔ 2 Weekly Keys",
                        costText = "$weeklyKeys / 2 ${if (isArabic) "متوفر" else "Available"}",
                        canAfford = weeklyKeys >= 2,
                        actionLabel = if (isArabic) "تخليق الجرعة" else "Synthesize",
                        isArabic = isArabic,
                        onTrade = { viewModel.synthesizeHealingPotion() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fusion Trade 3: 2 Daily Keys -> 1 24H Shield
                    ExchangeRow(
                        title = if (isArabic) "صياغة درع حماية 24H ➔ 2 مفاتيح يومية" else "Forge 24-Hour Shield ➔ 2 Daily Keys",
                        costText = "$dailyKeys / 2 ${if (isArabic) "متوفر" else "Available"}",
                        canAfford = dailyKeys >= 2,
                        actionLabel = if (isArabic) "صياغة الدرع" else "Forge Shield",
                        isArabic = isArabic,
                        onTrade = { viewModel.forgeShield() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fusion Trade 4: 15,000 XP -> 1 Mystery Box
                    ExchangeRow(
                        title = if (isArabic) "فتح صندوق السايبر الغامض ➔ 15,000 XP" else "Extract Mystery Cyber Box ➔ 15,000 XP",
                        costText = "${uiState.totalXp} / 15,000 XP",
                        canAfford = uiState.totalXp >= 15_000L,
                        actionLabel = if (isArabic) "فتح الصندوق" else "Open Box",
                        isArabic = isArabic,
                        onTrade = { viewModel.craftMysteryBox() }
                    )
                }
            }
        }

        // --- 5. Category Filter Chips ---
        item {
            Column {
                Text(
                    text = if (isArabic) "خزينة المقتنيات والعناصر (اضغط ℹ️ لمعرفة كيفية الحصول)" else "Neural Vault Items (Tap ℹ️ to learn how to obtain)",
                    fontWeight = FontWeight.Bold,
                    color = glassColors.onSurfaceColor,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = glassColors.accentColor.copy(alpha = 0.25f),
                                selectedLabelColor = glassColors.accentColor,
                                containerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) glassColors.accentColor else Color(0xFF334155)
                            )
                        )
                    }
                }
            }
        }

        // --- 6. Items List with Info & Explainer Button ---
        items(filteredItems) { item ->
            val count = uiState.inventory[item] ?: 0
            InventoryItemDetailCard(
                item = item,
                count = count,
                isArabic = isArabic,
                onInfoClick = { itemExplaining = item },
                onUse = { viewModel.useInventoryItem(item) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // --- Interactive Item Explanation & "How To Obtain" Dialog ---
    itemExplaining?.let { item ->
        val count = uiState.inventory[item] ?: 0
        AlertDialog(
            onDismissRequest = { itemExplaining = null },
            containerColor = Color(0xFF0F172A),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFCBD5E1),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("item_explainer_dialog"),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(item.rarity.colorHex).copy(alpha = 0.2f))
                            .border(1.dp, Color(item.rarity.colorHex), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.iconEmoji, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) item.titleAr else item.titleEn,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(item.rarity.colorHex).copy(alpha = 0.25f),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = if (isArabic) item.rarity.labelAr else item.rarity.labelEn,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(item.rarity.colorHex),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Section 1: What it does
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "📋 ما يفعله هذا العنصر:" else "📋 What it does:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) item.descriptionAr else item.descriptionEn,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Section 2: How to obtain it
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF064E3B).copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF059669)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "كيفية الحصول عليه:" else "How to obtain:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF34D399)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) item.howToObtainAr else item.howToObtainEn,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Current stock status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "الكمية المتوفرة في حقيبتك:" else "Current stock in inventory:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "x$count",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B)
                        )
                    }
                }
            },
            confirmButton = {
                if (count > 0) {
                    Button(
                        onClick = {
                            viewModel.useInventoryItem(item)
                            itemExplaining = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(item.rarity.colorHex)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isArabic) "استخدام الآن" else "Use Now",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { itemExplaining = null }) {
                    Text(
                        text = if (isArabic) "إغلاق" else "Close",
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        )
    }
}

@Composable
fun InventoryItemDetailCard(
    item: InventoryItemType,
    count: Int,
    isArabic: Boolean,
    onInfoClick: () -> Unit,
    onUse: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(item.rarity.colorHex).copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(item.rarity.colorHex).copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth().testTag("item_card_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(item.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) item.titleAr else item.titleEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(item.rarity.colorHex).copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = if (isArabic) item.rarity.labelAr else item.rarity.labelEn,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(item.rarity.colorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isArabic) item.descriptionAr else item.descriptionEn,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "${if (isArabic) "المخزون:" else "In Stock:"} x$count",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B)
                    )

                    // Prominent Info/Explanation badge button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF38BDF8).copy(alpha = 0.15f))
                            .clickable { onInfoClick() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("item_info_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = if (isArabic) "شرح العنصر" else "Item Info",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "كيفية الحصول" else "How to get",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Use Button
            Button(
                onClick = onUse,
                enabled = count > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(item.rarity.colorHex),
                    disabledContainerColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp)
            ) {
                Text(
                    text = if (count > 0) (if (isArabic) "استخدام" else "Use") else (if (isArabic) "فارغ" else "0"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (count > 0) Color.White else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun ExchangeRow(
    title: String,
    costText: String,
    canAfford: Boolean,
    actionLabel: String,
    isArabic: Boolean,
    onTrade: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = costText,
                    fontSize = 11.sp,
                    color = if (canAfford) Color(0xFF34D399) else Color(0xFF94A3B8)
                )
            }

            Button(
                onClick = onTrade,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7),
                    disabledContainerColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(32.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canAfford) Color.White else Color(0xFF64748B)
                )
            }
        }
    }
}
