package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * 100-Level Brain Evolution Engine Canvas:
 * - Level 1: Fully rotted, dark, dead brain (peak digital addiction).
 * - Level 100: Healthy, glowing, legendary genius cyber-neural brain with quantum orbital rings.
 * - Dynamically evolves in color, pulse rhythm, synaptic density, and holographic aura.
 * - Renders crystalline Aegis Shield barrier when protected.
 */
@Composable
fun DynamicBrainCanvas(
    level: Int,
    rotPercentage: Float = 0f,
    isShieldActive: Boolean = false,
    isHealing: Boolean = false,
    isGlitching: Boolean = false,
    modifier: Modifier = Modifier
) {
    val clampedLevel = level.coerceIn(1, 100)
    val evolutionFactor = (clampedLevel - 1) / 99f // 0.0 at Level 1, 1.0 at Level 100
    val effectiveRot = maxOf(rotPercentage, (1f - evolutionFactor) * 85f).coerceIn(0f, 100f)

    // Pulse rhythm accelerates from sluggish at Level 1 (2200ms) to hyper-alert at Level 100 (900ms)
    val pulseDuration = remember(clampedLevel) {
        val maxDuration = 2200
        val minDuration = 900
        (maxDuration - ((maxDuration - minDuration) * evolutionFactor)).toInt()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "brain_engine_transition")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = if (clampedLevel < 20) 0.94f else 0.96f,
        targetValue = if (clampedLevel < 20) 1.02f else 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val neuralSparkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (clampedLevel > 50) 1400 else 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "neural_sparks"
    )

    val orbitalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbital_rotation"
    )

    val shieldPulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    val glitchPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glitch_phase"
    )

    // Dynamic Evolution Color Palettes:
    // Level 1: Charred necrotic ash & decaying slate
    // Level 20-50: Reborn Emerald & Electric Cyan
    // Level 51-80: Plasma Gold & Prismatic Violet
    // Level 81-100: Transcendent Cyber-Neural Celestial Matrix
    val primaryBrainColor = when {
        clampedLevel <= 10 -> Color(0xFF1E1E24) // Charred void
        clampedLevel <= 25 -> Color(0xFF6B21A8) // Re-awakening violet
        clampedLevel <= 50 -> Color(0xFF00FFA3) // Vibrant neural emerald
        clampedLevel <= 80 -> Color(0xFF00E5FF) // Electric cyan
        else -> Color(0xFFFACC15)              // Transcendent cyber gold
    }

    val secondaryBrainColor = when {
        clampedLevel <= 10 -> Color(0xFF09090B) // Necrotic black
        clampedLevel <= 25 -> Color(0xFF3B0764) // Deep sludge plum
        clampedLevel <= 50 -> Color(0xFF0284C7) // Sky blue
        clampedLevel <= 80 -> Color(0xFFA855F7) // Ultra violet
        else -> Color(0xFF38BDF8)              // Cyber ethereal cyan
    }

    val synapseColor = when {
        clampedLevel <= 10 -> Color(0xFF71717A) // Dying grey spark
        clampedLevel <= 25 -> Color(0xFFE879F9) // Awakening pink spark
        clampedLevel <= 50 -> Color(0xFFE0F2FE) // Pristine ice white
        clampedLevel <= 80 -> Color(0xFFFEF08A) // Plasma gold spark
        else -> Color(0xFFFFFFFF)              // Pure quantum starburst
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = minOf(size.width, size.height) * 0.38f

            // 1. Quantum Orbital Rings (Level 50+)
            if (clampedLevel >= 50) {
                rotate(degrees = orbitalRotation, pivot = center) {
                    drawCircle(
                        color = primaryBrainColor.copy(alpha = 0.25f),
                        radius = radius * 1.35f,
                        center = center,
                        style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                    )
                    // Orbital nodes
                    val nodeAngle = Math.toRadians(orbitalRotation.toDouble())
                    val nx = center.x + (cos(nodeAngle) * radius * 1.35f).toFloat()
                    val ny = center.y + (sin(nodeAngle) * radius * 1.35f).toFloat()
                    drawCircle(color = Color.White, radius = 4f, center = Offset(nx, ny))
                }

                if (clampedLevel >= 80) {
                    rotate(degrees = -orbitalRotation * 1.4f, pivot = center) {
                        drawCircle(
                            color = secondaryBrainColor.copy(alpha = 0.20f),
                            radius = radius * 1.50f,
                            center = center,
                            style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                        )
                        val nodeAngle2 = Math.toRadians((-orbitalRotation * 1.4).toDouble())
                        val nx2 = center.x + (cos(nodeAngle2) * radius * 1.50f).toFloat()
                        val ny2 = center.y + (sin(nodeAngle2) * radius * 1.50f).toFloat()
                        drawCircle(color = primaryBrainColor, radius = 5f, center = Offset(nx2, ny2))
                    }
                }
            }

            // 2. Active Shield Barrier (Aegis Hexagon / Energy Dome)
            if (isShieldActive) {
                scale(scale = shieldPulse, pivot = center) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF38BDF8).copy(alpha = 0.22f),
                                Color(0xFF0284C7).copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius * 1.6f
                        ),
                        radius = radius * 1.55f,
                        center = center
                    )
                    drawCircle(
                        color = Color(0xFF38BDF8),
                        radius = radius * 1.45f,
                        center = center,
                        style = Stroke(width = 2.8f, cap = StrokeCap.Round)
                    )
                }
            }

            scale(scale = pulseScale, pivot = center) {
                // 3. Ambient Synaptic Aura
                val auraAlpha = if (clampedLevel < 20) 0.10f else 0.40f * evolutionFactor + 0.15f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryBrainColor.copy(alpha = auraAlpha),
                            secondaryBrainColor.copy(alpha = auraAlpha * 0.5f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 1.45f
                    ),
                    radius = radius * 1.4f,
                    center = center
                )

                // 4. Draw Left & Right Hemispheres
                drawBrainHemisphere(
                    isLeft = true,
                    center = center,
                    radius = radius,
                    primaryColor = primaryBrainColor,
                    secondaryColor = secondaryBrainColor,
                    level = clampedLevel,
                    rot = effectiveRot
                )
                drawBrainHemisphere(
                    isLeft = false,
                    center = center,
                    radius = radius,
                    primaryColor = primaryBrainColor,
                    secondaryColor = secondaryBrainColor,
                    level = clampedLevel,
                    rot = effectiveRot
                )

                // 5. Draw Necrotic Addiction Blotches (dominant at Level 1, fades out by Level 30)
                if (clampedLevel < 35 || effectiveRot > 20f) {
                    drawNecroticPatches(center = center, radius = radius, level = clampedLevel)
                }

                // 6. Draw High-Tech Synaptic Pathways & Sparks
                drawNeuralPathways(
                    center = center,
                    radius = radius,
                    color = synapseColor,
                    level = clampedLevel,
                    sparkProgress = neuralSparkProgress
                )

                // 7. Healing Ascension Particles
                if (isHealing || clampedLevel >= 90) {
                    drawHealingAscension(
                        center = center,
                        radius = radius,
                        progress = neuralSparkProgress,
                        color = primaryBrainColor
                    )
                }

                // 8. Glitch & Virus Distortion Effects (upon demotion penalty, glitching state, or high rot)
                if (isGlitching || effectiveRot > 75f || clampedLevel <= 3) {
                    drawGlitchVirusDistortion(
                        center = center,
                        radius = radius,
                        glitchPhase = glitchPhase
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawGlitchVirusDistortion(
    center: Offset,
    radius: Float,
    glitchPhase: Float
) {
    val random = Random(((glitchPhase * 1000).toInt() + 7))
    val tearCount = 7

    for (i in 0 until tearCount) {
        val yOffset = center.y - radius * 0.85f + (random.nextFloat() * radius * 1.7f)
        val tearWidth = radius * (0.5f + random.nextFloat() * 0.7f)
        val jitterX = (random.nextFloat() - 0.5f) * 26f

        // Chromatic aberration split lines
        drawLine(
            color = Color(0xFF00FFFF).copy(alpha = 0.8f),
            start = Offset(center.x - (tearWidth / 2f) + jitterX, yOffset),
            end = Offset(center.x + (tearWidth / 2f) + jitterX, yOffset),
            strokeWidth = 2.8f
        )
        drawLine(
            color = Color(0xFFFF0055).copy(alpha = 0.8f),
            start = Offset(center.x - (tearWidth / 2f) - jitterX, yOffset + 2f),
            end = Offset(center.x + (tearWidth / 2f) - jitterX, yOffset + 2f),
            strokeWidth = 2.8f
        )
    }
}

private fun DrawScope.drawBrainHemisphere(
    isLeft: Boolean,
    center: Offset,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    level: Int,
    rot: Float
) {
    val dir = if (isLeft) -1f else 1f
    val gap = radius * 0.05f

    val path = Path().apply {
        val startX = center.x + (gap * dir)
        moveTo(startX, center.y - radius * 0.95f)

        // Frontal Lobe & Cranial Apex
        cubicTo(
            startX + (radius * 0.45f * dir), center.y - radius * 1.05f,
            startX + (radius * 0.92f * dir), center.y - radius * 0.65f,
            startX + (radius * 0.95f * dir), center.y - radius * 0.20f
        )

        // Temporal convolution
        cubicTo(
            startX + (radius * 1.08f * dir), center.y + radius * 0.25f,
            startX + (radius * 0.85f * dir), center.y + radius * 0.65f,
            startX + (radius * 0.55f * dir), center.y + radius * 0.90f
        )

        // Occipital Base
        cubicTo(
            startX + (radius * 0.35f * dir), center.y + radius * 1.02f,
            startX + (radius * 0.10f * dir), center.y + radius * 0.85f,
            startX, center.y + radius * 0.70f
        )

        lineTo(startX, center.y - radius * 0.95f)
        close()
    }

    // Dynamic gradient fill: shifts from dead pitch-black to iridescent cyber matrix
    val fillBrush = Brush.linearGradient(
        colors = listOf(
            primaryColor.copy(alpha = if (level < 15) 0.85f else 0.55f),
            secondaryColor.copy(alpha = if (level < 15) 0.95f else 0.35f)
        ),
        start = Offset(center.x + (radius * dir), center.y - radius),
        end = Offset(center.x, center.y + radius)
    )

    drawPath(path = path, brush = fillBrush)

    // Luminous contour stroke
    val strokeWidth = if (level >= 80) 3.5f else 2.5f
    val strokeColor = if (level < 15) Color(0xFF475569) else primaryColor.copy(alpha = 0.90f)

    drawPath(
        path = path,
        color = strokeColor,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawNecroticPatches(center: Offset, radius: Float, level: Int) {
    val severity = (100 - level).coerceIn(10, 100) / 100f
    val patchCount = (severity * 10).toInt().coerceIn(2, 10)
    val random = Random(1337)

    for (i in 0 until patchCount) {
        val angle = random.nextFloat() * 6.28f
        val dist = radius * (0.25f + random.nextFloat() * 0.55f)
        val patchCenter = Offset(
            center.x + (cos(angle) * dist),
            center.y + (sin(angle) * dist)
        )
        val patchRadius = radius * (0.10f + random.nextFloat() * 0.18f) * severity

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF030712).copy(alpha = severity * 0.90f),
                    Color(0xFF1F2937).copy(alpha = 0.50f),
                    Color.Transparent
                ),
                center = patchCenter,
                radius = patchRadius
            ),
            radius = patchRadius,
            center = patchCenter
        )
    }
}

private fun DrawScope.drawNeuralPathways(
    center: Offset,
    radius: Float,
    color: Color,
    level: Int,
    sparkProgress: Float
) {
    val foldStyle = Stroke(
        width = if (level >= 80) 3.5f else 2.2f,
        cap = StrokeCap.Round
    )

    listOf(-1f, 1f).forEach { side ->
        val xShift = side * radius * 0.45f

        // Frontal gyri
        val path1 = Path().apply {
            moveTo(center.x + (side * radius * 0.12f), center.y - radius * 0.6f)
            cubicTo(
                center.x + xShift * 0.8f, center.y - radius * 0.7f,
                center.x + xShift * 1.3f, center.y - radius * 0.3f,
                center.x + xShift * 0.5f, center.y - radius * 0.1f
            )
        }
        drawPath(path = path1, color = color.copy(alpha = if (level < 20) 0.35f else 0.75f), style = foldStyle)

        // Temporal gyri
        val path2 = Path().apply {
            moveTo(center.x + (side * radius * 0.15f), center.y)
            cubicTo(
                center.x + xShift * 1.4f, center.y + radius * 0.1f,
                center.x + xShift * 1.1f, center.y + radius * 0.5f,
                center.x + xShift * 0.3f, center.y + radius * 0.45f
            )
        }
        drawPath(path = path2, color = color.copy(alpha = if (level < 20) 0.30f else 0.70f), style = foldStyle)

        // Spark pulse along synapse
        val sparkPhase = (sparkProgress + (if (side < 0) 0f else 0.5f)) % 1f
        val sparkX = center.x + (xShift * (0.3f + sparkPhase * 0.8f))
        val sparkY = center.y - radius * (0.5f - sparkPhase * 0.7f)

        val sparkRadius = if (level >= 80) 6f else 3.5f
        drawCircle(
            color = if (level < 15) Color(0xFFEF4444) else color,
            radius = sparkRadius,
            center = Offset(sparkX, sparkY)
        )
        drawCircle(
            color = Color.White,
            radius = sparkRadius * 0.5f,
            center = Offset(sparkX, sparkY)
        )
    }
}

private fun DrawScope.drawHealingAscension(
    center: Offset,
    radius: Float,
    progress: Float,
    color: Color
) {
    val count = 12
    for (i in 0 until count) {
        val angle = ((i * 30f) + (progress * 360f)) * (3.14159f / 180f)
        val dist = radius * (0.75f + (progress * 0.6f))
        val alpha = (1f - progress).coerceIn(0f, 1f) * 0.85f

        val sx = center.x + (cos(angle) * dist)
        val sy = center.y - (radius * 0.15f) - (progress * radius * 0.9f)

        drawCircle(
            color = color.copy(alpha = alpha),
            radius = 3.5f,
            center = Offset(sx, sy)
        )
    }
}
