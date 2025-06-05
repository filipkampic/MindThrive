package com.filipkampic.mindthrive.screens.tasks

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.TaskRepository
import com.filipkampic.mindthrive.model.tasks.SortDirection
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.model.tasks.TaskSortOption
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

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tasks(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel = remember {
        val db = AppDatabase.getDatabase(context)
        val repo = TaskRepository(context, db.taskDao(), db.categoryDao())
        TaskListViewModel(repo)
    }

    val showDialog = remember { mutableStateOf(false) }
    val showCompleted by viewModel.showCompleted.collectAsState()
    val expandedMenu = remember { mutableStateOf(false) }
    val showSortMenu = remember { mutableStateOf(false) }
    var sortMenuRefreshKey by remember { mutableIntStateOf(0) }
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
                                text = { Text(if (showCompleted) "Hide Completed" else "Show Completed", color = DarkBlue) },
                                onClick = { viewModel.setShowCompleted(!showCompleted) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort", color = DarkBlue) },
                                onClick = { showSortMenu.value = true }
                            )
                            DropdownMenuItem(
                                text = { Text("Eisenhower Matrix", color = DarkBlue) },
                                onClick = { navController.navigate("eisenhower") }
                            )
                        }

                        key(sortMenuRefreshKey) {
                            DropdownMenu(
                                expanded = showSortMenu.value,
                                onDismissRequest = {
                                    showSortMenu.value = false
                                    expandedMenu.value = false
                                },
                                modifier = Modifier.background(Peach)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("By title", color = DarkBlue)
                                            if (viewModel.sortOption.value == TaskSortOption.TITLE) {
                                                Icon(
                                                    imageVector = if (viewModel.sortDirection.value == SortDirection.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                                    contentDescription = "Direction",
                                                    tint = DarkBlue,
                                                    modifier = Modifier.padding(start = 4.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (viewModel.sortOption.value == TaskSortOption.TITLE) {
                                            viewModel.toggleSortDirection()
                                        } else {
                                            viewModel.setSortOption(TaskSortOption.TITLE)
                                            viewModel.setSortDirection(SortDirection.ASCENDING)
                                        }
                                            sortMenuRefreshKey++
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("By due date", color = DarkBlue)
                                            if (viewModel.sortOption.value == TaskSortOption.DUE_DATE) {
                                                Icon(
                                                    imageVector = if (viewModel.sortDirection.value == SortDirection.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                                    contentDescription = "Direction",
                                                    tint = DarkBlue,
                                                    modifier = Modifier.padding(start = 4.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (viewModel.sortOption.value == TaskSortOption.DUE_DATE) {
                                            viewModel.toggleSortDirection()
                                        } else {
                                            viewModel.setSortOption(TaskSortOption.DUE_DATE)
                                            viewModel.setSortDirection(SortDirection.ASCENDING)
                                        }
                                            sortMenuRefreshKey++
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("By priority", color = DarkBlue)
                                            if (viewModel.sortOption.value == TaskSortOption.PRIORITY) {
                                                Icon(
                                                    imageVector = if (viewModel.sortDirection.value == SortDirection.ASCENDING) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                                    contentDescription = "Direction",
                                                    tint = DarkBlue,
                                                    modifier = Modifier.padding(start = 4.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (viewModel.sortOption.value == TaskSortOption.PRIORITY) {
                                            viewModel.toggleSortDirection()
                                        } else {
                                            viewModel.setSortOption(TaskSortOption.PRIORITY)
                                            viewModel.setSortDirection(SortDirection.ASCENDING)
                                        }
                                            sortMenuRefreshKey++
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Default order", color = DarkBlue) },
                                    onClick = {
                                        viewModel.setSortOption(TaskSortOption.DEFAULT)
                                        sortMenuRefreshKey++
                                    }
                                )
                            }
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
                maxHeight = if (showCompleted) 300.dp else 500.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showCompleted) {
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
                        Text("Delete", color = Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        categoryToDelete.value = null
                        deleteMode.value = DeleteMode.REASSIGN
                    }) {
                        Text("Cancel", color = DarkBlue)
                    }
                },
                title = {
                    Text("Delete category '${categoryToDelete.value}'?", color = DarkBlue)
                },
                text = {
                    Column {
                        Text(
                            "Do you want to delete all tasks in this category or move them to 'General'?",
                            color = DarkBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { deleteMode.value = DeleteMode.REASSIGN }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = deleteMode.value == DeleteMode.REASSIGN,
                                onClick = { deleteMode.value = DeleteMode.REASSIGN },
                                colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reassign to 'General'", color = DarkBlue)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { deleteMode.value = DeleteMode.DELETE }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = deleteMode.value == DeleteMode.DELETE,
                                onClick = { deleteMode.value = DeleteMode.DELETE },
                                colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete tasks", color = DarkBlue)
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
