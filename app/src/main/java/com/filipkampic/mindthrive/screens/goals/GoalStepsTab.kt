package com.filipkampic.mindthrive.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.GoalStep
import com.filipkampic.mindthrive.ui.goals.AddStepDialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.GoalsViewModel
import com.filipkampic.mindthrive.viewmodel.GoalsViewModelFactory
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun GoalStepsTab(goalId: Int) {
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao(), db.goalStepDao(), db.goalNoteDao(), db.goalCategoryDao())
    }
    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModelFactory(repository)
    )

    val stepsStateList = remember { mutableStateListOf<GoalStep>() }
    val hapticFeedback = LocalHapticFeedback.current

    val steps by viewModel.getSteps(goalId).collectAsState(initial = null)

    var showStepDialog by remember { mutableStateOf(false) }
    var selectedStepForEditing by remember { mutableStateOf<GoalStep?>(null) }

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            val movedItem = stepsStateList.removeAt(from.index)
            stepsStateList.add(to.index, movedItem)
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)

            val updatedOrder = stepsStateList.mapIndexed { index, step ->
                step.copy(order = index)
            }
            viewModel.updateStepsOrder(updatedOrder)
        }
    )

    LaunchedEffect(steps) {
        if (steps != null) {
            stepsStateList.clear()
            stepsStateList.addAll(steps!!.sortedBy { it.order })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(horizontal = 16.dp)
    ) {
        when {
            steps == null -> {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }

            steps!!.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No steps yet", color = Peach.copy(alpha = 0.7f))
                }
            }
            else -> {
                LazyColumn(
                    state = reorderState.listState,
                    modifier = Modifier
                        .weight(1f)
                        .reorderable(reorderState)
                ) {
                    itemsIndexed(
                        items = stepsStateList,
                        key = { _, step -> step.id }
                    ) { index, step ->
                        val textColor = if (step.isCompleted) Peach.copy(alpha = 0.6f) else Peach
                        val descriptionColor =
                            if (step.isCompleted) Peach.copy(alpha = 0.4f) else Peach.copy(alpha = 0.7f)
                        val textDecoration =
                            if (step.isCompleted) TextDecoration.LineThrough else null

                        ReorderableItem(
                            reorderableState = reorderState,
                            key = step.id
                        ) { isDragging ->
                            val elevation = if (isDragging) 8.dp else 0.dp

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation)
                                    .clickable {
                                        selectedStepForEditing = step
                                        showStepDialog = true
                                    }
                                    .padding(horizontal = 12.dp)
                                    .detectReorderAfterLongPress(reorderState)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        color = textColor,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            step.name,
                                            color = textColor,
                                            textDecoration = textDecoration
                                        )
                                        step.description?.let { text ->
                                            Text(
                                                text = text,
                                                color = descriptionColor,
                                                textDecoration = textDecoration
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Checkbox(
                                        checked = step.isCompleted,
                                        onCheckedChange = { isCheckedValue ->
                                            viewModel.updateGoalStepCompletion(
                                                step,
                                                isCheckedValue
                                            )
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Peach,
                                            uncheckedColor = Peach.copy(alpha = 0.7f),
                                            checkmarkColor = DarkBlue
                                        )
                                    )
                                }

                                if (index < stepsStateList.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(top = 8.dp),
                                        thickness = 1.dp,
                                        color = Peach.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                selectedStepForEditing = null
                showStepDialog = true
            },
            containerColor = Peach,
            contentColor = DarkBlue,
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Step")
        }

        if (showStepDialog) {
            AddStepDialog(
                existingStep = selectedStepForEditing,
                goalId = goalId,
                onDismissRequest = {
                    showStepDialog = false
                    selectedStepForEditing = null
                },
                onConfirm = { id, name, description ->
                    if (id != null && selectedStepForEditing != null) {
                        viewModel.updateStep(
                            selectedStepForEditing!!.copy(
                                name = name,
                                description = description
                            )
                        )
                    } else {
                        viewModel.addStep(
                            goalId = goalId,
                            name = name,
                            description = description
                        )
                    }
                    showStepDialog = false
                    selectedStepForEditing = null
                },
                onDelete = { viewModel.deleteStep(it) }
            )
        }
    }
}
