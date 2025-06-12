package com.filipkampic.mindthrive.screens.focus

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.focus.FocusBottomNavigation
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Focus(navController: NavController) {
    var currentTab by rememberSaveable { mutableStateOf("pomodoro") }

    val isTimerRunning = remember { mutableStateOf(false) }

    var pomodoroShowPlanner by remember { mutableStateOf(false) }
    var pomodoroShowResults by remember { mutableStateOf(false) }

    val shouldShowBottomBar = when (currentTab) {
        "pomodoro" -> !pomodoroShowPlanner && !pomodoroShowResults
        "stopwatch" -> true
        "statistics" -> true
        else -> true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isTimerRunning.value) {
                            if (currentTab == "statistics") currentTab = "pomodoro"
                            else navController.navigate("home")
                        }
                    }) {
                        Icon(
                            imageVector = if (currentTab == "statistics") Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Home,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { Text(currentTab.replaceFirstChar { it.uppercase() }) },
                actions = {
                    if (currentTab != "statistics") {
                        IconButton(
                            onClick = {
                                if (!isTimerRunning.value) {
                                    currentTab = "statistics"
                                }
                            },
                            enabled = !isTimerRunning.value
                        ) {
                            Icon(Icons.Default.PieChart, contentDescription = "Statistics")
                        }
                    }
                    IconButton(onClick = { /* TODO: hamburger meni */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    navigationIconContentColor = Peach,
                    titleContentColor = Peach,
                    actionIconContentColor = Peach
                ),
                modifier = Modifier.alpha(if (isTimerRunning.value) 0.5f else 1f)
            )
        },
        bottomBar = {
            if (shouldShowBottomBar && currentTab != "statistics") {
                Box(modifier = Modifier.alpha(if (isTimerRunning.value) 0.5f else 1f)) {
                    FocusBottomNavigation(
                        current = currentTab,
                        onTabSelected = { selectedTab ->
                            if (!isTimerRunning.value) currentTab = selectedTab
                        }
                    )
                }
            }
        }
    ) { padding ->
        when (currentTab) {
            "pomodoro" -> Pomodoro(
                modifier = Modifier.padding(padding),
                isRunning = isTimerRunning,
                onShowPlannerChange = { pomodoroShowPlanner = it },
                onShowResultsChange = { pomodoroShowResults = it }
            )
            "stopwatch" -> Stopwatch(modifier = Modifier.padding(padding))
            "statistics" -> {
                Text("Statistics ", modifier = Modifier.padding(padding))
            }
        }
    }
}
