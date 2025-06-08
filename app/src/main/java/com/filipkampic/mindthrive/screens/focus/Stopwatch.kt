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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.focus.ActivityNameInput
import com.filipkampic.mindthrive.ui.focus.FocusTimer
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import kotlinx.coroutines.delay

@Composable
fun Stopwatch(modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current

    var isRunning by rememberSaveable { mutableStateOf(false) }
    var isPaused by rememberSaveable { mutableStateOf(false) }
    var time by rememberSaveable { mutableIntStateOf(0) }
    var activityName by rememberSaveable { mutableStateOf("") }

    val minutes = time / 60
    val seconds = time % 60
    val timeText = "%02d:%02d".format(minutes, seconds)

    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused) {
            delay(1000)
            time++
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
                .background(DarkBlue)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            FocusTimer(timeText = timeText, ringColor = Peach)

            Spacer(modifier = Modifier.height(16.dp))

            ActivityNameInput(
                value = activityName,
                onValueChange = { activityName = it },
                isReadOnly = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!isRunning) {
                IconButton(
                    onClick = {
                        isRunning = true
                        isPaused = false
                        time = 0
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
                            isRunning = false
                            isPaused = false
                            time = 0
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.fillMaxSize(),
                            tint = Peach
                        )
                    }
                }
            }
        }
    }
}
