package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyanLight

@Composable
fun PinPadView(
    pinLength: Int = 4,
    enteredPin: String,
    isError: Boolean = false,
    onPinChange: (String) -> Unit,
    onPinComplete: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // PIN Indicator Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until pinLength) {
                val isFilled = i < enteredPin.length
                val animatedColor by animateColorAsState(
                    targetValue = when {
                        isError -> MaterialTheme.colorScheme.error
                        isFilled -> CyberCyanLight
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    label = "dotColor"
                )

                Box(
                    modifier = Modifier
                        .size(if (isFilled) 20.dp else 16.dp)
                        .clip(CircleShape)
                        .background(animatedColor)
                )
            }
        }

        // Keypad Grid
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "DEL")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { key ->
                        if (key.isEmpty()) {
                            Spacer(modifier = Modifier.size(72.dp))
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("keypad_btn_$key")
                                    .clickable {
                                        if (key == "DEL") {
                                            if (enteredPin.isNotEmpty()) {
                                                val newPin = enteredPin.dropLast(1)
                                                onPinChange(newPin)
                                            }
                                        } else {
                                            if (enteredPin.length < pinLength) {
                                                val newPin = enteredPin + key
                                                onPinChange(newPin)
                                                if (newPin.length == pinLength) {
                                                    onPinComplete(newPin)
                                                }
                                            }
                                        }
                                    }
                            ) {
                                if (key == "DEL") {
                                    Icon(
                                        imageVector = AppIcons.Backspace,
                                        contentDescription = "Delete key",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
