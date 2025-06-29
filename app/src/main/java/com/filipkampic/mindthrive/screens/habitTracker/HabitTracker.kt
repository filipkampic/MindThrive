package com.filipkampic.mindthrive.screens.habitTracker

import HabitSection
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.notification.habitTracker.scheduleHabitReminder
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import com.filipkampic.mindthrive.viewmodel.HabitViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTracker(navController: NavController) {
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        HabitRepository(db.habitDao(), db.habitCheckDao())
    }
    val viewModel: HabitViewModel = viewModel(
        factory = HabitViewModelFactory(repository)
    )
    val habits by viewModel.habits.collectAsState(initial = emptyList())

    var showHabitTypeDialog by remember { mutableStateOf(false) }
    val currentDate = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)) }

    var showDialogForMeasurableHabit by remember { mutableStateOf<Habit?>(null) }
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(habits) {
        habits.filter { !it.reminder.isNullOrBlank() }.forEach { habit ->
            scheduleHabitReminder(context, habit)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit Tracker") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("habitStats") }) {
                        Icon(Icons.Default.PieChart, contentDescription = "Stats")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = Peach,
                    navigationIconContentColor = Peach,
                    actionIconContentColor = Peach
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showHabitTypeDialog = true },
                containerColor = Peach
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit", tint = DarkBlue)
            }
        },
        containerColor = DarkBlue
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(currentDate, style = MaterialTheme.typography.headlineMedium, color = Peach)
            }

            Spacer(modifier = Modifier.height(16.dp))

            HabitSection(
                habits = habits,
                onToggle = { habit: Habit -> viewModel.toggleHabit(habit) },
                onClick = { habit: Habit -> navController.navigate("habitDetail/${habit.id}") },
                onEnterAmount = { habit: Habit -> showDialogForMeasurableHabit = habit },
                getChecks = { habitId: Int ->
                    viewModel.getAllChecksForHabit(habitId).collectAsState(initial = emptyList()).value
                },
                onMove = { newList: List<Habit> ->
                    val updatedList = newList.mapIndexed { index, habit -> habit.copy(position = index) }
                    viewModel.moveHabits(updatedList)
                }
            )
        }

        if (showHabitTypeDialog) {
            AlertDialog(
                onDismissRequest = { showHabitTypeDialog = false },
                confirmButton = {},
                containerColor = DarkBlue,
                title = { Text("New Habit", color = Peach) },
                text = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                showHabitTypeDialog = false
                                navController.navigate("yesOrNoHabit")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Peach,
                                contentColor = DarkBlue
                            )
                        ) {
                            Text("Yes or No")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                showHabitTypeDialog = false
                                navController.navigate("measurableHabit")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Peach,
                                contentColor = DarkBlue
                            )
                        ) {
                            Text("Measurable")
                        }
                    }
                }
            )
        }

        if (showDialogForMeasurableHabit != null) {
            AlertDialog(
                onDismissRequest = { showDialogForMeasurableHabit = null },
                confirmButton = {
                    TextButton(onClick = {
                        val amount = inputText.toFloatOrNull()
                        if (amount != null) {
                            viewModel.saveMeasurableCheck(showDialogForMeasurableHabit!!, amount)
                        }
                        showDialogForMeasurableHabit = null
                        inputText = ""
                    }) {
                        Text("Save", color = Peach)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialogForMeasurableHabit = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = Peach)
                    ) {
                        Text("Cancel")
                    }
                },
                title = { Text("Enter amount", color = Peach) },
                text = {
                    CompositionLocalProvider(
                        LocalTextSelectionColors provides TextSelectionColors(
                            handleColor = Peach,
                            backgroundColor = Peach.copy(alpha = 0.4f)
                        )
                    ) {
                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            label = {
                                Text(
                                    "Amount",
                                    color = Peach
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DarkBlue,
                                unfocusedContainerColor = DarkBlue,
                                focusedIndicatorColor = Peach,
                                unfocusedIndicatorColor = Peach,
                                cursorColor = Peach
                            ),
                            textStyle = TextStyle(color = Peach)
                        )
                    }
                },
                containerColor = DarkBlue
            )
        }
    }
}
