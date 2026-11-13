package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.InventoryItemType
import com.example.ui.theme.LocalGlassColors
import com.example.ui.theme.ShieldCyan
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class CraftedRewardFeedback(
    val id: Long = System.currentTimeMillis(),
    val resultEmoji: String,
    val titleEn: String,
    val titleAr: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val glowColor: Color
)

@Composable
fun VaultShieldStatusHud(
    shieldCount: Int,
    isImmunityActive: Boolean,
    immunityRemainingHours: Int,
    isArabic: Boolean
) {
    val glassColors = LocalGlassColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "shield_hud_glow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_hud_pulse"
    )

    val statusColor = if (isImmunityActive) Color(0xFF10B981) else ShieldCyan

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0E1628).copy(alpha = 0.88f),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                listOf(
                    statusColor.copy(alpha = 0.6f * pulse),
                    Color(0xFF38BDF8).copy(alpha = 0.25f),
                    statusColor.copy(alpha = 0.6f * pulse)
                )
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_shields_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            statusColor.copy(alpha = 0.14f),
                            Color.Transparent
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
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(statusColor.copy(alpha = 0.18f))
                        .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isArabic) "درع الحصانة والدفاع" else "Aegis Defense Barrier",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = glassColors.onSurfaceColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isImmunityActive) {
                            if (isArabic) "حصانة نشطة الآن • متبقي $immunityRemainingHours ساعة"
                            else "Immunity Online • ${immunityRemainingHours}h remaining"
                        } else {
                            if (isArabic) "الدروع الجاهزة: $shieldCount / 2"
                            else "Standby Shields: $shieldCount / 2"
                        },
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isImmunityActive) Color(0xFF34D399) else Color(0xFF94A3B8)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(2) { index ->
                    val filled = index < shieldCount || isImmunityActive
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (filled) statusColor.copy(alpha = 0.24f)
                                else Color.White.copy(alpha = 0.04f)
                            )
                            .border(
                                width = 1.2.dp,
                                color = if (filled) statusColor.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (filled) Icons.Filled.Shield else Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = if (filled) statusColor else Color(0xFF475569),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameInventoryItemTile(
    item: InventoryItemType,
    count: Int,
    isArabic: Boolean,
    onInfoClick: () -> Unit,
    onUse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rarityColor = Color(item.rarity.colorHex)
    val isOwned = count > 0

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "tile_press_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "item_float_${item.id}")
    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "item_hover"
    )
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (isOwned) Color(0xFF10172A) else Color(0xFF0C111E).copy(alpha = 0.82f),
        border = BorderStroke(
            width = if (isOwned) 1.5.dp else 1.dp,
            brush = Brush.verticalGradient(
                if (isOwned) {
                    listOf(
                        rarityColor.copy(alpha = 0.85f),
                        rarityColor.copy(alpha = 0.25f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.04f)
                    )
                }
            )
        ),
        shadowElevation = if (isOwned) 10.dp else 2.dp,
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = rarityColor),
                onClick = onInfoClick
            )
            .testTag("item_card_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (isOwned) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    rarityColor.copy(alpha = auraAlpha * 0.65f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width / 2f, size.height * 0.34f),
                                radius = size.width * 0.62f
                            ),
                            center = Offset(size.width / 2f, size.height * 0.34f),
                            radius = size.width * 0.62f
                        )
                    }
                }
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top HUD bar inside tile: Rarity pill + Info icon + Stock badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rarityColor.copy(alpha = if (isOwned) 0.22f else 0.1f),
                    border = BorderStroke(0.8.dp, rarityColor.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = if (isArabic) item.rarity.labelAr else item.rarity.labelEn,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isOwned) rarityColor else Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isOwned) Color(0xFF0F2942) else Color(0xFF1E293B).copy(alpha = 0.6f),
                        border = BorderStroke(
                            1.dp,
                            if (isOwned) Color(0xFF38BDF8).copy(alpha = 0.7f) else Color(0xFF334155)
                        )
                    ) {
                        Text(
                            text = "x$count",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isOwned) Color(0xFF38BDF8) else Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onInfoClick)
                            .testTag("item_info_btn_${item.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = if (isArabic) "تفاصيل العنصر" else "Item Details",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Central Pedestal Emblem with Floating Glow
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer {
                        translationY = if (isOwned) hoverOffset else 0f
                        alpha = if (isOwned) 1f else 0.45f
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    rarityColor.copy(alpha = if (isOwned) 0.34f else 0.08f),
                                    Color(0xFF090D18)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            color = rarityColor.copy(alpha = if (isOwned) 0.7f else 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.iconEmoji,
                        fontSize = 34.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & concise perk
            Text(
                text = if (isArabic) item.titleAr else item.titleEn,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isOwned) Color.White else Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = if (isArabic) item.descriptionAr else item.descriptionEn,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(28.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Full-width Action CTA
            Button(
                onClick = onUse,
                enabled = isOwned,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = rarityColor,
                    disabledContainerColor = Color(0xFF1E293B).copy(alpha = 0.75f),
                    contentColor = Color.White,
                    disabledContentColor = Color(0xFF64748B)
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Text(
                    text = if (isOwned) {
                        if (isArabic) "تفعيل" else "Use"
                    } else {
                        if (isArabic) "غير متوفر" else "Empty"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun FusionBlueprintCard(
    ingredientEmoji: String,
    ingredientLabel: String,
    resultEmoji: String,
    resultTitle: String,
    resultAccent: Color,
    currentAmountText: String,
    progressFraction: Float,
    canAfford: Boolean,
    actionLabel: String,
    testTag: String,
    onCraft: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fusion_blueprint_$testTag")
    val readyGlow by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ready_glow"
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF0E172B).copy(alpha = 0.92f),
        border = BorderStroke(
            width = if (canAfford) 1.3.dp else 1.dp,
            color = if (canAfford) resultAccent.copy(alpha = readyGlow * 0.85f) else Color(0xFF23304A)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        if (canAfford) {
                            listOf(
                                Color(0xFF0F2544).copy(alpha = 0.45f),
                                resultAccent.copy(alpha = 0.14f)
                            )
                        } else {
                            listOf(Color.Transparent, Color.Transparent)
                        }
                    )
                )
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Visual Formula: [Ingredient] ━━▶ [Result]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Ingredient Node
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF17223B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            Text(ingredientEmoji, fontSize = 18.sp)
                            Text(
                                text = ingredientLabel,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1
                            )
                        }
                    }

                    // Energy Bridge Arrow
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = if (canAfford) resultAccent else Color(0xFF475569),
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(16.dp)
                    )

                    // Result Item Node
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        resultAccent.copy(alpha = if (canAfford) 0.35f else 0.16f),
                                        Color(0xFF0A101F)
                                    )
                                )
                            )
                            .border(
                                1.2.dp,
                                resultAccent.copy(alpha = if (canAfford) 0.9f else 0.4f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(resultEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Title + Stock text
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = resultTitle,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentAmountText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (canAfford) Color(0xFF34D399) else Color(0xFF94A3B8),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Craft CTA Button
                Button(
                    onClick = onCraft,
                    enabled = canAfford,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = resultAccent,
                        disabledContainerColor = Color(0xFF1E293B),
                        contentColor = Color.White,
                        disabledContentColor = Color(0xFF64748B)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(42.dp)
                ) {
                    Text(
                        text = actionLabel,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(9.dp))

            // Material Progress Meter
            LinearProgressIndicator(
                progress = { progressFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = if (canAfford) resultAccent else Color(0xFF38BDF8),
                trackColor = Color.White.copy(alpha = 0.08f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun CraftingCelebrationDialog(
    reward: CraftedRewardFeedback,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val burstProgress = remember(reward.id) { Animatable(0f) }
    val cardScale = remember(reward.id) { Animatable(0.72f) }

    val infiniteTransition = rememberInfiniteTransition(label = "crafted_halo")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    LaunchedEffect(reward.id) {
        burstProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(reward.id) {
        cardScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF0E1424),
                border = BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(
                            reward.glowColor.copy(alpha = 0.9f),
                            reward.glowColor.copy(alpha = 0.25f)
                        )
                    )
                ),
                shadowElevation = 28.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 350.dp)
                    .graphicsLayer {
                        scaleX = cardScale.value
                        scaleY = cardScale.value
                    }
                    .testTag("crafting_celebration_dialog")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Radial Particles & Shockwave VFX Canvas
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        val cx = size.width / 2f
                        val cy = size.height * 0.36f
                        val p = burstProgress.value

                        // Soft radial bloom
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    reward.glowColor.copy(alpha = 0.38f),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = size.minDimension * 0.55f
                            ),
                            center = Offset(cx, cy),
                            radius = size.minDimension * 0.55f
                        )

                        // Expanding Shockwave Rings
                        if (p < 0.95f) {
                            val ringAlpha = (1f - p).coerceIn(0f, 1f) * 0.8f
                            drawCircle(
                                color = reward.glowColor.copy(alpha = ringAlpha),
                                radius = (30f + p * 170f),
                                center = Offset(cx, cy),
                                style = Stroke(width = 4f * (1f - p * 0.6f))
                            )
                            drawCircle(
                                color = Color.White.copy(alpha = ringAlpha * 0.65f),
                                radius = (18f + p * 120f),
                                center = Offset(cx, cy),
                                style = Stroke(width = 2.5f)
                            )
                        }

                        // Rotating Hexagon Alchemy Mandala behind item
                        rotate(degrees = ringRotation, pivot = Offset(cx, cy)) {
                            val hexRadius = size.minDimension * 0.24f
                            val path = Path()
                            for (i in 0 until 6) {
                                val angle = (i * 60.0) * PI / 180.0
                                val px = cx + (hexRadius * cos(angle)).toFloat()
                                val py = cy + (hexRadius * sin(angle)).toFloat()
                                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                            }
                            path.close()
                            drawPath(
                                path = path,
                                color = reward.glowColor.copy(alpha = 0.55f),
                                style = Stroke(width = 2.2f)
                            )
                        }

                        // 26 Radiating Spark Particles
                        val sparkCount = 26
                        for (i in 0 until sparkCount) {
                            val angle = (i * (360f / sparkCount)) * (PI / 180.0)
                            val speedFactor = if (i % 3 == 0) 1.0f else if (i % 2 == 0) 0.78f else 0.56f
                            val dist = (28f + p * 165f * speedFactor)
                            val sx = cx + (dist * cos(angle)).toFloat()
                            val sy = cy + (dist * sin(angle)).toFloat()
                            val sparkAlpha = (1f - p * 0.85f).coerceIn(0f, 1f)
                            val dotColor = if (i % 2 == 0) reward.glowColor else Color(0xFFFDE047)
                            drawCircle(
                                color = dotColor.copy(alpha = sparkAlpha),
                                radius = (4.8f * (1f - p * 0.5f)).coerceAtLeast(1.5f),
                                center = Offset(sx, sy)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = if (isArabic) "إغلاق" else "Close",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }

                        // Floating Forged Item Pedestal
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .shadow(
                                    elevation = 18.dp,
                                    shape = CircleShape,
                                    ambientColor = reward.glowColor,
                                    spotColor = reward.glowColor
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            reward.glowColor.copy(alpha = 0.4f),
                                            Color(0xFF0A0E1B)
                                        )
                                    )
                                )
                                .border(2.dp, reward.glowColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(reward.resultEmoji, fontSize = 46.sp)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = reward.glowColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) reward.titleAr else reward.titleEn,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isArabic) reward.subtitleAr else reward.subtitleEn,
                            fontSize = 12.5.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onDismiss,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = reward.glowColor,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = if (isArabic) "رائع! متابعة" else "Awesome!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemExplainerDialog(
    item: InventoryItemType,
    count: Int,
    isArabic: Boolean,
    onUseNow: () -> Unit,
    onDismiss: () -> Unit
) {
    val rarityColor = Color(item.rarity.colorHex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF0E1526),
                border = BorderStroke(1.2.dp, rarityColor.copy(alpha = 0.6f)),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 360.dp)
                    .testTag("item_explainer_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = rarityColor.copy(alpha = 0.22f),
                            border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = if (isArabic) item.rarity.labelAr else item.rarity.labelEn,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = rarityColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isArabic) "إغلاق" else "Close",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Showpiece Pedestal
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(rarityColor.copy(alpha = 0.35f), Color(0xFF090E1A))
                                )
                            )
                            .border(1.5.dp, rarityColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.iconEmoji, fontSize = 40.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isArabic) item.titleAr else item.titleEn,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // What it does
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF162036),
                        border = BorderStroke(1.dp, Color(0xFF283552)),
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
                                    text = if (isArabic) "وظيفة العنصر" else "Effect & Role",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) item.descriptionAr else item.descriptionEn,
                                fontSize = 12.sp,
                                color = Color(0xFFE2E8F0),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // How to obtain
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF064E3B).copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "كيفية الحصول عليه" else "How to Obtain",
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "المتوفر في حقيبتك:" else "In Stock:",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "x$count",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (count > 0) Color(0xFF38BDF8) else Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (count > 0) {
                        Button(
                            onClick = onUseNow,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = rarityColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = if (isArabic) "تفعيل العنصر الآن" else "Use Item Now",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = if (isArabic) "إغلاق" else "Close",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }
    }
}
