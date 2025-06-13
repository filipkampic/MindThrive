package com.filipkampic.mindthrive.screens.focus

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.focus.FocusResults
import com.filipkampic.mindthrive.ui.focus.FocusTimer
import com.filipkampic.mindthrive.ui.focus.SessionPlanner
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.FocusViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun Pomodoro(
    modifier: Modifier = Modifier,
    isRunning: MutableState<Boolean>,
    onShowPlannerChange: (Boolean) -> Unit,
    onShowResultsChange: (Boolean) -> Unit,
    viewModel: FocusViewModel
) {
    val focusManager = LocalFocusManager.current

    val durationOptions = listOf(1, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120)
    val sessionsOptions = listOf(1, 2, 3, 4, 5, 6)
    val breakOptions = (1..15).toList()

    var selectedDurationIndex by viewModel::pomodoroDurationIndex
    var selectedSessionIndex by viewModel::pomodoroSessionIndex
    var selectedBreakIndex by viewModel::pomodoroBreakIndex

    val sessionDuration = durationOptions[selectedDurationIndex]
    val sessionCount = sessionsOptions[selectedSessionIndex]
    val breakDuration = breakOptions[selectedBreakIndex]

    var currentSession by rememberSaveable { mutableIntStateOf(1) }
    var completedSessions by rememberSaveable { mutableIntStateOf(0) }
    var isOnBreak by rememberSaveable { mutableStateOf(false) }
    var isPaused by rememberSaveable { mutableStateOf(false) }
    var isWaitingForNextPhase by rememberSaveable { mutableStateOf(false) }
    var isReset by rememberSaveable { mutableStateOf(true) }
    var timeLeft by rememberSaveable { mutableIntStateOf(durationOptions[selectedDurationIndex] * 60) }
    var visualInitialTime by rememberSaveable { mutableIntStateOf(durationOptions[selectedDurationIndex] * 60) }
    var totalTimeInSeconds by rememberSaveable { mutableIntStateOf(0) }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeText = "%02d:%02d".format(minutes, seconds)

    val progress = if (visualInitialTime > 0)
        (timeLeft.toFloat() / visualInitialTime.toFloat()).coerceIn(0f, 1f)
    else 0f

    var showPlanner by remember { mutableStateOf(false) }
    val sessionPlans = viewModel.pomodoroSessionPlans
    var plannedActivities = viewModel.pomodoroPlannedActivities
    val activityName = plannedActivities?.getOrNull(currentSession - 1).orEmpty()
    var triedToStartWithoutPlanning by remember { mutableStateOf(false) }

    var showResults by rememberSaveable { mutableStateOf(false) }

    fun resetAllStates() {
        currentSession = 1
        completedSessions = 0
        isOnBreak = false
        isPaused = false
        isWaitingForNextPhase = false
        isReset = true
        isRunning.value = false
        showResults = false
        timeLeft = sessionDuration * 60
        visualInitialTime = timeLeft
        totalTimeInSeconds = 0
        plannedActivities = null
        sessionPlans.replaceAll { "" }
    }

    LaunchedEffect(showPlanner) {
        onShowPlannerChange(showPlanner)
    }

    LaunchedEffect(showResults) {
        onShowResultsChange(showResults)
    }

    LaunchedEffect(sessionCount) {
        if (sessionPlans.size < sessionCount) {
            repeat(sessionCount - sessionPlans.size) { sessionPlans.add("") }
        } else if (sessionPlans.size > sessionCount) {
            repeat(sessionPlans.size - sessionCount) { sessionPlans.removeAt(sessionPlans.lastIndex) }
        }
    }

    LaunchedEffect(selectedDurationIndex) {
        if (!isRunning.value && !isOnBreak) {
            val newTime = sessionDuration * 60
            timeLeft = newTime
            visualInitialTime = newTime
        }
    }

    LaunchedEffect(isRunning.value, isPaused, isWaitingForNextPhase, timeLeft) {
        if (isRunning.value && !isPaused && !isWaitingForNextPhase) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
                totalTimeInSeconds++
            }

            if (timeLeft == 0) {
                if (!isOnBreak && currentSession == sessionCount) {
                    completedSessions++
                    isRunning.value = false
                    isWaitingForNextPhase = false
                    isReset = true
                    showResults = true
                } else {
                    if (!isOnBreak) {
                        completedSessions++
                    }
                    isPaused = true
                    isWaitingForNextPhase = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                focusManager.clearFocus()
            }
    ) {
        if (showResults) {
            FocusResults(
                title = "Deep Work Complete",
                totalTime = totalTimeInSeconds,
                sessions = completedSessions,
                activityName = plannedActivities?.take(completedSessions)?.joinToString("|"),
                onDone = {
                    showResults = false
                    resetAllStates()
                    plannedActivities = null

                    viewModel.pomodoroSessionPlans.clear()
                    repeat(sessionCount) { viewModel.pomodoroSessionPlans.add("") }
                }
            )
        } else {
            if (showPlanner) {
                SessionPlanner(
                    sessionCount = sessionCount,
                    sessionPlans = sessionPlans,
                    onSessionPlansChanged = { index, value -> viewModel.pomodoroSessionPlans[index] = value },
                    onPlanConfirmed = {
                        viewModel.pomodoroPlannedActivities = sessionPlans.toList()
                        triedToStartWithoutPlanning = false
                        showPlanner = false
                        isReset = true
                    },
                    onCancel = { showPlanner = false }
                )
                return@Box
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopCenter)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (plannedActivities != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    if (isOnBreak) "Break" else "Now focusing on:",
                                    color = Peach
                                )
                                if (!isOnBreak) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(activityName, color = Peach)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    FocusTimer(
                        timeText = timeText,
                        ringColor = if (isOnBreak) Color(0xFF88C0D0) else Peach,
                        progress = progress
                    )

                    Spacer(Modifier.height(16.dp))

                    if (isRunning.value || isPaused || isWaitingForNextPhase) {
                        Text(
                            text = "Session $currentSession / $sessionCount",
                            color = Peach,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

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
                                    colors = SliderDefaults.colors(
                                        thumbColor = Peach,
                                        activeTrackColor = Peach
                                    ),
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
                                    colors = SliderDefaults.colors(
                                        thumbColor = Peach,
                                        activeTrackColor = Peach
                                    ),
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
                                    colors = SliderDefaults.colors(
                                        thumbColor = Peach,
                                        activeTrackColor = Peach
                                    ),
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (!isRunning.value) {
                                        showPlanner = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isRunning.value) Peach else Peach.copy(alpha = 0.9f),
                                    contentColor = if (!isRunning.value) DarkBlue else DarkBlue.copy(alpha = 0.9f),
                                    disabledContainerColor = Peach.copy(alpha = 0.9f),
                                    disabledContentColor = DarkBlue.copy(alpha = 0.9f)
                                ),
                                enabled = !isRunning.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .blockIfRunning(isRunning.value)
                            ) {
                                Text("Plan Activities", color = DarkBlue)
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp)
            ) {
                if (isWaitingForNextPhase && !(currentSession == sessionCount && !isOnBreak)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Button(
                            onClick = {
                                isWaitingForNextPhase = false
                                isPaused = false

                                if (isOnBreak) {
                                    isOnBreak = false

                                    if (currentSession >= sessionCount) {
                                        isRunning.value = false
                                        isWaitingForNextPhase = false
                                        isReset = true
                                        return@Button
                                    }

                                    currentSession++
                                    timeLeft = sessionDuration * 60
                                    visualInitialTime = timeLeft
                                } else {
                                    if (currentSession == sessionCount) {
                                        isRunning.value = false
                                        isWaitingForNextPhase = false
                                        isReset = true
                                        showResults = true
                                        return@Button
                                    }

                                    isOnBreak = true
                                    timeLeft = breakDuration * 60
                                    visualInitialTime = timeLeft
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Peach),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = if (isOnBreak) "Start Session" else "Start Break",
                                color = DarkBlue
                            )
                        }

                        IconButton(
                            onClick = {
                                isRunning.value = false
                                isPaused = false
                                isWaitingForNextPhase = false
                                isReset = true

                                if (totalTimeInSeconds > 0) {
                                    showResults = true
                                } else {
                                    timeLeft = sessionDuration * 60
                                    visualInitialTime = timeLeft
                                }

                                currentSession = 1
                                isOnBreak = false
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = Peach
                            )
                        }
                    }
                } else if (isReset) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                if (plannedActivities == null) {
                                    triedToStartWithoutPlanning = true
                                } else {
                                    currentSession = 1
                                    isOnBreak = false
                                    timeLeft = sessionDuration * 60
                                    visualInitialTime = timeLeft
                                    isRunning.value = true
                                    isPaused = false
                                    isWaitingForNextPhase = false
                                    isReset = false
                                    triedToStartWithoutPlanning = false
                                }
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
                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (triedToStartWithoutPlanning) {
                                Text("Please plan your activities first", color = Color.Red)
                            }
                        }
                    }

                } else if (isRunning.value) {
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
                                isPaused = false
                                isWaitingForNextPhase = false
                                isReset = true

                                if (totalTimeInSeconds > 0) {
                                    showResults = true
                                } else {
                                    timeLeft = sessionDuration * 60
                                    visualInitialTime = timeLeft
                                }

                                currentSession = 1
                                isOnBreak = false
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
                } else {
                    Spacer(modifier = Modifier.height(0.dp))
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
