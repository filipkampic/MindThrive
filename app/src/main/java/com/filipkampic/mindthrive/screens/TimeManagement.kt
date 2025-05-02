package com.filipkampic.mindthrive.screens

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
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
    TimeManagement(rememberNavController(), "2025-04-27")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeManagement(navController: NavController, date: String) {
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var showDialog by remember { mutableStateOf(false) }
    var defaultStartTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    val currentDate = LocalDate.parse(date)
    val todaysTasks = tasks.filter { task ->
        if (task.start <= task.end) {
            task.date == currentDate
        } else {
            task.date == currentDate || task.date.plusDays(1) == currentDate
        }
    }.sortedBy { it.start }
    var showOverlapDialog by remember { mutableStateOf(false) }
    var selectedOverlappingTasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    val taskListState = remember { mutableStateListOf<Task>().apply { addAll(todaysTasks) } }
    val lazyListState = rememberLazyListState()
    val draggedItemIndex = remember { mutableStateOf<Int?>(null) }
    val dragOffset = remember { mutableStateOf(0f) }
    val taskHeights = remember { mutableStateListOf<Float>().apply { addAll(List(taskListState.size) { 0f }) } }
    val hoveredIndex = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(todaysTasks) {
        taskListState.clear()
        taskListState.addAll(todaysTasks)
        taskHeights.clear()
        taskHeights.addAll(List(taskListState.size) { 0f })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
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
                Box {
                    val expanded = remember { mutableStateOf(false) }
                    IconButton(
                        onClick = { expanded.value = true },
                        modifier = Modifier.offset(y = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Peach,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.background(Peach)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Option 1") },
                            onClick = {
                                expanded.value = false
                                // TODO: Dodati logiku za Option 1
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Option 2") },
                            onClick = {
                                expanded.value = false
                                // TODO: Dodati logiku za Option 2
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Option 3") },
                            onClick = {
                                expanded.value = false
                                // TODO: Dodati logiku za Option 3
                            }
                        )
                    }
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
                            "00:00",
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
                            "00:00",
                            color = Peach,
                            fontSize = 14.sp,
                            modifier = Modifier.width(60.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    state = lazyListState
                ) {
                    val allTimes = (taskListState.flatMap { listOf(it.start, it.end) } + LocalTime.of(0, 0) + LocalTime.of(23, 59)).distinct().sorted()
                    val timeBlocks = allTimes.zipWithNext()

                    itemsIndexed(timeBlocks, key = { index, (start, _) -> "$index-$start" }) { _, (start, end) ->
                        val tasksInBlock = taskListState.filter {
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
                                            .padding(start = 16.dp)
                                            .clickable {
                                                defaultStartTime = start
                                                editingTask = null
                                                showDialog = true
                                            },
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = when {
                                                hours == 0L -> "${minutes}min"
                                                minutes == 0L -> "${hours}h"
                                                else -> "${hours}h ${minutes}min"
                                            },
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
                                    tasksInBlock.forEachIndexed { _, task ->
                                        val globalIndex = taskListState.indexOf(task)
                                        var isDragging by remember { mutableStateOf(false) }
                                        val density = LocalDensity.current
                                        val defaultCardHeightPx = with(density) { 48.dp.toPx() } + with(density) { 8.dp.toPx() }
                                        val isHovered = hoveredIndex.value == globalIndex && draggedItemIndex.value != globalIndex

                                        TaskCard(
                                            task = task,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .onGloballyPositioned {
                                                    if (globalIndex < taskHeights.size) {
                                                        taskHeights[globalIndex] = it.size.height.toFloat()
                                                    }
                                                }
                                                .offset {
                                                    if (isDragging && draggedItemIndex.value == globalIndex) {
                                                        IntOffset(0, dragOffset.value.toInt())
                                                    } else {
                                                        IntOffset(0, 0)
                                                    }
                                                }
                                                .pointerInput(task) {
                                                    detectDragGesturesAfterLongPress(
                                                        onDragStart = {
                                                            draggedItemIndex.value = globalIndex
                                                            dragOffset.value = 0f
                                                            isDragging = true
                                                        },
                                                        onDragEnd = {
                                                            if (draggedItemIndex.value != null) {
                                                                val draggedIndex = draggedItemIndex.value!!
                                                                val draggedTask = taskListState[draggedIndex]
                                                                taskListState.removeAt(draggedIndex)

                                                                val avgHeight = if (taskHeights.isNotEmpty()) taskHeights.average().toFloat() else defaultCardHeightPx
                                                                val positionOffset = dragOffset.value / avgHeight
                                                                val targetIndex = (draggedIndex + positionOffset).toInt().coerceIn(0, taskListState.size)

                                                                taskListState.add(targetIndex, draggedTask)

                                                                val reflowedTasks = reflowTasks(taskListState)
                                                                tasks = reflowedTasks
                                                                taskListState.clear()
                                                                taskListState.addAll(reflowedTasks)
                                                            }
                                                            draggedItemIndex.value = null
                                                            dragOffset.value = 0f
                                                            hoveredIndex.value = null
                                                            isDragging = false
                                                        },
                                                        onDragCancel = {
                                                            draggedItemIndex.value = null
                                                            dragOffset.value = 0f
                                                            hoveredIndex.value = null
                                                            isDragging = false
                                                        },
                                                        onDrag = { change, dragAmount ->
                                                            change.consume()
                                                            if (draggedItemIndex.value == globalIndex) {
                                                                dragOffset.value += dragAmount.y

                                                                val avgHeight = if (taskHeights.isNotEmpty()) taskHeights.average().toFloat() else defaultCardHeightPx
                                                                val halfHeight = avgHeight / 2
                                                                val adjustedOffset = dragOffset.value + halfHeight
                                                                val positionOffset = adjustedOffset / avgHeight
                                                                val targetIndex = (globalIndex + positionOffset).toInt().coerceIn(0, taskListState.size)

                                                                if (targetIndex != globalIndex) {
                                                                    hoveredIndex.value = if (targetIndex > globalIndex) targetIndex - 1 else targetIndex
                                                                } else {
                                                                    hoveredIndex.value = null
                                                                }
                                                            }
                                                        }
                                                    )
                                                },
                                            isDragging = isDragging,
                                            isHovered = isHovered,
                                            onClick = {
                                                editingTask = task
                                                showDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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

        if (showOverlapDialog) {
            OverlapDialog(
                overlappingTasks = selectedOverlappingTasks,
                onDismiss = { showOverlapDialog = false }
            )
        }
    }
}

private fun reflowTasks(tasks: List<Task>): List<Task> {
    if (tasks.isEmpty()) return tasks
    val newTasks = mutableListOf<Task>()
    var currentTime = LocalTime.of(0, 0)

    tasks.forEach { task ->
        val duration = Duration.between(task.start, task.end)
        val newStartTime = currentTime
        var newEndTime = newStartTime.plus(duration)

        if (newEndTime > LocalTime.of(23, 59)) {
            newEndTime = LocalTime.of(23, 59)
        }

        val newTask = task.copy(
            start = newStartTime,
            end = newEndTime
        )
        newTasks.add(newTask)
        currentTime = newEndTime
    }
    return newTasks
}

@Composable
fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
    isOverlapping: Boolean = false,
    isDragging: Boolean = false,
    isHovered: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                when {
                    isDragging -> Color.Gray.copy(alpha = 0.2f)
                    isHovered -> Peach.copy(alpha = 0.8f)
                    isOverlapping -> Peach.copy(alpha = 0.6f)
                    else -> Peach
                },
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 32.dp)
        ) {
            Text(
                text = task.name,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    color = DarkBlue,
                    fontSize = 14.sp
                )
            }
            Text(
                text = task.duration(),
                color = DarkBlue,
                fontSize = 14.sp
            )
        }

        if (isDragging) {
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Drag",
                tint = Peach,
                modifier = Modifier.size(24.dp)
            )
        }
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
    var openEndPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val openStartPicker = remember { mutableStateOf(false) }

    val customTheme = remember { context.resources.getIdentifier("CustomTimePickerTheme", "style", context.packageName) }

    fun showTimePicker(isStart: Boolean, initialHour: Int, initialMinute: Int) {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            if (isStart) {
                start = LocalTime.of(hour, minute)
                openStartPicker.value = false
            } else {
                end = LocalTime.of(hour, minute)
                openEndPicker = false
            }
        }
        val dialog = TimePickerDialog(context, customTheme, listener, initialHour, initialMinute, true).apply {
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            setOnCancelListener {
                if (isStart) {
                    openStartPicker.value = false
                } else {
                    openEndPicker = false
                }
            }
            setOnDismissListener {
                if (isStart) {
                    openStartPicker.value = false
                } else {
                    openEndPicker = false
                }
            }
        }
        dialog.show()
    }

    LaunchedEffect(openStartPicker.value) {
        if (openStartPicker.value) {
            showTimePicker(true, start.hour, start.minute)
        }
    }

    LaunchedEffect(openEndPicker) {
        if (openEndPicker) {
            showTimePicker(false, end.hour, end.minute)
        }
    }

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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .clickable { openStartPicker.value = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Start",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = start.format(DateTimeFormatter.ofPattern("HH:mm")),
                                fontSize = 16.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .clickable { openEndPicker = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = "End",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = end.format(DateTimeFormatter.ofPattern("HH:mm")),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
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