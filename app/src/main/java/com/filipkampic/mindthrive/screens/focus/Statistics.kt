package com.filipkampic.mindthrive.screens.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Statistics(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var activityName by rememberSaveable { mutableStateOf("") }
    var selectedTask by rememberSaveable { mutableStateOf<String?>(null) }

    var sessionCount by rememberSaveable { mutableStateOf(4) }
    var sessionDuration by rememberSaveable { mutableStateOf(25) }
    var breakDuration by rememberSaveable { mutableStateOf(5) }

    var currentSession by rememberSaveable { mutableStateOf(1) }

    var isRunning by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Statistics") },
                actions = {
                    IconButton(onClick = { /* TODO: hamburger meni */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isRunning) "24:37" else "${sessionDuration.toString().padStart(2, '0')}:00",
                    style = MaterialTheme.typography.displayLarge
                )
            }
        }

        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text("Activity name") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Session $currentSession of $sessionCount",
            modifier = Modifier.padding(top = 8.dp)
        )

        var taskExpanded by remember { mutableStateOf(false) }
        val tasks = listOf("Math HW", "Read Book", "Clean Desk")

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { taskExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedTask ?: "Select task")
            }
            DropdownMenu(
                expanded = taskExpanded,
                onDismissRequest = { taskExpanded = false }
            ) {
                tasks.forEach { task ->
                    DropdownMenuItem(
                        text = { Text(task) },
                        onClick = {
                            selectedTask = task
                            taskExpanded = false
                        }
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Duration: $sessionDuration min")
            Slider(
                value = sessionDuration.toFloat(),
                onValueChange = { sessionDuration = it.toInt() },
                valueRange = 5f..60f,
                steps = 11
            )

            Text("Sessions: $sessionCount")
            Slider(
                value = sessionCount.toFloat(),
                onValueChange = { sessionCount = it.toInt() },
                valueRange = 1f..10f,
                steps = 8
            )

            Text("Break: $breakDuration min")
            Slider(
                value = breakDuration.toFloat(),
                onValueChange = { breakDuration = it.toInt() },
                valueRange = 1f..30f,
                steps = 14
            )
        }

        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(if (isRunning) "Pause" else "Start")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = { /* TODO */ }) {
                Text("Ambient Sound")
            }
            OutlinedButton(onClick = { /* TODO */ }) {
                Text("Block Notifications")
            }
        }
    }
}