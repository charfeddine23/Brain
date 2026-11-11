package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.EggState
import com.example.model.EggTier
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.AlertRed
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.ShieldCyan
import com.example.ui.theme.ShieldGold
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.HabitPulseUiState
import com.example.ui.viewmodel.HabitPulseViewModel

@Composable
fun EggRewardsScreen(
    uiState: HabitPulseUiState,
    viewModel: HabitPulseViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null
) {
    val glassColors = LocalGlassColors.current
    val isAr = uiState.isArabic

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (onNavigateBack != null) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(glassColors.accentColor.copy(alpha = 0.15f))
                                .testTag("rewards_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = glassColors.accentColor
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Column {
                        Text(
                            text = if (isAr) "نظام البيض والدروع" else "Egg Rewards & Shield Vault",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = glassColors.onSurfaceColor
                        )
                        Text(
                            text = if (isAr) "حافظ على تتابعك لفتح البيض النادر والحصول على دروع الحماية" else "Maintain streaks to hatch mystical eggs & gain defense shields",
                            fontSize = 12.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Transferred Tutorial & Shield Masterclass Button
                IconButton(
                    onClick = { viewModel.openTutorial() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(glassColors.accentColor.copy(alpha = 0.15f))
                        .border(1.dp, glassColors.accentColor.copy(alpha = 0.5f), CircleShape)
                        .testTag("open_tutorial_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = if (isAr) "دليل الاستخدام والتطور" else "Tutorial & Evolution Guide",
                        tint = glassColors.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Shield Vault Status
        item {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth().testTag("shield_vault_card")) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isAr) "خزينة الدروع النشطة" else "Shield Defense Vault",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = glassColors.onSurfaceColor
                            )
                            Text(
                                text = if (isAr) "السعة القصوى: درعان فقط" else "Maximum capacity: 2 shields",
                                fontSize = 12.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                        }

                        // Shield slots visualizer
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(2) { index ->
                                val filled = index < uiState.shieldCount
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (filled) ShieldCyan.copy(alpha = 0.25f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .border(
                                            1.5.dp,
                                            if (filled) ShieldCyan else Color.White.copy(alpha = 0.15f),
                                            RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (filled) Icons.Filled.Shield else Icons.Outlined.Shield,
                                        contentDescription = null,
                                        tint = if (filled) ShieldCyan else Color.Gray,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isAr) {
                            "الميكانيكية: عند ارتكاب مخالفة لحدود التطبيقات، ينكسر الدرع تلقائياً ويمتص 100% من العقوبة دون أي خصم من نقاط الشعلة ويحافظ على التتابع."
                        } else {
                            "Mechanic: When a threshold breach occurs, an active shield automatically breaks and absorbs 100% of the penalty, safeguarding your streak and strike score."
                        },
                        fontSize = 11.sp,
                        color = glassColors.onSurfaceVariantColor,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Daily Breach Block Notice (if blocked today)
        if (uiState.isEggBlockedToday) {
            item {
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth().testTag("egg_blocked_notice"),
                    borderWidth = 1.5.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AlertRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) "فقس البيض محظور اليوم!" else "Egg Hatching Blocked Today!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                            Text(
                                text = if (isAr) {
                                    "تسبب حدوث تجاوز اليوم في تجميد إمكانية فتح البيض. استعد للانضباط غداً!"
                                } else {
                                    "A limit breach occurred today, locking egg hatching until tomorrow. Stay disciplined!"
                                },
                                fontSize = 11.sp,
                                color = glassColors.onSurfaceVariantColor
                            )
                        }
                    }
                }
            }
        }

        // Egg Tiers List
        items(EggTier.entries) { tier ->
            val eggState = uiState.eggStates[tier] ?: EggState(tier)
            EggItemCard(
                tier = tier,
                eggState = eggState,
                currentStreak = uiState.streakDays,
                isBlockedToday = uiState.isEggBlockedToday,
                isArabic = isAr,
                onHatch = { viewModel.hatchEgg(tier) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun EggItemCard(
    tier: EggTier,
    eggState: EggState,
    currentStreak: Int,
    isBlockedToday: Boolean,
    isArabic: Boolean,
    onHatch: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    val tierColors = when (tier) {
        EggTier.BRONZE -> listOf(Color(0xFFCD7F32), Color(0xFFA0522D))
        EggTier.SILVER -> listOf(Color(0xFFE0E0E0), Color(0xFF757575))
        EggTier.GOLD_DRAGON -> listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
    }

    val isStreakReached = currentStreak >= tier.requiredStreakDays
    val canHatch = isStreakReached && !eggState.isHatched && !isBlockedToday

    val infiniteTransition = rememberInfiniteTransition(label = "egg_shimmer")
    val eggScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "egg_anim"
    )

    val progress = (currentStreak.toFloat() / tier.requiredStreakDays).coerceIn(0f, 1f)

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("egg_card_${tier.name}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mystical Egg graphic
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(if (canHatch) eggScale else 1f)
                            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                            .background(Brush.verticalGradient(tierColors))
                            .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(26.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (tier) {
                                EggTier.BRONZE -> "🥉"
                                EggTier.SILVER -> "🥈"
                                EggTier.GOLD_DRAGON -> "🐉"
                            },
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isArabic) tier.arabicTitle else tier.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = glassColors.onSurfaceColor
                        )
                        Text(
                            text = if (isArabic) {
                                "يتطلب تتابع ${tier.requiredStreakDays} أيام"
                            } else {
                                "Requires ${tier.requiredStreakDays}-day streak"
                            },
                            fontSize = 12.sp,
                            color = glassColors.onSurfaceVariantColor
                        )
                    }
                }

                // Status chip
                if (eggState.isHatched) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SafeGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SafeGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "تم الفقس" else "Hatched",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SafeGreen
                        )
                    }
                } else if (isBlockedToday) {
                    Text(
                        text = if (isArabic) "محظور اليوم" else "Blocked Today",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                } else if (!isStreakReached) {
                    Text(
                        text = if (isArabic) "$currentStreak / ${tier.requiredStreakDays}" else "$currentStreak / ${tier.requiredStreakDays} Days",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.onSurfaceVariantColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Drop description
            Text(
                text = if (isArabic) tier.dropDescriptionAr else tier.dropDescription,
                fontSize = 12.sp,
                color = glassColors.onSurfaceColor
            )

            // Progress towards egg streak
            if (!eggState.isHatched) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = tierColors[0],
                    trackColor = Color.White.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
            }

            // Reward result if already hatched
            if (eggState.isHatched && eggState.droppedReward != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = ShieldGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = eggState.droppedReward,
                        fontSize = 12.sp,
                        color = ShieldGold,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Hatch Action Button
            if (canHatch) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onHatch,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tierColors[0],
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("hatch_btn_${tier.name}")
                ) {
                    Text(
                        text = if (isArabic) "🥚 افتح البيضة واحصل على المكافأة!" else "🥚 Hatch Egg & Claim Drops!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
