package com.filipkampic.mindthrive.screens.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.focus.FocusBottomNavigation
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.FocusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Focus(navController: NavController) {
    val context = LocalContext.current
    val focusViewModel: FocusViewModel = viewModel()

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

    var menuExpanded by remember { mutableStateOf(false) }
    val isAlarmEnabled by focusViewModel::isAlarmEnabled

    LaunchedEffect(Unit) {
        focusViewModel.observeAlarmEnabled(context)
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
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            enabled = !isTimerRunning.value
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(Peach)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isAlarmEnabled,
                                            onCheckedChange = {
                                                focusViewModel.saveAlarmEnabled(context, it)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = DarkBlue, uncheckedColor = DarkBlue)
                                        )
                                        Text("Enable Alarm", color = DarkBlue)
                                    }
                                },
                                onClick = {
                                    focusViewModel.saveAlarmEnabled(context, !isAlarmEnabled)
                                }
                            )
                        }
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
                onShowResultsChange = { pomodoroShowResults = it },
                isAlarmEnabled = isAlarmEnabled,
                viewModel = focusViewModel
            )
            "stopwatch" -> Stopwatch(
                modifier = Modifier.padding(padding),
                viewModel = focusViewModel
            )
            "statistics" -> Statistics(
                modifier = Modifier.padding(padding),
                viewModel = focusViewModel
            )
        }
    }
}
