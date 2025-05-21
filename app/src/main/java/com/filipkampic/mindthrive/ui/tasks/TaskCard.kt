package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.Priority
import com.filipkampic.mindthrive.model.Task
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.LocalDate

@Composable
@Preview(showBackground = false)
fun TaskCardPreview(modifier: Modifier = Modifier) {
    TaskCard(
        task = Task(
            title = "Task Title",
            dueDate = LocalDate.now(),
            priority = Priority.HIGH,
            isDone = false
        ),
        onCheck = {},
        onEdit = {}
    )
}

@Composable
fun TaskCard(
    task: Task,
    onCheck: (Task) -> Unit,
    onEdit: (Task) -> Unit
) {
    val expired = task.dueDate?.isBefore(LocalDate.now()) == true && !task.isDone
    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color(0xFFFF5C5C)
        Priority.MEDIUM -> Color(0xFFFFA500)
        Priority.LOW -> Color(0xFF64B5F6)
        else -> Color.Unspecified
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp)
            .clickable { onEdit(task) },
        colors = CardDefaults.cardColors(
            containerColor = Peach,
            contentColor = DarkBlue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { onCheck(task) },
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Column {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (task.isDone) Color.Gray else MaterialTheme.colorScheme.onSurface,
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                        )
                    )

                    if (task.priority != Priority.NONE) {
                        Box(
                            modifier = Modifier
                                .background(priorityColor, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = task.priority.name,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    if (task.dueDate != null) {
                        Text(
                            text = "Due: ${task.dueDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (expired) Color.Red else Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}