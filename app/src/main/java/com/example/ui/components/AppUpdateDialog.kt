package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppUpdateInfo
import java.util.Locale

private val PopupBgDark = Color(0xFF13131A)
private val CrimsonPrimary = Color(0xFFE0141E)
private val CrimsonDeep = Color(0xFFB30E16)
private val CrimsonRing = Color(0xFFD91620)
private val MutedSubtitleGray = Color(0xFF9CA3AF)

@Composable
fun AppUpdateDialog(
    currentVersion: String,
    updateInfo: AppUpdateInfo,
    isDownloading: Boolean,
    downloadProgress: Int,
    needsInstallPermission: Boolean,
    errorMessage: String?,
    isArabic: Boolean,
    onStartDownloadAndInstall: () -> Unit,
    onOpenInstallPermission: () -> Unit,
    onOpenReleasesBrowser: () -> Unit,
    onDismiss: () -> Unit
) {
    // Smooth entrance spring animation
    val scaleAnim = remember { Animatable(0.88f) }
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
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
        )
    }

    val sizeMbText = if (updateInfo.apkSizeBytes > 0L) {
        String.format(Locale.US, "%.1f MB", updateInfo.apkSizeBytes / (1024f * 1024f))
    } else {
        "APK"
    }

    Dialog(
        onDismissRequest = { if (!isDownloading) onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = !isDownloading,
            dismissOnClickOutside = !isDownloading
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = PopupBgDark,
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.14f),
                            CrimsonRing.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                ),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 360.dp)
                    .graphicsLayer {
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                        alpha = alphaAnim.value
                    }
                    .testTag("app_update_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Row: Subtle Close (X) button aligned to start
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (!isDownloading) onDismiss() },
                            enabled = !isDownloading,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("update_dialog_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isArabic) "إغلاق" else "Close",
                                tint = if (isDownloading) Color.White.copy(alpha = 0.25f) else MutedSubtitleGray,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Center Emblem: Concentric Crimson Halo + Animated Rocket
                    RocketUpdateEmblem(
                        modifier = Modifier.size(116.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Bold White Main Title
                    Text(
                        text = if (isArabic) "تحديث جديد متاح" else "New Update Available",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Crimson Subtitle with Version Info
                    Text(
                        text = if (isArabic) {
                            "تحديث جديد • v${updateInfo.versionName}"
                        } else {
                            "New Update • v${updateInfo.versionName}"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Muted Grey Summary / Release Notes (compact, never oversized)
                    val notesPreview = updateInfo.releaseNotes
                        .replace(Regex("[#*`>]"), "")
                        .trim()
                        .ifBlank {
                            if (isArabic) {
                                "يتوفر إصدار أحدث (v$currentVersion ➔ v${updateInfo.versionName} • $sizeMbText). التحديث يحافظ على كل بياناتك."
                            } else {
                                "A newer build is ready (v$currentVersion ➔ v${updateInfo.versionName} • $sizeMbText). Keeps all your progress."
                            }
                        }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 88.dp)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notesPreview,
                            fontSize = 13.sp,
                            color = MutedSubtitleGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Downloading Progress Bar
                    if (isDownloading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "جاري تحميل التحديث..." else "Downloading APK...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "$downloadProgress%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CrimsonPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (downloadProgress / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(CircleShape),
                            color = CrimsonPrimary,
                            trackColor = Color.White.copy(alpha = 0.1f),
                            strokeCap = StrokeCap.Round
                        )
                    }

                    // Android Unknown Sources Permission Hint
                    if (needsInstallPermission) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.55f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Security,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) {
                                        "فعّل «السماح من هذا المصدر» وسيبدأ التثبيت تلقائياً فور رجوعك."
                                    } else {
                                        "Enable 'Allow from this source' and installation will start on return."
                                    },
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Error Message (if any)
                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage,
                            fontSize = 11.sp,
                            color = Color(0xFFF87171),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Full-width Crimson Pill CTA Button ("تحديث" / "Update")
                    if (needsInstallPermission) {
                        Button(
                            onClick = onOpenInstallPermission,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF59E0B),
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(vertical = 14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("grant_install_perm_btn")
                        ) {
                            Text(
                                text = if (isArabic) "منح إذن التثبيت" else "Allow Install Permission",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(
                                    elevation = if (isDownloading) 0.dp else 14.dp,
                                    shape = CircleShape,
                                    ambientColor = CrimsonPrimary.copy(alpha = 0.5f),
                                    spotColor = CrimsonPrimary.copy(alpha = 0.65f)
                                )
                                .clip(CircleShape)
                                .background(
                                    brush = if (isDownloading) {
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF272734), Color(0xFF1E1E29))
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            listOf(CrimsonPrimary, CrimsonDeep)
                                        )
                                    }
                                )
                        ) {
                            Button(
                                onClick = onStartDownloadAndInstall,
                                enabled = !isDownloading,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                                ),
                                elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("download_install_update_btn")
                            ) {
                                Text(
                                    text = if (isDownloading) {
                                        if (isArabic) "جاري التحميل ($downloadProgress%)..." else "Downloading ($downloadProgress%)..."
                                    } else {
                                        if (isArabic) "تحديث" else "Update"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    // Subtle fallback link only if download fails (keeps the clean screenshot aesthetic normally)
                    if (!errorMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = onOpenReleasesBrowser,
                            modifier = Modifier.testTag("open_github_release_btn")
                        ) {
                            Text(
                                text = if (isArabic) "فتح صفحة التحميل عبر المتصفح" else "Download via Browser (GitHub)",
                                fontSize = 11.sp,
                                color = MutedSubtitleGray
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RocketUpdateEmblem(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "rocket_emblem")
    val rocketBob by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rocket_bob"
    )
    val flamePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(260, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_pulse"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = size.minDimension / 2f
        val innerRadius = outerRadius * 0.78f

        // Soft ambient crimson outer halo
        drawCircle(
            color = CrimsonRing.copy(alpha = 0.16f),
            radius = outerRadius,
            center = center
        )
        drawCircle(
            color = CrimsonRing.copy(alpha = 0.22f),
            radius = outerRadius * 0.90f,
            center = center
        )

        // Deep dark inner space disc
        drawCircle(
            color = Color(0xFF170B10),
            radius = innerRadius,
            center = center
        )

        // Crisp glowing crimson ring border
        drawCircle(
            color = CrimsonRing,
            radius = innerRadius,
            center = center,
            style = Stroke(width = size.minDimension * 0.025f)
        )

        // Tiny floating pixel stars inside the dark circle
        val starSize = size.minDimension * 0.024f
        drawRoundRect(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(center.x + innerRadius * 0.56f, center.y - innerRadius * 0.34f),
            size = Size(starSize, starSize),
            cornerRadius = CornerRadius(1f, 1f)
        )
        drawRoundRect(
            color = Color(0xFFE04848),
            topLeft = Offset(center.x - innerRadius * 0.54f, center.y - innerRadius * 0.25f),
            size = Size(starSize * 0.85f, starSize * 0.85f),
            cornerRadius = CornerRadius(1f, 1f)
        )
        drawRoundRect(
            color = Color(0xFFFACC15),
            topLeft = Offset(center.x - innerRadius * 0.45f, center.y + innerRadius * 0.35f),
            size = Size(starSize * 0.9f, starSize * 0.9f),
            cornerRadius = CornerRadius(1f, 1f)
        )
        drawRoundRect(
            color = Color(0xFFE04848),
            topLeft = Offset(center.x + innerRadius * 0.48f, center.y + innerRadius * 0.28f),
            size = Size(starSize * 0.85f, starSize * 0.85f),
            cornerRadius = CornerRadius(1f, 1f)
        )

        // Rocket geometry centered + gentle hover bobbing
        val cx = center.x
        val cy = center.y + rocketBob
        val u = size.minDimension * 0.01f // 1% unit

        // Thruster exhaust flame (outer orange + inner yellow)
        val flameLen = 22f * u * flamePulse
        val outerFlame = Path().apply {
            moveTo(cx - 5.2f * u, cy + 16f * u)
            lineTo(cx, cy + 16f * u + flameLen)
            lineTo(cx + 5.2f * u, cy + 16f * u)
            close()
        }
        drawPath(outerFlame, color = Color(0xFFF97316))

        val innerFlame = Path().apply {
            moveTo(cx - 2.6f * u, cy + 16f * u)
            lineTo(cx, cy + 16f * u + flameLen * 0.65f)
            lineTo(cx + 2.6f * u, cy + 16f * u)
            close()
        }
        drawPath(innerFlame, color = Color(0xFFFDE047))

        // Left & Right Fins (crimson)
        val leftFin = Path().apply {
            moveTo(cx - 7.5f * u, cy + 3f * u)
            lineTo(cx - 17f * u, cy + 14f * u)
            lineTo(cx - 16f * u, cy + 16.5f * u)
            lineTo(cx - 6.5f * u, cy + 14.5f * u)
            close()
        }
        drawPath(leftFin, color = Color(0xFFB91C1C))

        val rightFin = Path().apply {
            moveTo(cx + 7.5f * u, cy + 3f * u)
            lineTo(cx + 17f * u, cy + 14f * u)
            lineTo(cx + 16f * u, cy + 16.5f * u)
            lineTo(cx + 6.5f * u, cy + 14.5f * u)
            close()
        }
        drawPath(rightFin, color = Color(0xFFB91C1C))

        // Rocket White Fuselage
        val bodyPath = Path().apply {
            moveTo(cx, cy - 28f * u)
            cubicTo(
                cx + 11f * u, cy - 14f * u,
                cx + 10.5f * u, cy + 4f * u,
                cx + 6.8f * u, cy + 15f * u
            )
            lineTo(cx - 6.8f * u, cy + 15f * u)
            cubicTo(
                cx - 10.5f * u, cy + 4f * u,
                cx - 11f * u, cy - 14f * u,
                cx, cy - 28f * u
            )
            close()
        }
        drawPath(bodyPath, color = Color(0xFFF8FAFC))

        // Nose Cone (red tip)
        val nosePath = Path().apply {
            moveTo(cx, cy - 28f * u)
            cubicTo(
                cx + 5.8f * u, cy - 20f * u,
                cx + 7.2f * u, cy - 15.5f * u,
                cx + 7.4f * u, cy - 13.5f * u
            )
            lineTo(cx - 7.4f * u, cy - 13.5f * u)
            cubicTo(
                cx - 7.2f * u, cy - 15.5f * u,
                cx - 5.8f * u, cy - 20f * u,
                cx, cy - 28f * u
            )
            close()
        }
        drawPath(nosePath, color = Color(0xFFDC2626))

        // Nozzle base (dark charcoal)
        drawRoundRect(
            color = Color(0xFF334155),
            topLeft = Offset(cx - 5.5f * u, cy + 14.5f * u),
            size = Size(11f * u, 3.2f * u),
            cornerRadius = CornerRadius(2f * u, 2f * u)
        )

        // Cockpit Porthole (dark rim + bright cyan glass + reflection highlight)
        val windowCenter = Offset(cx, cy - 3.5f * u)
        drawCircle(
            color = Color(0xFF1E293B),
            radius = 5.8f * u,
            center = windowCenter
        )
        drawCircle(
            color = Color(0xFF22D3EE),
            radius = 4.2f * u,
            center = windowCenter
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = 1.3f * u,
            center = Offset(windowCenter.x - 1.4f * u, windowCenter.y - 1.4f * u)
        )
    }
}
