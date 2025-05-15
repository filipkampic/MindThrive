package com.filipkampic.mindthrive.screens

import TimeBlockViewModel
import TimeBlockViewModelFactory
import android.annotation.SuppressLint
import android.app.Application
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.filipkampic.mindthrive.model.TimeBlock
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Preview(showBackground = true)
@Composable
fun TimeManagementPreview(modifier: Modifier = Modifier) {
    TimeManagement(rememberNavController(), "2025-04-27")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TimeManagement(navController: NavController, date: String) {
    val context = LocalContext.current
    val viewModel: TimeBlockViewModel = viewModel(factory = TimeBlockViewModelFactory(context.applicationContext as Application))
    val currentDate = LocalDate.parse(date)
    var showDialog by remember { mutableStateOf(false) }
    var defaultStartTime by remember { mutableStateOf(LocalTime.of(0, 0)) }
    var editingTimeBlock by remember { mutableStateOf<TimeBlock?>(null) }
    var showOverlapDialog by remember { mutableStateOf(false) }
    var selectedOverlappingTimeBlocks by remember { mutableStateOf<List<TimeBlock>>(emptyList()) }
    val timeBlocks by viewModel.timeBlocks.collectAsState()
    val todaysTimeBlocks by viewModel.todaysTimeBlocks.collectAsState()
    val localTimeBlocks = remember(currentDate, todaysTimeBlocks) {
        mutableStateListOf<TimeBlock>().apply { addAll(todaysTimeBlocks) }
    }

    val lazyListState = rememberLazyListState()
    val draggedItemIndex = remember { mutableStateOf<Int?>(null) }
    val dragOffset = remember { mutableStateOf(0f) }
    val timeBlockHeights = remember { mutableStateListOf<Float>().apply { addAll(List(todaysTimeBlocks.size) { 0f }) } }
    val hoveredIndex = remember { mutableStateOf<Int?>(null) }

    val currentTime by produceState(initialValue = LocalDateTime.now()) {
        while(true) {
            value = LocalDateTime.now()
            delay(1000)
        }
    }

    LaunchedEffect(timeBlocks, currentDate) {
        val newList = timeBlocks.filter { timeBlock ->
            val startDate = timeBlock.start?.toLocalDate() ?: timeBlock.date
            val endDate = timeBlock.end?.toLocalDate() ?: timeBlock.date
            (startDate == currentDate || endDate == currentDate) && timeBlock.start != null && timeBlock.end != null
        }.sortedBy { it.start?.toLocalTime() ?: LocalTime.of(0, 0) }

        localTimeBlocks.clear()
        localTimeBlocks.addAll(newList)
    }


    LaunchedEffect(currentDate) {
        viewModel.loadTimeBlocks(currentDate)
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
                            text = { Text("Duplicate Day") },
                            onClick = {
                                expanded.value = false
                                todaysTimeBlocks.forEach { timeBlock ->
                                    val newTimeBlock = timeBlock.copy(
                                        id = UUID.randomUUID().toString(),
                                        start = timeBlock.start?.plusDays(1),
                                        end = timeBlock.end?.plusDays(1),
                                        date = timeBlock.date.plusDays(1)
                                    )
                                    viewModel.insertTimeBlock(newTimeBlock)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear All Time Blocks") },
                            onClick = {
                                expanded.value = false
                                todaysTimeBlocks.forEach { viewModel.deleteTimeBlock(it) }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (todaysTimeBlocks.isEmpty()) {
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
                    val allTimes = (todaysTimeBlocks.flatMap { listOfNotNull(it.start?.toLocalTime(), it.end?.toLocalTime()) } + LocalTime.of(0, 0) + LocalTime.of(23, 59)).distinct().sorted()
                    val timeIntervals = allTimes.zipWithNext()

                    itemsIndexed(timeIntervals, key = { index, pair -> "$index-${pair.first}" }) { _, (start, end) ->
                        val blockStartDateTime = LocalDateTime.of(currentDate, start)
                        val blockEndDateTime = LocalDateTime.of(currentDate, end)

                        val timeBlocksInBlock = todaysTimeBlocks.filter { timeBlock ->
                            val timeBlockStart = timeBlock.start
                            val timeBlockEnd = timeBlock.end
                            timeBlockStart != null && timeBlockEnd != null &&
                                    timeBlockStart < blockEndDateTime && timeBlockEnd > blockStartDateTime
                        }
                        val isOverlap = timeBlocksInBlock.size > 1

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.width(60.dp)) {
                                Text(
                                    text = start.format(DateTimeFormatter.ofPattern("HH:mm")),
                                    color = Peach
                                )
                                if (timeIntervals.last().second == end) {
                                    Text(
                                        text = end.format(DateTimeFormatter.ofPattern("HH:mm")),
                                        color = Peach
                                    )
                                }
                            }
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (timeBlocksInBlock.isEmpty()) {
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
                                                editingTimeBlock = null
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
                                    OverlapWarningCard(timeBlocks = timeBlocksInBlock) {
                                        selectedOverlappingTimeBlocks = timeBlocksInBlock
                                        showOverlapDialog = true
                                    }
                                } else {
                                    timeBlocksInBlock.forEachIndexed { _, timeBlock ->
                                        val globalIndex = todaysTimeBlocks.indexOf(timeBlock)
                                        var isDragging by remember { mutableStateOf(false) }
                                        val density = LocalDensity.current
                                        val defaultCardHeightPx = with(density) { 48.dp.toPx() } + with(density) { 8.dp.toPx() }
                                        val isHovered = hoveredIndex.value == globalIndex && draggedItemIndex.value != globalIndex
                                        val isActive = timeBlock.start != null && timeBlock.end != null &&
                                                        currentTime.isAfter(timeBlock.start) && currentTime.isBefore(timeBlock.end)
                                        val remainingMinutes = timeBlock.end?.let { Duration.between(currentTime, it).plusSeconds(59).toMinutes().coerceAtLeast(0) } ?: 0

                                        TimeBlockCard(
                                            timeBlock = timeBlock,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .onGloballyPositioned {
                                                    if (globalIndex < timeBlockHeights.size) {
                                                        timeBlockHeights[globalIndex] = it.size.height.toFloat()
                                                    }
                                                }
                                                .offset {
                                                    if (isDragging && draggedItemIndex.value == globalIndex) {
                                                        IntOffset(0, dragOffset.value.toInt())
                                                    } else {
                                                        IntOffset(0, 0)
                                                    }
                                                }
                                                .pointerInput(timeBlock) {
                                                    detectDragGesturesAfterLongPress(
                                                        onDragStart = {
                                                            draggedItemIndex.value = globalIndex
                                                            dragOffset.value = 0f
                                                            isDragging = true
                                                        },
                                                        onDragEnd = {
                                                            if (draggedItemIndex.value != null && todaysTimeBlocks.isNotEmpty()) {
                                                                val draggedIndex = draggedItemIndex.value!!
                                                                val draggedTimeBlock = todaysTimeBlocks[draggedIndex]
                                                                localTimeBlocks.removeAt(draggedIndex)

                                                                val avgHeight = if (timeBlockHeights.isNotEmpty()) timeBlockHeights.average().toFloat() else defaultCardHeightPx
                                                                val positionOffset = dragOffset.value / avgHeight
                                                                val targetIndex = (draggedIndex + positionOffset).toInt().coerceIn(0, todaysTimeBlocks.size)

                                                                localTimeBlocks.add(targetIndex, draggedTimeBlock)

                                                                val reflowedTimeBlocks = reflowTimeBlocks(todaysTimeBlocks, currentDate)
                                                                reflowedTimeBlocks.forEach { viewModel.updateTimeBlock(it) }
                                                                localTimeBlocks.clear()
                                                                localTimeBlocks.addAll(reflowedTimeBlocks)
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
                                                            change.consumeAllChanges()
                                                            if (draggedItemIndex.value == globalIndex) {
                                                                dragOffset.value += dragAmount.y

                                                                val avgHeight = if (timeBlockHeights.isNotEmpty()) timeBlockHeights.average().toFloat() else defaultCardHeightPx
                                                                val halfHeight = avgHeight / 2
                                                                val adjustedOffset = dragOffset.value + halfHeight
                                                                val positionOffset = adjustedOffset / avgHeight
                                                                val targetIndex = (globalIndex + positionOffset).toInt().coerceIn(0, todaysTimeBlocks.size)

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
                                            isActive = isActive,
                                            remainingMinutes = remainingMinutes,
                                            onClick = {
                                                editingTimeBlock = timeBlock
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
            AddTimeBlockDialog(
                defaultStart = defaultStartTime,
                timeBlocks = todaysTimeBlocks,
                timeBlockToEdit = editingTimeBlock,
                onSave = { newTimeBlock ->
                    if (editingTimeBlock != null) {
                        viewModel.updateTimeBlock(newTimeBlock)
                    } else {
                        viewModel.insertTimeBlock(newTimeBlock)
                    }
                    showDialog = false
                    editingTimeBlock = null
                },
                onDelete = {
                    viewModel.deleteTimeBlock(it)
                    showDialog = false
                    editingTimeBlock = null
                },
                onCancel = {
                    showDialog = false
                    editingTimeBlock = null
                },
                currentDate = currentDate
            )
        }

        if (showOverlapDialog) {
            OverlapDialog(
                overlappingTimeBlocks = selectedOverlappingTimeBlocks,
                onDismiss = { showOverlapDialog = false },
                onTimeBlockClick = { timeBlock ->
                    editingTimeBlock = timeBlock
                    showDialog = true
                    showOverlapDialog = false
                }
            )
        }
    }
}

private fun reflowTimeBlocks(timeBlocks: List<TimeBlock>, baseDate: LocalDate): List<TimeBlock> {
    if (timeBlocks.isEmpty()) return timeBlocks

    val newTimeBlocks = mutableListOf<TimeBlock>()
    var currentTime = LocalDateTime.of(baseDate, LocalTime.of(0, 0))

    timeBlocks.forEach { timeBlock ->
        if (timeBlock.start == null || timeBlock.end == null) return@forEach

        val originalDuration = Duration.between(timeBlock.start, timeBlock.end)

        val newTimeBlock = timeBlock.copy(
            start = currentTime,
            end = currentTime.plus(originalDuration),
            date = currentTime.toLocalDate()
        )

        newTimeBlocks.add(newTimeBlock)
        currentTime = newTimeBlock.end
    }

    return newTimeBlocks
}

@Composable
fun TimeBlockCard(
    timeBlock: TimeBlock,
    modifier: Modifier = Modifier,
    isOverlapping: Boolean = false,
    isDragging: Boolean = false,
    isHovered: Boolean = false,
    isActive: Boolean = false,
    remainingMinutes: Long = 0,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    isActive -> Color(0xFFFF5C5C).copy(alpha = 0.95f)
                    isDragging -> Color.Gray.copy(alpha = 0.2f)
                    isHovered -> Peach.copy(alpha = 0.8f)
                    isOverlapping -> Peach.copy(alpha = 0.6f)
                    else -> Peach
                }
            )
            .border(
                width = if (isActive) 2.dp else 0.dp,
                color = if (isActive) Color.Red else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 32.dp)
        ) {
            Text(
                text = timeBlock.name,
                fontWeight = FontWeight.Bold,
                color = if (isActive) Color.White else DarkBlue
            )
            if (timeBlock.description.isNotBlank()) {
                Text(
                    text = timeBlock.description,
                    color = if (isActive) Color.White else DarkBlue,
                    fontSize = 14.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeBlock.duration(),
                    color = if (isActive) Color.White else DarkBlue,
                    fontSize = 14.sp
                )
                if (isActive) {
                    Spacer(modifier = Modifier.width(8.dp))

                    val remHours = remainingMinutes / 60
                    val remMins = remainingMinutes % 60

                    Text(
                        text = "Remaining: " +
                                when {
                                    remHours > 0 -> "${remHours}h ${remMins}min"
                                    else -> "${remMins}min"
                                },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
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
fun AddTimeBlockDialog(
    defaultStart: LocalTime,
    timeBlocks: List<TimeBlock>,
    currentDate: LocalDate,
    timeBlockToEdit: TimeBlock? = null,
    onSave: (TimeBlock) -> Unit,
    onDelete: (TimeBlock) -> Unit = {},
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(timeBlockToEdit?.name ?: "") }
    var start by remember { mutableStateOf(timeBlockToEdit?.start?.toLocalTime() ?: defaultStart) }
    val nextTimeBlockStart = timeBlocks
        .mapNotNull { it.start?.toLocalTime() }
        .filter { it > defaultStart }
        .minOrNull()
    var end by remember { mutableStateOf(timeBlockToEdit?.end?.toLocalTime() ?: (nextTimeBlockStart ?: LocalTime.of(0, 0))) }
    var description by remember { mutableStateOf(timeBlockToEdit?.description ?: "") }
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
            showTimePicker(false, end?.hour ?: 0, end?.minute ?: 0)
        }
    }

    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            IconButton(onClick = {
                if (name.isNotBlank()) {
                    val startDateTime = LocalDateTime.of(currentDate, start)
                    val endDateTime = LocalDateTime.of(currentDate, end)
                    val adjustedEndDateTime = if (end < start) {
                        endDateTime.plusDays(1)
                    } else {
                        endDateTime
                    }
                    val newTimeBlock = TimeBlock(
                        id = timeBlockToEdit?.id ?: UUID.randomUUID().toString(),
                        name = name,
                        start = startDateTime,
                        end = adjustedEndDateTime,
                        description = description,
                        date = currentDate
                    )
                    onSave(newTimeBlock)
                }
            }) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        },
        dismissButton = {
            Row {
                if (timeBlockToEdit != null) {
                    IconButton(onClick = { onDelete(timeBlockToEdit) }) {
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
                if (timeBlockToEdit != null) "Edit Time Block" else "New Time Block",
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
                                text = start.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "N/A",
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
                                text = end?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "N/A",
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
fun OverlapWarningCard(timeBlocks: List<TimeBlock>, onClick: () -> Unit) {
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
                text = "${timeBlocks.size} overlapping blocks",
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OverlapDialog(
    overlappingTimeBlocks: List<TimeBlock>,
    onDismiss: () -> Unit,
    onTimeBlockClick: (TimeBlock) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Overlapping TimeBlocks", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                overlappingTimeBlocks.forEach { timeBlock ->
                    TimeBlockCard(
                        timeBlock = timeBlock,
                        onClick = { onTimeBlockClick(timeBlock) }
                    )
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimeManagementWrapper(navController: NavController, initialDate: String) {
    var currentDate by rememberSaveable {
        mutableStateOf(LocalDate.parse(initialDate))
    }

    val swipeModifier = Modifier.pointerInput(currentDate) {
        detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount < -50) {
                currentDate = currentDate.plusDays(1)
            } else if (dragAmount > 50) {
                currentDate = currentDate.minusDays(1)
            }
        }
    }

    Box(
        modifier = swipeModifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        AnimatedContent(
            targetState = currentDate,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() with
                            slideOutHorizontally { width -> width } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            modifier = Modifier.fillMaxSize(),
            label = "dateTransition"
        ) { targetDate ->
                val context = LocalContext.current
                val viewModel: TimeBlockViewModel = viewModel(factory = TimeBlockViewModelFactory(context.applicationContext as Application))
                LaunchedEffect(currentDate) {
                    viewModel.setDate(currentDate)
                }

                TimeManagement(
                    navController = navController,
                    date = targetDate.toString()
                )
        }
    }
}
