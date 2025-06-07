package com.filipkampic.mindthrive.screens.focus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme.colors
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun Pomodoro(
    modifier: Modifier = Modifier,
    isRunning: MutableState<Boolean>
) {
    var activityName by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val interactionSource = remember { MutableInteractionSource() }

    val durationOptions = listOf(10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120)
    val sessionsOptions = listOf(1, 2, 3, 4, 5, 6)
    val breakOptions = (1..15).toList()

    var selectedDurationIndex by rememberSaveable { mutableIntStateOf(3) }
    var selectedSessionIndex by rememberSaveable { mutableIntStateOf(3) }
    var selectedBreakIndex by rememberSaveable { mutableIntStateOf(4) }

    val sessionDuration = durationOptions[selectedDurationIndex]
    val sessionCount = sessionsOptions[selectedSessionIndex]
    val breakDuration = breakOptions[selectedBreakIndex]

    val totalSeconds by rememberSaveable { mutableIntStateOf(sessionDuration * 60) }
    var timeLeft by rememberSaveable { mutableIntStateOf(totalSeconds) }
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeText = "%02d:%02d".format(minutes, seconds)

    val currentSession by rememberSaveable { mutableIntStateOf(1) }
    var isPaused by rememberSaveable { mutableStateOf(false) }

    val progress = timeLeft.toFloat() / totalSeconds

    LaunchedEffect(isRunning.value, isPaused, timeLeft) {
        while(isRunning.value && !isPaused && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Peach.copy(alpha = 0.2f),
                        style = Stroke(12f)
                    )
                    drawArc(
                        color = Peach,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(12f)
                    )
                }
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displayLarge,
                    color = Peach
                )
            }

            Spacer(Modifier.height(16.dp))

            val customTextSelectionColors = TextSelectionColors(
                handleColor = Peach,
                backgroundColor = Peach.copy(alpha = 0.4f)
            )

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                OutlinedTextField(
                    value = activityName,
                    onValueChange = { if (!isRunning.value) activityName = it },
                    label = { Text("Activity name", color = Peach) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .blockIfRunning(isRunning.value),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Peach,
                        focusedBorderColor = Peach,
                        cursorColor = Peach,
                        focusedTextColor = Peach,
                        unfocusedTextColor = Peach
                    ),
                    interactionSource = interactionSource,
                    readOnly = isRunning.value
                )
            }

            Spacer(Modifier.height(8.dp))

            Text("Session $currentSession / $sessionCount", color = Peach)

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Duration", color = Peach)
                    Text("$sessionDuration min", color = Peach)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sessions", color = Peach)
                    Text("$sessionCount", color = Peach)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Break", color = Peach)
                    Text("$breakDuration min", color = Peach)
                }
            }

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier.alpha(if (isRunning.value) 0.5f else 1f)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Slider(
                            value = selectedDurationIndex.toFloat(),
                            onValueChange = {
                                if (!isRunning.value) selectedDurationIndex =
                                    it.roundToInt().coerceIn(durationOptions.indices)
                            },
                            valueRange = 0f..(durationOptions.size - 1).toFloat(),
                            steps = durationOptions.size - 2,
                            modifier = Modifier
                                .weight(1f)
                                .blockIfRunning(isRunning.value),
                            colors = SliderDefaults.colors(thumbColor = Peach, activeTrackColor = Peach),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        Spacer(Modifier.width(8.dp))
                        Slider(
                            value = selectedSessionIndex.toFloat(),
                            onValueChange = {
                                if (!isRunning.value) selectedSessionIndex =
                                    it.roundToInt().coerceIn(sessionsOptions.indices)
                            },
                            valueRange = 0f..(sessionsOptions.size - 1).toFloat(),
                            steps = sessionsOptions.size - 2,
                            modifier = Modifier
                                .weight(1f)
                                .blockIfRunning(isRunning.value),
                            colors = SliderDefaults.colors(thumbColor = Peach, activeTrackColor = Peach),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        Spacer(Modifier.width(8.dp))
                        Slider(
                            value = selectedBreakIndex.toFloat(),
                            onValueChange = {
                                if (!isRunning.value) selectedBreakIndex =
                                    it.roundToInt().coerceIn(breakOptions.indices)
                            },
                            valueRange = 0f..(breakOptions.size - 1).toFloat(),
                            steps = breakOptions.size - 2,
                            modifier = Modifier
                                .weight(1f)
                                .blockIfRunning(isRunning.value),
                            colors = SliderDefaults.colors(thumbColor = Peach, activeTrackColor = Peach),
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            if (!isRunning.value) {
                                selectedDurationIndex = durationOptions.indexOf(25)
                                selectedSessionIndex = sessionsOptions.indexOf(4)
                                selectedBreakIndex = breakOptions.indexOf(5)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Peach),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .blockIfRunning(isRunning.value)
                    ) {
                        Text("Set Default", color = DarkBlue)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 130.dp)
        ) {
            if (!isRunning.value) {
                IconButton(
                    onClick = {
                        timeLeft = sessionDuration * 60
                        isRunning.value = true
                        isPaused = false
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.fillMaxSize(),
                        tint = Peach
                    )
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    IconButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            modifier = Modifier.fillMaxSize(),
                            tint = Peach
                        )
                    }

                    IconButton(
                        onClick = {
                            isRunning.value = false
                            timeLeft = sessionDuration * 60
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "End",
                            modifier = Modifier.fillMaxSize(),
                            tint = Peach
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.blockIfRunning(isRunning: Boolean): Modifier {
    return if (isRunning) {
        this.pointerInput(Unit) {}
    } else {
        this
    }
}