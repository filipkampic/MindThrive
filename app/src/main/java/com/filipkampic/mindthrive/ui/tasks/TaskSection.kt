package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.shadow
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
        onMove = {}
    )
}

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    onCheck: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onMove: (List<Task>) -> Unit
) {
    val taskList = remember { mutableStateListOf<Task>() }
    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        if (from.index in taskList.indices && to.index in 0..taskList.size) {
            val item = taskList.removeAt(from.index)
            taskList.add(to.index, item)
            onMove(taskList.mapIndexed { index, task -> task.copy(position = index) })
        }
    })

    LaunchedEffect(tasks) {
        taskList.clear()
        taskList.addAll(tasks)
    }

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Peach,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (tasks.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Peach),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "No tasks",
                    modifier = Modifier.padding(16.dp),
                    color = DarkBlue
                )
            }
        } else {
            LazyColumn(
                state = reorderState.listState,
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
        }
    }
}