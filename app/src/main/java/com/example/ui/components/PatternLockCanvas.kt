package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberCyanLight
import kotlin.math.hypot

@Composable
fun PatternLockCanvas(
    modifier: Modifier = Modifier,
    isStealth: Boolean = false,
    isError: Boolean = false,
    onPatternStart: () -> Unit = {},
    onPatternComplete: (String) -> Unit
) {
    var selectedDots by remember { mutableStateOf(listOf<Int>()) }
    var currentTouchOffset by remember { mutableStateOf<Offset?>(null) }

    val dotColor = if (isError) MaterialTheme.colorScheme.error else CyberCyanLight
    val pathColor = if (isError) MaterialTheme.colorScheme.error else CyberCyanLight.copy(alpha = 0.8f)

    val density = LocalDensity.current
    val touchRadiusPx = remember(density) { with(density) { 50.dp.toPx() } }

    BoxWithConstraints(modifier = modifier) {
        val sizePx = constraints.maxWidth.toFloat()
        val step = sizePx / 4f

        // Calculate 3x3 grid dot centers
        val dotCenters = remember(sizePx) {
            val list = mutableListOf<Offset>()
            for (row in 0..2) {
                for (col in 0..2) {
                    list.add(Offset(step * (col + 1), step * (row + 1)))
                }
            }
            list
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isError) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onPatternStart()
                            selectedDots = emptyList()
                            currentTouchOffset = offset
                            findNearestDot(offset, dotCenters, touchRadiusPx)?.let { dotIdx ->
                                if (!selectedDots.contains(dotIdx)) {
                                    selectedDots = selectedDots + dotIdx
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            currentTouchOffset = change.position
                            findNearestDot(change.position, dotCenters, touchRadiusPx)?.let { dotIdx ->
                                if (!selectedDots.contains(dotIdx)) {
                                    selectedDots = selectedDots + dotIdx
                                }
                            }
                        },
                        onDragEnd = {
                            currentTouchOffset = null
                            if (selectedDots.isNotEmpty()) {
                                onPatternComplete(selectedDots.joinToString(","))
                            }
                        },
                        onDragCancel = {
                            currentTouchOffset = null
                            selectedDots = emptyList()
                        }
                    )
                }
        ) {
            // Draw lines connecting selected dots
            if (!isStealth && selectedDots.isNotEmpty()) {
                val path = Path().apply {
                    val firstOffset = dotCenters[selectedDots.first()]
                    moveTo(firstOffset.x, firstOffset.y)
                    for (i in 1 until selectedDots.size) {
                        val nextOffset = dotCenters[selectedDots[i]]
                        lineTo(nextOffset.x, nextOffset.y)
                    }
                    currentTouchOffset?.let { touchOffset ->
                        lineTo(touchOffset.x, touchOffset.y)
                    }
                }
                drawPath(
                    path = path,
                    color = pathColor,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Draw 3x3 Dots
            dotCenters.forEachIndexed { index, center ->
                val isSelected = selectedDots.contains(index)
                val dotRadius = if (isSelected) 18.dp.toPx() else 12.dp.toPx()

                // Outer circle ring if selected
                if (isSelected && !isStealth) {
                    drawCircle(
                        color = dotColor.copy(alpha = 0.3f),
                        radius = 32.dp.toPx(),
                        center = center
                    )
                }

                // Inner solid dot
                drawCircle(
                    color = if (isSelected) dotColor else Color.Gray.copy(alpha = 0.5f),
                    radius = dotRadius,
                    center = center
                )
            }
        }
    }
}

private fun findNearestDot(touch: Offset, dotCenters: List<Offset>, maxRadiusPx: Float): Int? {
    dotCenters.forEachIndexed { idx, center ->
        val dist = hypot(touch.x - center.x, touch.y - center.y)
        if (dist <= maxRadiusPx) {
            return idx
        }
    }
    return null
}
