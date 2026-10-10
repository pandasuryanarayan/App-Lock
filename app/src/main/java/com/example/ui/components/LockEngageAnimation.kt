package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay

/**
 * Dependency-free open -> locked padlock transition drawn on [Canvas].
 *
 * The overlay opens with the shackle lifted and detached (grey); it then
 * drops and closes while the body tints cyan. Single animatable float, one
 * draw pass per frame — cheap on low-end devices, zero new dependencies.
 */
@Composable
fun LockEngageAnimation(
    modifier: Modifier = Modifier
) {
    var engaged by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (engaged) 1f else 0f,
        animationSpec = tween(durationMillis = 750),
        label = "lock_engage_progress"
    )

    LaunchedEffect(Unit) {
        delay(300)
        engaged = true
    }

    val bodyColor = lerp(SlateSurfaceVariant, CyberCyan, progress)
    val shackleColor = lerp(TextSecondaryDark, CyberCyanLight, progress)
    val shackleLift = (1f - progress) * 0.16f // fraction of canvas height
    val shackleGap = (1f - progress) * 80f // degrees missing on the open side

    Canvas(
        modifier = modifier
            .size(112.dp)
            .scale(0.92f + 0.08f * progress)
            .testTag("lock_engage_animation")
    ) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.075f

        // Shackle: arch that drops down and closes as progress -> 1.
        val archLeft = w * 0.30f
        val archTop = h * 0.10f + shackleLift * h
        val archRight = w * 0.70f
        val archBottom = h * 0.55f
        drawArc(
            color = shackleColor,
            startAngle = 180f,
            sweepAngle = 180f - shackleGap,
            useCenter = false,
            topLeft = Offset(archLeft, archTop),
            size = Size(archRight - archLeft, archBottom - archTop),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        // Left post stays connected to the body; right post meets it on close.
        val postTop = archTop + (archBottom - archTop) / 2f
        val bodyTop = h * 0.50f
        drawLine(
            color = shackleColor,
            start = Offset(archLeft + stroke / 2f, postTop),
            end = Offset(archLeft + stroke / 2f, bodyTop + stroke / 2f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        if (progress > 0.85f) {
            drawLine(
                color = shackleColor,
                start = Offset(archRight - stroke / 2f, postTop),
                end = Offset(archRight - stroke / 2f, bodyTop + stroke / 2f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }

        // Body.
        val bodyLeft = w * 0.20f
        val bodyRight = w * 0.80f
        val bodyBottom = h * 0.94f
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(bodyLeft, bodyTop),
            size = Size(bodyRight - bodyLeft, bodyBottom - bodyTop),
            cornerRadius = CornerRadius(w * 0.06f, w * 0.06f)
        )

        // Keyhole.
        val cx = w / 2f
        val cy = bodyTop + (bodyBottom - bodyTop) * 0.38f
        drawCircle(
            color = ObsidianDark,
            radius = w * 0.055f,
            center = Offset(cx, cy)
        )
        drawRoundRect(
            color = ObsidianDark,
            topLeft = Offset(cx - w * 0.028f, cy),
            size = Size(w * 0.056f, (bodyBottom - bodyTop) * 0.26f),
            cornerRadius = CornerRadius(w * 0.028f, w * 0.028f)
        )

        // Engaged tick glow dot on the shackle once closed.
        if (progress >= 1f) {
            drawCircle(
                color = Color.White,
                radius = w * 0.022f,
                center = Offset(archRight - stroke / 2f, postTop - stroke)
            )
        }
    }
}
