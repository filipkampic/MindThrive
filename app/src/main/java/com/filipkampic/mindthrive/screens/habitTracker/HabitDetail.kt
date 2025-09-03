package com.filipkampic.mindthrive.screens.habitTracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.habitTracker.HabitStatistics
import com.filipkampic.mindthrive.ui.habitTracker.habitOverview.MonthlyProgressOverview
import com.filipkampic.mindthrive.ui.habitTracker.habitOverview.WeeklyProgressOverview
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Inter
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetail(
    habitId: Int,
    navController: NavController,
    viewModel: HabitViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Weekly Progress", "Monthly Progress")
    val coroutineScope = rememberCoroutineScope()

    val checks by viewModel.getAllChecksForHabit(habitId).collectAsState(initial = emptyList())
    val habits by viewModel.habits.collectAsState()
    val habit = habits.find { it.id == habitId } ?: return

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(checks) {
        viewModel.syncHabitStreaksWithChecks(checks)
    }

    Scaffold(
        containerColor = DarkBlue,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Peach)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (habit.isMeasurable) {
                            navController.navigate("edit_measurable/${habit.id}")
                        } else {
                            navController.navigate("edit_yesOrNo/${habit.id}")
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Peach)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Peach)
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Peach,
                        fontFamily = Montserrat
                    )
                    habit.description?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Peach,
                            fontFamily = Inter
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (habit.isMeasurable && habit.target != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = "Target",
                                tint = Peach
                            )
                            Text(
                                text = "${habit.target.toInt()} ${habit.unit ?: ""}",
                                color = Peach,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Reminder",
                            tint = Peach
                        )
                        Text(
                            text = if (habit.reminder.isNullOrEmpty()) "Off" else "On",
                            color = Peach,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = "Frequency",
                            tint = Peach
                        )
                        Text(
                            text = habit.frequency ?: "Every day",
                            color = Peach,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBlue,
                contentColor = Peach,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Peach
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) Peach else Peach.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == 0) {
                WeeklyProgressOverview(
                    habitId = habit.id,
                    isMeasurable = habit.isMeasurable,
                    checks = checks
                )
            } else {
                MonthlyProgressOverview(habitId = habit.id, isMeasurable = habit.isMeasurable)
            }

            Spacer(modifier = Modifier.height(32.dp))

            HabitStatistics(habit = habit, viewModel = viewModel)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Confirm Deletion", color = DarkBlue, fontFamily = Montserrat)
            },
            text = {
                Text(
                    text = "Are you sure you want to delete this habit?",
                    color = DarkBlue.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        coroutineScope.launch {
                          viewModel.deleteHabit(habit)
                          navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBlue,
                        contentColor = Peach
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = DarkBlue)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Peach,
            titleContentColor = DarkBlue,
            textContentColor = DarkBlue
        )
    }
}
