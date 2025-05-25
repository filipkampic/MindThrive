package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.Task
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import org.burnoutcrew.reorderable.*

@Composable
@Preview(showBackground = false)
fun TaskSectionPreview(modifier: Modifier = Modifier) {
    TaskSection(
        title = "Tasks",
        tasks = listOf(
            Task(title = "Task 1"),
            Task(title = "Task 2"),
            Task(title = "Task 3")
        ),
        onCheck = {},
        onEdit = {},
        onMove = {},
        maxHeight = 300.dp
    )
}

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    onCheck: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onMove: (List<Task>) -> Unit,
    maxHeight: Dp
) {
    val taskList = remember { mutableStateListOf<Task>() }
    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        if (from.index in taskList.indices && to.index in 0..taskList.size) {
            val item = taskList.removeAt(from.index)
            taskList.add(to.index, item)
            onMove(taskList.mapIndexed { index, task -> task.copy(position = index) })
        }
    })

    val listState = reorderState.listState

    val showTopFade by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }
    val showBottomFade by remember {
        derivedStateOf {
            val visible = listState.layoutInfo.visibleItemsInfo
            if (visible.isEmpty()) false
            else visible.last().index < listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(tasks) {
        taskList.clear()
        taskList.addAll(tasks)
    }

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Peach,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (tasks.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Peach),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "No tasks",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp),
                    color = DarkBlue
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxHeight)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .reorderable(reorderState)
                        .detectReorderAfterLongPress(reorderState)
                ) {
                    items(taskList, key = { it.id }) { task ->
                        ReorderableItem(reorderState, key = task.id) { isDragging ->
                            val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "")

                            TaskCard(
                                task = task,
                                onCheck = onCheck,
                                onEdit = onEdit,
                                modifier = Modifier
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .zIndex(if (isDragging) 1f else 0f)
                                    .shadow(elevation.value)
                            )
                        }
                    }
                }

                if (showTopFade) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(DarkBlue.copy(alpha = 0.6f), Color.Transparent)
                                )
                            )
                    )
                }

                if (showBottomFade) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DarkBlue.copy(alpha = 0.6f))
                                )
                            )
                    )
                }
            }
        }
    }
}