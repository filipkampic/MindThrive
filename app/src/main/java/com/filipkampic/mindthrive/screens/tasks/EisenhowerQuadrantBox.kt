package com.filipkampic.mindthrive.screens.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.R
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.ui.tasks.TaskCard
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Inter
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = false)
fun EisenhowerQuadrantBoxPreview() {
    EisenhowerQuadrantBox(
        title = "Important & Urgent",
        tasks = listOf(
            Task(id = 1, title = "Task 1"),
            Task(id = 2, title = "Task 2")
        ),
        onCheck = {},
        onEdit = {},
        color = Color.Red,
        modifier = Modifier,
        iconRes = R.drawable.number_one
    )
}

@Composable
fun EisenhowerQuadrantBox(
    title: String,
    tasks: List<Task>,
    onCheck: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    iconRes: Int
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Peach)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(title, style = MaterialTheme.typography.titleSmall, fontFamily = Inter, color = color)
                }
                Spacer(Modifier.height(8.dp))

                if (tasks.isEmpty()) {
                    Text("No tasks", color = DarkBlue, modifier = Modifier.padding(start = 8.dp))
                }
            }

            items(tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onCheck = onCheck,
                    onEdit = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                    compact = true
                )
            }
        }
    }
}
