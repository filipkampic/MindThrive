package com.filipkampic.mindthrive.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.Goal
import com.filipkampic.mindthrive.ui.tasks.DatePickerDialogContent
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.GoalsViewModel
import com.filipkampic.mindthrive.viewmodel.GoalsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoal(
    navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao())
    }
    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModelFactory(repository)
    )

    val categories by viewModel.categories.collectAsState()

    var name by remember { mutableStateOf("") }
    var motivation by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedCategory by remember { mutableStateOf(categories.getOrNull(1) ?: "") }

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
    var showDatePicker by remember { mutableStateOf(false) }

    var showEmptyNameDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDuplicateNameDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Peach,
            backgroundColor = Peach.copy(alpha = 0.4f)
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Goal", color = Peach) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Peach
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (name.isBlank()) {
                                showEmptyNameDialog = true
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    val exists = viewModel.goalNameExists(name)
                                    if (exists) {
                                        showDuplicateNameDialog = true
                                    } else {
                                        val goal = Goal(
                                            name = name,
                                            motivation = motivation.takeIf { it.isNotBlank() },
                                            reward = reward.takeIf { it.isNotBlank() },
                                            deadLine = selectedDate,
                                            category = selectedCategory.ifBlank { "General" }
                                        )
                                        viewModel.insertGoal(goal)
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = Peach)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
                )
            },
            containerColor = DarkBlue
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Peach,
                    unfocusedBorderColor = Peach,
                    cursorColor = Peach,
                    focusedTextColor = Peach,
                    unfocusedTextColor = Peach,
                    focusedLabelColor = Peach,
                    unfocusedLabelColor = Peach
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("Specific and measurable goal") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = colors
                )
                OutlinedTextField(
                    value = motivation,
                    onValueChange = { motivation = it },
                    label = { Text("Motivation") },
                    placeholder = { Text("Why is this goal important?") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = colors
                )
                OutlinedTextField(
                    value = reward,
                    onValueChange = { reward = it },
                    label = { Text("Reward") },
                    placeholder = { Text("How will you reward yourself?") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = colors
                )
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Peach, contentColor = DarkBlue),
                    modifier = Modifier
                        .width(220.dp)
                        .height(50.dp)
                ) {
                    Text("Deadline: ${selectedDate.format(dateFormatter)}")
                }

                if (categories.size > 1) {
                    Box(
                        modifier = Modifier
                            .width(220.dp)
                            .height(50.dp)
                    ) {
                        Button(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Peach, contentColor = DarkBlue),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text("Category: ${selectedCategory.ifBlank { "General" }}")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Peach)
                        ) {
                            categories.filter { it != "All" }.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, color = DarkBlue) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Peach),
                                    colors = MenuDefaults.itemColors(
                                        textColor = DarkBlue
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialogContent(
            onDateSelected = {
                selectedDate = it
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }

    if (showEmptyNameDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyNameDialog = false },
            confirmButton = {
                TextButton(onClick = { showEmptyNameDialog = false }) {
                    Text("OK", color = Peach)
                }
            },
            title = { Text("Missing name", color = Peach) },
            text = { Text("Goal name cannot be empty", color = Peach) },
            containerColor = DarkBlue
        )
    }

    if (showDuplicateNameDialog) {
        AlertDialog(
            onDismissRequest = { showDuplicateNameDialog = false },
            confirmButton = {
                TextButton(onClick = { showDuplicateNameDialog = false }) {
                    Text("OK", color = Peach)
                }
            },
            title = { Text("Goal already exists", color = Peach) },
            text = { Text("You already have a goal with that name.", color = Peach) },
            containerColor = DarkBlue
        )
    }
}
