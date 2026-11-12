package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.sin
import kotlin.random.Random

private data class FireParticle(
    val initialXRatio: Float,
    val speed: Float,
    val baseRadius: Float,
    val phaseOffset: Float,
    val colorIndex: Int
)

/**
 * Subtle fire particle animation that surrounds the flame streak chip.
 * As [streakDays] increases, particles become:
 * - More numerous (from 6 particles up to 24 particles)
 * - Move with higher upward energy and dynamic sway
 * - Emit a richer glowing gradient from fiery amber to solar yellow and intense violet/white core
 */
@Composable
fun FireParticleContainer(
    streakDays: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Intensity tier based on streakDays:
    // Level 0: 0 days (dormant)
    // Level 1: 1-4 days (gentle embers)
    // Level 2: 5-14 days (warm active flames)
    // Level 3: 15-29 days (intense firestorm)
    // Level 4: 30+ days (infernal ascended embers)
    val streakLevel = when {
        streakDays <= 0 -> 0
        streakDays < 5 -> 1
        streakDays < 15 -> 2
        streakDays < 30 -> 3
        else -> 4
    }

    val particleCount = when (streakLevel) {
        0 -> 4
        1 -> 8
        2 -> 14
        3 -> 20
        else -> 28
    }

    // Seeded stable random particles so they don't re-roll on recomposition
    val particles = remember(streakLevel) {
        val rand = Random(42)
        List(particleCount) {
            FireParticle(
                initialXRatio = rand.nextFloat(), // horizontal position across the chip 0..1
                speed = 0.7f + rand.nextFloat() * (0.8f + streakLevel * 0.3f),
                baseRadius = (2.0f + rand.nextFloat() * 2.5f) * (1f + streakLevel * 0.15f),
                phaseOffset = rand.nextFloat() * 6.28f,
                colorIndex = rand.nextInt(3)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "fire_particles")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2400 / (1f + streakLevel * 0.25f)).toInt().coerceAtLeast(1000),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_motion"
    )

    Box(modifier = modifier) {
        // Background canvas for particles rising from the bottom and swirling upward
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val bottomY = height * 0.90f

            for (particle in particles) {
                // Compute particle life cycle (0 to 1) based on speed and time
                val rawProgress = (animProgress * particle.speed + (particle.phaseOffset / 6.28f)) % 1f

                // Y position: rising from near the base upwards past the top
                val y = bottomY - rawProgress * (height * 1.35f)

                // Horizontal gentle harmonic sway
                val sway = sin(rawProgress * 6.28f + particle.phaseOffset) * (8f + streakLevel * 4f)
                val x = (particle.initialXRatio * width) + sway

                // Alpha fades in at bottom, glows bright at middle, and dissolves as it rises
                val alpha = when {
                    rawProgress < 0.2f -> rawProgress / 0.2f
                    rawProgress > 0.75f -> ((1f - rawProgress) / 0.25f).coerceIn(0f, 1f)
                    else -> 1f
                } * (0.45f + streakLevel * 0.12f).coerceAtMost(0.95f)

                // Particle size shrinks as it rises
                val radius = particle.baseRadius * (1f - rawProgress * 0.5f)

                // Rich fire color palette based on intensity
                val particleColor = when (particle.colorIndex) {
                    0 -> Color(0xFFFF5722) // Crimson Flame
                    1 -> Color(0xFFFFB300) // Solar Gold
                    else -> if (streakLevel >= 3) Color(0xFFFFD54F) else Color(0xFFFF7043) // Bright Core
                }

                // Inner core and outer soft radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = alpha * 0.85f),
                            particleColor.copy(alpha = alpha * 0.75f),
                            particleColor.copy(alpha = 0f)
                        ),
                        center = Offset(x, y),
                        radius = radius * 2.2f
                    ),
                    radius = radius * 2.2f,
                    center = Offset(x, y)
                )

                // Crisp bright spark center
                drawCircle(
                    color = Color.White.copy(alpha = alpha * 0.9f),
                    radius = radius * 0.5f,
                    center = Offset(x, y)
                )
            }
        }

        // The actual streak chip in foreground
        content()
    }
}
