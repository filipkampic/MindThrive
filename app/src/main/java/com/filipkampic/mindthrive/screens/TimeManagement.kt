package com.filipkampic.mindthrive.screens

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.filipkampic.mindthrive.Task
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Preview(showBackground = true)
@Composable
fun TimeManagementPreview(modifier: Modifier = Modifier) {
    TimeManagement(rememberNavController(), "")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeManagement(navController: NavController, date: String) {
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var showDialog by remember { mutableStateOf(false) }
    var defaultStartTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var editingTask by remember { mutableStateOf<Task?>(null)}
    val currentDate = LocalDate.parse(date)
    val todaysTasks = tasks.flatMap { task ->
        if (task.start <= task.end && task.date == currentDate) {
            listOf(task)
        } else if (task.start > task.end) {
            val isToday = task.date == currentDate
            val isTomorrow = task.date.plusDays(1) == currentDate

            listOfNotNull(
                if (isToday) Task(task.name, task.start, LocalTime.of(23, 59), task.description, task.date) else null,
                if (isTomorrow) Task(task.name, LocalTime.MIDNIGHT, task.end.takeIf { it != LocalTime.MIDNIGHT } ?: LocalTime.of(23, 59), task.description, currentDate) else null
            )
        } else {
            emptyList()
        }
    }
    var showOverlapDialog by remember { mutableStateOf(false) }
    var selectedOverlappingTasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.offset(y = 16.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Peach,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = date,
                    color = Peach,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(
                    onClick = {
                        defaultStartTime = tasks.maxByOrNull { it.end }?.end ?: LocalTime.of(0, 0)
                        showDialog = true
                    },
                    modifier = Modifier.offset(y = 16.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Peach,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (todaysTasks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "OO:OO",
                            color = Peach,
                            fontSize = 14.sp,
                            modifier = Modifier.width(60.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Peach)
                                .padding(start = 16.dp)
                                .clickable { showDialog = true },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                "24 h",
                                color = DarkBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "OO:OO",
                            color = Peach,
                            fontSize = 14.sp,
                            modifier = Modifier.width(60.dp)
                        )
                    }
                }
            } else
                LazyColumn(modifier = Modifier.weight(1f)) {
                    val allTimes = (todaysTasks.flatMap { listOf(it.start, it.end) } + LocalTime.of(0, 0) + LocalTime.of(23, 59)).distinct().sorted()
                    val timeBlocks = allTimes.zipWithNext()

                    items(timeBlocks) { (start, end) ->
                        val tasksInBlock = todaysTasks.filter {
                            it.start < end && it.end > start
                        }
                        val isOverlap = tasksInBlock.size > 1

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.width(60.dp)) {
                                Text(
                                    text = start.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    color = Peach
                                )
                                if (timeBlocks.last().second == end) {
                                    Text(
                                        text = end.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        color = Peach
                                    )
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (tasksInBlock.isEmpty()) {
                                    val duration = Duration.between(start, end)
                                    val hours = duration.toHours()
                                    val minutes = duration.toMinutes() % 60

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .border(1.dp, Peach, RoundedCornerShape(12.dp))
                                            .background(DarkBlue)
                                            .padding(start = 16.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = if (minutes == 0L) "${hours}h" else "${hours}h ${minutes}min",
                                            color = Peach,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else if (isOverlap) {
                                    OverlapWarningCard(tasks = tasksInBlock) {
                                        selectedOverlappingTasks = tasksInBlock
                                        showOverlapDialog = true
                                    }
                                } else {
                                    tasksInBlock.forEach { task ->
                                        TaskCard(task, onClick = {
                                            editingTask = task
                                            showDialog = true
                                        })
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (showDialog) {
                AddTaskDialog(
                    defaultStart = defaultStartTime,
                    tasks = todaysTasks,
                    taskToEdit = editingTask,
                    onSave = { newTask ->
                        tasks = if (editingTask != null) tasks - editingTask!! + newTask else tasks + newTask
                        showDialog = false
                        editingTask = null
                    },
                    onDelete = {
                        tasks = tasks - it
                        showDialog = false
                        editingTask = null
                    },
                    onCancel = {
                        showDialog = false
                        editingTask = null
                    },
                    currentDate = currentDate
                )
            }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomEnd)
                .size(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Peach)
                .clickable {
                    defaultStartTime = tasks.maxByOrNull { it.end }?.end ?: LocalTime.of(0, 0)
                    showDialog = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }
        if (showOverlapDialog) {
            OverlapDialog(
                overlappingTasks = selectedOverlappingTasks,
                onDismiss = { showOverlapDialog = false }
            )
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    isOverlapping: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isOverlapping) Peach.copy(alpha = 0.6f) else Peach,
                RoundedCornerShape(12.dp)
            )
            .clickable{ onClick() }
            .padding(12.dp)
            .padding(end = 32.dp)
    ) {
        Text(task.name, fontWeight = FontWeight.Bold, color = DarkBlue)
        if (task.description.isNotBlank()) {
            Text(task.description, color = DarkBlue)
        }
        Text(task.duration(), color = DarkBlue)
    }
}

@Composable
fun AddTaskDialog(
    defaultStart: LocalTime,
    tasks: List<Task>,
    currentDate: LocalDate,
    taskToEdit: Task? = null,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit = {},
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(taskToEdit?.name ?: "") }
    var start by remember { mutableStateOf(taskToEdit?.start ?: defaultStart) }
    val nextTaskStart = taskToEdit?.end ?: tasks
        .map { it.start }
        .filter { it > defaultStart }
        .minOrNull()
    var end by remember { mutableStateOf(nextTaskStart ?: LocalTime.of(0, 0)) }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            IconButton(onClick = {
                if (name.isNotBlank()) {
                    onSave(Task(name, start, end, description, currentDate))
                }
            }) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        },
        dismissButton = {
            Row {
                if (taskToEdit != null) {
                    IconButton(onClick = { onDelete(taskToEdit) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                }
            }
        },
        title = {
            Text(
                if (taskToEdit != null) "Edit Task" else "New Task",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = start.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onValueChange = {},
                        label = { Text("Start") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute -> start = LocalTime.of(hour, minute) },
                                    start.hour,
                                    start.minute,
                                    true
                                ).show()
                            }
                    )
                    OutlinedTextField(
                        value = end.format(DateTimeFormatter.ofPattern("HH:mm")),
                        onValueChange = {},
                        label = { Text("End") },
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute -> end = LocalTime.of(hour, minute) },
                                    end.hour,
                                    end.minute,
                                    true
                                ).show()
                            }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Peach
    )
}

@Composable
fun OverlapWarningCard(tasks: List<Task>, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .border(2.dp, Color.Red, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Overlap",
                tint = Color.Red,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${tasks.size} overlapping blocks",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OverlapDialog(overlappingTasks: List<Task>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Overlapping Tasks", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                overlappingTasks.forEach { task ->
                    TaskCard(task = task)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        containerColor = Peach.copy(alpha = 0.95f)
    )
}
