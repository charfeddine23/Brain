package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.InventoryItemType
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.LocalGlassColors
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

@Composable
fun BrainClinicScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel
) {
    val glassColors = LocalGlassColors.current
    val isArabic = uiState.isArabic

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding), vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Clinic Header ---
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
                            .background(Color(0xFF00FFA3).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF00FFA3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalPharmacy,
                            contentDescription = null,
                            tint = Color(0xFF00FFA3),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isArabic) "عيادة الدماغ السيبرانية" else "The Brain Clinic",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = glassColors.onSurfaceColor
                        )
                        Text(
                            text = if (isArabic) "خزينة الجرعات ومختبر الدمج العصبي" else "Neural Lab & Item Black Market",
                            fontSize = 12.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                    }
                }

                // Balance of Total XP
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = glassColors.accentColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, glassColors.accentColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${uiState.totalXp} XP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // --- 3. Neural Synthesis & Fusion Lab (Black Market Trading) ---
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
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
                                text = if (isArabic) "مختبر الدمج والتبادل العصبي" else "Neural Fusion & Exchange Lab",
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

        // --- 4. Inventory Vault Section ---
        item {
            Text(
                text = if (isArabic) "مستودع الأدوية والعناصر العصبية (7 عناصر)" else "Active Neural Inventory (7 Items)",
                fontWeight = FontWeight.Bold,
                color = glassColors.onSurfaceColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(InventoryItemType.entries) { item ->
            val count = uiState.inventory[item] ?: 0
            ClinicItemDetailCard(
                item = item,
                count = count,
                isArabic = isArabic,
                onUse = { viewModel.useInventoryItem(item) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
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

@Composable
fun ClinicItemDetailCard(
    item: InventoryItemType,
    count: Int,
    isArabic: Boolean,
    onUse: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(item.rarity.colorHex).copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(item.rarity.colorHex).copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${if (isArabic) "الكمية في المخزون:" else "In Stock:"} x$count",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

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
