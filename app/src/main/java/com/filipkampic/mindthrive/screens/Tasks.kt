package com.filipkampic.mindthrive.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    val allTasks by viewModel.allTasks.collectAsState()

    val customCategories = viewModel.customCategories.collectAsState().value
    val showAddCategoryDialog = remember { mutableStateOf(false) }
    val newCategoryName = remember { mutableStateOf("") }
    val categoryError = remember { mutableStateOf<String?>(null) }

    val categoryToDelete = remember { mutableStateOf<String?>(null) }
    val deleteMode = remember { mutableStateOf(DeleteMode.REASSIGN) }

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
                categories = customCategories,
                onCategoryChange = viewModel::setCategory,
                onAddCategoryClick = { showAddCategoryDialog.value = true },
                onDeleteCategoryClick = { category ->
                    val hasTasks = allTasks.any { it.category == category }
                    if (hasTasks) {
                        categoryToDelete.value = category
                    } else {
                        viewModel.deleteCategory(category)
                        if (viewModel.selectedCategory.value == category) {
                            viewModel.setCategory("All")
                        }
                    }
                }
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
                categories = customCategories,
                defaultCategory = viewModel.selectedCategory.collectAsState().value.let {
                    if (it == "All") "General" else it
                },
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
                categories = customCategories,
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
                onValueChange = {
                    newCategoryName.value = it
                    categoryError.value = null
                },
                onDismiss = {
                    newCategoryName.value = ""
                    categoryError.value = null
                    showAddCategoryDialog.value = false
                },
                onConfirm = {
                    val trimmed = newCategoryName.value.trim()
                    val existing = customCategories.map { it.lowercase() }

                    if (trimmed.isBlank()) {
                        categoryError.value = null
                        return@AddCategoryDialog
                    }

                    if (trimmed.lowercase() in existing) {
                        categoryError.value = "Category already exists."
                        return@AddCategoryDialog
                    }

                    viewModel.addCategory(trimmed)
                    newCategoryName.value = ""
                    categoryError.value = null
                    showAddCategoryDialog.value = false
                },
                errorMessage = categoryError.value
            )
        }

        if (categoryToDelete.value != null) {
            AlertDialog(
                onDismissRequest = {
                    categoryToDelete.value = null
                    deleteMode.value = DeleteMode.REASSIGN
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (deleteMode.value == DeleteMode.REASSIGN) {
                            viewModel.reassignTasksFromCategory(categoryToDelete.value!!, "General")
                        } else {
                            viewModel.deleteTasksInCategory(categoryToDelete.value!!)
                        }
                        viewModel.deleteCategory(categoryToDelete.value!!)

                        if (viewModel.selectedCategory.value == categoryToDelete.value) {
                            viewModel.setCategory("All")
                        }

                        categoryToDelete.value = null
                        deleteMode.value = DeleteMode.REASSIGN
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        categoryToDelete.value = null
                        deleteMode.value = DeleteMode.REASSIGN
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Delete category '${categoryToDelete.value}'?") },
                text = {
                    Column {
                        Text("Do you want to delete all tasks in this category or move them to 'General'?")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            RadioButton(
                                selected = deleteMode.value == DeleteMode.REASSIGN,
                                onClick = { deleteMode.value = DeleteMode.REASSIGN }
                            )
                            Text("Reassign to 'General'", modifier = Modifier.clickable { deleteMode.value = DeleteMode.REASSIGN })
                        }
                        Row {
                            RadioButton(
                                selected = deleteMode.value == DeleteMode.DELETE,
                                onClick = { deleteMode.value = DeleteMode.DELETE }
                            )
                            Text("Delete tasks", modifier = Modifier.clickable { deleteMode.value = DeleteMode.DELETE })
                        }
                    }
                },
                containerColor = Peach
            )
        }
    }
}

enum class DeleteMode {
    REASSIGN,
    DELETE
}
