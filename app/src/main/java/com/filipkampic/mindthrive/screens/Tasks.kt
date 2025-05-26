package com.filipkampic.mindthrive.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.TaskRepository
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.ui.tasks.AddCategoryDialog
import com.filipkampic.mindthrive.ui.tasks.CategoryFilterRow
import com.filipkampic.mindthrive.ui.tasks.TaskSection
import com.filipkampic.mindthrive.viewmodel.TaskListViewModel
import com.filipkampic.mindthrive.ui.tasks.AddTaskDialog
import com.filipkampic.mindthrive.ui.tasks.EditTaskDialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun TasksPreview() {
    Tasks(navController = NavController(LocalContext.current))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tasks(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel = remember {
        val db = AppDatabase.getDatabase(context)
        val repo = TaskRepository(db.taskDao(), db.categoryDao())
        TaskListViewModel(repo)
    }

    val showDialog = remember { mutableStateOf(false) }
    val showCompleted = remember { mutableStateOf(true) }
    val expandedMenu = remember { mutableStateOf(false) }
    val tasks by viewModel.tasks.collectAsState()
    val editingTask = remember { mutableStateOf<Task?>(null)}

    val showAddCategoryDialog = remember { mutableStateOf(false) }
    val newCategoryName = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { expandedMenu.value = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = expandedMenu.value,
                            onDismissRequest = { expandedMenu.value = false },
                            modifier = Modifier.background(Peach)
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (showCompleted.value) "Hide Completed" else "Show Completed") },
                                onClick = { showCompleted.value = !showCompleted.value }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort") },
                                onClick = { /* TODO: Sort logic */ }
                            )
                            DropdownMenuItem(
                                text = { Text("Eisenhower Matrix") },
                                onClick = { /* TODO: Navigate to Eisenhower Matrix */ }
                            )
                            DropdownMenuItem(
                                text = { Text("Show Details")},
                                onClick = { /* TODO: Show/Hide task details */ }
                            )
                        }
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
                onClick = { showDialog.value = true },
                containerColor = Peach,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = DarkBlue)
            }
        },
        containerColor = DarkBlue
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            CategoryFilterRow(
                selectedCategory = viewModel.selectedCategory.collectAsState().value,
                categories = viewModel.customCategories.collectAsState().value,
                onCategoryChange = viewModel::setCategory,
                onAddCategoryClick = { showAddCategoryDialog.value = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TaskSection(
                title = "TO-DO",
                tasks = tasks.filter { !it.isDone },
                onCheck = viewModel::toggleTask,
                onEdit = { editingTask.value = it },
                onMove = { viewModel.updateTasksOrder(it) },
                maxHeight = if (showCompleted.value) 300.dp else 500.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showCompleted.value) {
                TaskSection(
                    title = "COMPLETED",
                    tasks = tasks.filter { it.isDone },
                    onCheck = viewModel::toggleTask,
                    onEdit = { editingTask.value = it },
                    onMove = { viewModel.updateTasksOrder(it) },
                    maxHeight = 300.dp
                )
            }
        }

        if (showDialog.value) {
            AddTaskDialog(
                categories = viewModel.customCategories.collectAsState().value,
                onDismiss = { showDialog.value = false },
                onAdd = {
                    viewModel.addTask(it)
                    showDialog.value = false
                }
            )
        }

        if (editingTask.value != null) {
            EditTaskDialog(
                task = editingTask.value!!,
                categories = viewModel.customCategories.collectAsState().value,
                onDismiss = { editingTask.value = null },
                onSave = {
                    viewModel.updateTask(it)
                    editingTask.value = null
                },
                onDelete = {
                    viewModel.deleteTask(it)
                    editingTask.value = null
                }
            )
        }

        if (showAddCategoryDialog.value) {
            AddCategoryDialog(
                value = newCategoryName.value,
                onValueChange = { newCategoryName.value = it },
                onDismiss = {
                    newCategoryName.value = ""
                    showAddCategoryDialog.value = false
                },
                onConfirm = {
                    if (newCategoryName.value.trim().isNotBlank()) {
                        viewModel.addCategory(newCategoryName.value.trim())
                        newCategoryName.value = ""
                        showAddCategoryDialog.value = false
                    }
                }
            )
        }
    }
}