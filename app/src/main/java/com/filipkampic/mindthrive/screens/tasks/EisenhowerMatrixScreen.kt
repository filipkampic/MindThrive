package com.filipkampic.mindthrive.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipkampic.mindthrive.R
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.tasks.TaskRepository
import com.filipkampic.mindthrive.model.tasks.Priority
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.ui.tasks.EditTaskDialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.TaskListViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
@Preview(showBackground = false)
fun EisenhowerMatrixPreview() {
    EisenhowerMatrix(
        tasks = listOf(Task(title = "Task 1", priority = Priority.HIGH, dueDate = LocalDate.now().plusDays(3))),
        onCheck = {},
        navController = NavController(LocalContext.current)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EisenhowerMatrix(
    tasks: List<Task>,
    onCheck: (Task) -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repo = TaskRepository(context, db.taskDao(), db.categoryDao())
    val viewModel = remember { TaskListViewModel(repo) }

    val showCompleted by viewModel.showCompleted.collectAsState()

    val doFirst = tasks.filter { it.getEisenhowerQuadrant() == EisenhowerQuadrant.DO_FIRST && (showCompleted || !it.isDone) }
    val schedule = tasks.filter { it.getEisenhowerQuadrant() == EisenhowerQuadrant.SCHEDULE && (showCompleted || !it.isDone)}
    val delegate = tasks.filter { it.getEisenhowerQuadrant() == EisenhowerQuadrant.DELEGATE && (showCompleted || !it.isDone)}
    val eliminate = tasks.filter { it.getEisenhowerQuadrant() == EisenhowerQuadrant.ELIMINATE && (showCompleted || !it.isDone)}

    val editingTask = remember { mutableStateOf<Task?>(null) }
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eisenhower Matrix", fontFamily = Montserrat) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Completed", color = Peach, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.scale(0.8f)) {
                            Switch(
                                checked = showCompleted,
                                onCheckedChange = { viewModel.setShowCompleted(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Peach,
                                    checkedTrackColor = DarkBlue,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = DarkBlue.copy(alpha = 0.4f),
                                    checkedBorderColor = Peach,
                                    uncheckedBorderColor = Peach
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = Peach,
                    navigationIconContentColor = Peach
                ),
            )
        },
        containerColor = DarkBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EisenhowerQuadrantBox("Urgent & Important", doFirst, onCheck, { editingTask.value = it }, color = Color.Red, modifier = Modifier.weight(1f), iconRes = R.drawable.number_one)
                EisenhowerQuadrantBox("Not urgent & Important", schedule, onCheck, { editingTask.value = it }, color = Color(0xFFFFA500), modifier = Modifier.weight(1f), iconRes = R.drawable.number_two)
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EisenhowerQuadrantBox("Urgent & Unimportant", delegate, onCheck, { editingTask.value = it }, color = Color(0xFF3F51B5), modifier = Modifier.weight(1f), iconRes = R.drawable.number_three)
                EisenhowerQuadrantBox("Not Urgent & Unimportant", eliminate, onCheck, { editingTask.value = it }, color = Color(0xFF4CAF50), modifier = Modifier.weight(1f), iconRes = R.drawable.number_four)
            }
        }
        if (editingTask.value != null) {
            EditTaskDialog(
                task = editingTask.value!!,
                categories = categories.map { it.name },
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
    }
}

fun Task.getEisenhowerQuadrant(): EisenhowerQuadrant {
    val today = LocalDate.now()
    val daysUntilDue = dueDate?.let { ChronoUnit.DAYS.between(today, it) } ?: Long.MAX_VALUE

    return when {
        priority == Priority.HIGH && daysUntilDue <= 5 -> EisenhowerQuadrant.DO_FIRST
        priority == Priority.HIGH && daysUntilDue > 5 -> EisenhowerQuadrant.SCHEDULE
        priority == Priority.MEDIUM && daysUntilDue > 5 -> EisenhowerQuadrant.SCHEDULE
        priority == Priority.MEDIUM && daysUntilDue <= 5 -> EisenhowerQuadrant.DELEGATE
        priority == Priority.LOW && daysUntilDue <= 5 -> EisenhowerQuadrant.DELEGATE
        else -> EisenhowerQuadrant.ELIMINATE
    }
}

enum class EisenhowerQuadrant {
    DO_FIRST,
    SCHEDULE,
    DELEGATE,
    ELIMINATE
}
