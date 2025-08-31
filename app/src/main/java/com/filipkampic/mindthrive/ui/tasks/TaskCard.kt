package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.model.tasks.Priority
import com.filipkampic.mindthrive.model.tasks.Task
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
        onEdit = {},
        modifier = Modifier,
        compact = false
    )
}

@Composable
fun TaskCard(
    task: Task,
    onCheck: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    modifier: Modifier,
    compact: Boolean = false
) {
    val now = LocalDate.now()
    val expired = task.dueDate?.isBefore(now) == true && !task.isDone
    val dueSoon = task.dueDate?.isEqual(now) == true && !task.isDone
    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color(0xFFFF5C5C)
        Priority.MEDIUM -> Color(0xFFFFA500)
        Priority.LOW -> Color(0xFF64B5F6)
        else -> Color.Unspecified
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (compact) 0.dp else 4.dp,
                vertical = if (compact) 0.dp else 2.dp
            )
            .clickable { onEdit(task) },
        colors = CardDefaults.cardColors(
            containerColor = Peach,
            contentColor = DarkBlue
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                start = if (compact) 2.dp else 8.dp,
                top = if (compact) 4.dp else 8.dp,
                end = if (compact) 4.dp else 8.dp,
                bottom = if (compact) 4.dp else 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { onCheck(task) },
                    modifier = Modifier
                        .padding(end = if (compact) 0.dp else 4.dp)
                        .scale(if (compact) 0.6f else 1.0f),
                    colors = CheckboxDefaults.colors(
                        checkedColor = DarkBlue,
                        checkmarkColor = Peach,
                        uncheckedColor = DarkBlue,
                        disabledCheckedColor = Peach,
                        disabledUncheckedColor = Peach
                    )
                )

                Column {
                    Text(
                        text = task.title,
                        style = if (compact) {
                            MaterialTheme.typography.labelMedium.copy(
                                color = if (task.isDone) Color.Gray else DarkBlue,
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                            )
                        } else {
                            MaterialTheme.typography.bodySmall.copy(
                                color = if (task.isDone) Color.Gray else DarkBlue,
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                            )
                        }
                    )

                    if (task.priority != Priority.NONE) {
                        Box(
                            modifier = Modifier
                                .background(priorityColor, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.priority.name,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                lineHeight = if (compact) 12.sp else 14.sp
                            )
                        }
                    }

                    if (task.dueDate != null) {
                        val dueText = if (dueSoon) "⚠\uFE0F Due: ${task.dueDate}" else "Due: ${task.dueDate}"
                        val dueColor = when {
                            expired -> Color.Red
                            dueSoon -> Color(0xFFFFA500)
                            else -> Color.Gray
                        }

                        Text(
                            text = dueText,
                            style = MaterialTheme.typography.labelSmall,
                            color = dueColor,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
