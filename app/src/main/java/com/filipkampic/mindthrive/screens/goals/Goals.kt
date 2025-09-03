package com.filipkampic.mindthrive.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.GoalProgress
import com.filipkampic.mindthrive.ui.goals.GoalCard
import com.filipkampic.mindthrive.ui.goals.AddCategoryDialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.GoalsViewModel
import com.filipkampic.mindthrive.viewmodel.GoalsViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Goals(navController: NavController) {
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao(), db.goalStepDao(), db.goalNoteDao(), db.goalCategoryDao())
    }
    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModelFactory(repository)
    )

    val goals by viewModel.filteredGoals.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var showAddCategory by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var showCompletedDialog by remember { mutableStateOf(false) }
    var showManageCategories by remember { mutableStateOf(false) }
    var showEditCategory by remember { mutableStateOf<String?>(null) }
    var showDialogMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteCategoryDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals", fontFamily = Montserrat) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Peach)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Peach)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Completed Goals", color = DarkBlue) },
                            onClick = {
                                expanded = false
                                showCompletedDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Manage Categories", color = DarkBlue) },
                            onClick = {
                                expanded = false
                                showManageCategories = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    navigationIconContentColor = Peach,
                    titleContentColor = Peach,
                    actionIconContentColor = Peach
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addGoal") }, containerColor = Peach) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = DarkBlue)
            }
        },
        containerColor = DarkBlue
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(bottom = 80.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { viewModel.selectCategory(category) },
                                label = {
                                    Text(
                                        category,
                                        color = if (selectedCategory == category) DarkBlue else Peach
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (selectedCategory == category) Peach else Color.Transparent,
                                    labelColor = Peach,
                                    selectedLabelColor = DarkBlue,
                                    selectedContainerColor = Peach,
                                    disabledContainerColor = Color.Transparent,
                                    disabledLabelColor = Peach
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showAddCategory = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Category",
                            tint = Peach
                        )
                    }
                }
            }

            items(goals, key = { it.id }) { goal ->
                val daysLeft = viewModel.calculateDaysRemaining(goal.deadline)
                val isOverdue = viewModel.isDeadlinePassed(goal.deadline)
                val progress by viewModel.goalProgress(goal.id).collectAsState(
                    initial = GoalProgress(0, 0, 0f)
                )
                val isCompleted by viewModel.goalCompleted(goal.id).collectAsState(initial = false)

                GoalCard(
                    goal = goal,
                    daysLeft = daysLeft,
                    isOverdue = isOverdue,
                    hasSteps = progress.total > 0,
                    progress = progress.ratio,
                    isCompleted = isCompleted,
                    isCompletedOnTime = goal.isCompletedOnTime,
                    onClick = {
                        navController.navigate("goalDetails/${goal.id}")
                    }
                )
            }
        }
    }

    if (showAddCategory) {
        AddCategoryDialog(
            value = newCategoryName,
            onValueChange = { newCategoryName = it },
            onDismiss = {
                showAddCategory = false
                newCategoryName = ""
            },
            onConfirm = { name ->
                val trimmed = name.trim()
                if (trimmed.isEmpty()) {
                    showDialogMessage = "Category name cannot be empty."
                } else {
                    scope.launch {
                        val errorMessage = viewModel.addCategory(name)
                        if (errorMessage == null) {
                            showAddCategory = false
                            newCategoryName = ""
                        } else {
                            showDialogMessage = errorMessage
                        }
                    }
                }
            }
        )
    }

    if (showCompletedDialog) {
        val completed by viewModel.completedGoals.collectAsState()

        AlertDialog(
            onDismissRequest = { showCompletedDialog = false },
            title = { Text("Completed Goals", color = DarkBlue, fontFamily = Montserrat) },
            text = {
                if (completed.isEmpty()) {
                    Text("No completed goals.", color = DarkBlue.copy(alpha = 0.8f))
                } else {
                    LazyColumn {
                        items(completed, key = { it.id }) { goal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        showCompletedDialog = false
                                        navController.navigate("goalDetails/${goal.id}")
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(goal.name, color = DarkBlue)
                            }
                            HorizontalDivider(color = DarkBlue.copy(alpha = 0.2f))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCompletedDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = Peach)
                ) {
                    Text("Close")
                }
            },
            containerColor = Peach,
            titleContentColor = DarkBlue,
            textContentColor = DarkBlue
        )
    }

    if (showManageCategories) {
        val cats by viewModel.categories.collectAsState()
        val userCategories = cats.filterNot { it.equals("All", true) || it.equals("General", true) }

        AlertDialog(
            onDismissRequest = { showManageCategories = false },
            title = { Text("Manage Categories", color = DarkBlue, fontFamily = Montserrat) },
            text = {
                if (userCategories.isEmpty()) {
                    Text("No categories.", color = DarkBlue.copy(alpha = 0.7f))
                } else {
                    LazyColumn {
                        items(userCategories, key = { it }) { name ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        showManageCategories = false
                                        showEditCategory = name
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(name, color = DarkBlue)
                            }
                            HorizontalDivider(color = DarkBlue.copy(alpha = 0.2f))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showManageCategories = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBlue,
                        contentColor = Peach
                    )
                ) {
                    Text("Close")
                }
            },
            containerColor = Peach,
            titleContentColor = DarkBlue,
            textContentColor = DarkBlue
        )
    }

    if (showEditCategory != null) {
        var text by remember { mutableStateOf(showEditCategory!!) }
        var error by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showEditCategory = null },
            title = {
                Text("Edit Category", color = DarkBlue, fontFamily = Montserrat)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            error = null
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = DarkBlue.copy(alpha = 0.5f),
                            cursorColor = DarkBlue,
                            focusedLabelColor = DarkBlue,
                            unfocusedLabelColor = DarkBlue.copy(alpha = 0.5f),
                            selectionColors = TextSelectionColors(
                                handleColor = DarkBlue,
                                backgroundColor = DarkBlue.copy(alpha = 0.2f)
                            )
                        )
                    )
                    if (error != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val old = showEditCategory!!
                        scope.launch {
                            val err = viewModel.renameCategory(old, text)
                            if (err == null) {
                                showEditCategory = null
                            } else {
                                showDialogMessage = err
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = Peach)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = { showEditCategory = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = DarkBlue)
                    ) {
                        Text("Cancel")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = { showDeleteCategoryDialog = showEditCategory },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError)
                    ) {
                        Text("Delete")
                    }
                }
            },
            containerColor = Peach,
            titleContentColor = DarkBlue,
            textContentColor = DarkBlue
        )
    }

    if (showDeleteCategoryDialog != null) {
        val categoryName = showDeleteCategoryDialog!!
        var deleteGoals by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showDeleteCategoryDialog = null },
            title = { Text("Delete Category '$categoryName'?", color = DarkBlue, fontFamily = Montserrat) },
            text = {
                Column {
                    Text(
                        text = "Do you want to delete all goals in this category or move them to General?",
                        color = DarkBlue
                    )
                    Spacer(Modifier.height(12.dp))


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { deleteGoals = false }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !deleteGoals,
                            onClick = { deleteGoals = false },
                            colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Move goals to General", color = DarkBlue)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { deleteGoals = true }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = deleteGoals,
                            onClick = { deleteGoals = true },
                            colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Delete goals", color = DarkBlue)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        viewModel.deleteCategory(categoryName, deleteGoals)
                        showDeleteCategoryDialog = null
                        showEditCategory = null
                    }
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteCategoryDialog = null }) {
                    Text("Cancel", color = DarkBlue)
                }
            },
            containerColor = Peach,
            titleContentColor = DarkBlue,
            textContentColor = DarkBlue
        )
    }

    showDialogMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { showDialogMessage = null },
            title = { Text("Validation Error", color = Peach) },
            text = { Text(message, color = Peach) },
            confirmButton = {
                TextButton(
                    onClick = { showDialogMessage = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Peach)
                ) {
                    Text("OK")
                }
            },
            containerColor = DarkBlue,
            titleContentColor = Peach,
            textContentColor = Peach
        )
    }
}