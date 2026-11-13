package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EggState
import com.example.model.EggTier
import com.example.ui.theme.LocalGlassColors

/**
 * Dynamic Glowing Rewards Navigation Chip.
 * Placed in the top header beside the Flame Strike chip on its left side.
 * Renders an animated mystical glowing aura, pulsating neon border,
 * egg/rewards icon with sparkle animation, and a notification badge when rewards/eggs are ready.
 */
@Composable
fun GlowingRewardsNavChip(
    onClick: () -> Unit,
    isArabic: Boolean,
    streakDays: Int,
    canClaimMilestone: Boolean = false,
    eggStates: Map<EggTier, EggState> = emptyMap(),
    isBlockedToday: Boolean = false,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    val canHatchAnyEgg = remember(eggStates, streakDays, isBlockedToday) {
        EggTier.entries.any { tier ->
            val eggState = eggStates[tier] ?: EggState(tier)
            val isStreakReached = streakDays >= tier.requiredStreakDays
            isStreakReached && !eggState.isHatched && !isBlockedToday
        }
    }

    val hasClaimableAction = canClaimMilestone || canHatchAnyEgg

    val infiniteTransition = rememberInfiniteTransition(label = "glowing_rewards_transition")

    // Pulsing scale for breathing effect
    val glowScale by infiniteTransition.animateFloat(
        initialValue = if (hasClaimableAction) 0.96f else 0.98f,
        targetValue = if (hasClaimableAction) 1.06f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (hasClaimableAction) 900 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    // Breathing glow alpha
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (hasClaimableAction) 0.5f else 0.3f,
        targetValue = if (hasClaimableAction) 0.95f else 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (hasClaimableAction) 900 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Sparkle icon subtle rotation / shimmer
    val sparkleAngle by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_angle"
    )

    // Dynamic color gradient for the glowing border
    val glowBrush = remember(glowAlpha) {
        Brush.sweepGradient(
            listOf(
                Color(0xFFFFD700).copy(alpha = glowAlpha), // Gold
                Color(0xFFA855F7).copy(alpha = glowAlpha), // Cyber Purple
                Color(0xFF38BDF8).copy(alpha = glowAlpha), // Cyan
                Color(0xFFFF8C00).copy(alpha = glowAlpha), // Amber
                Color(0xFFFFD700).copy(alpha = glowAlpha)
            )
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag("glowing_rewards_nav_btn")
    ) {
        // Outer pulsing diffuse halo
        Box(
            modifier = Modifier
                .scale(glowScale)
                .size(width = 82.dp, height = 34.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (hasClaimableAction) Color(0xFFFFD700).copy(alpha = glowAlpha * 0.45f)
                            else Color(0xFFA855F7).copy(alpha = glowAlpha * 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main Glowing Pill (exact 34.dp visual height matching Flame chip)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xF00F172A),
            border = BorderStroke(1.5.dp, glowBrush),
            shadowElevation = 8.dp,
            modifier = Modifier
                .height(34.dp)
                .testTag("glowing_rewards_nav_container")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Mystical Glowing Egg / Star Icon
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(sparkleAngle),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = if (isArabic) "صفحة المكافآت والبيض" else "Rewards & Shield Vault",
                        tint = if (hasClaimableAction) Color(0xFFFFD700) else Color(0xFFC084FC),
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                // Rewards Label
                Text(
                    text = if (isArabic) "المكافآت" else "Rewards",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hasClaimableAction) Color(0xFFFFE082) else Color(0xFFF1F5F9)
                )

                // Optional Claimable Indicator dot / icon
                if (hasClaimableAction) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFD700))
                            .shadow(4.dp, CircleShape, spotColor = Color(0xFFFFD700))
                    )
                }
            }
        }
    }
}
