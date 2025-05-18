package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.Task
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

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
        onCheck = {}
    )
}

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    onCheck: (Task) -> Unit
) {
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
            tasks.forEach { task ->
                TaskCard(task, onCheck)
            }
        }
    }
}