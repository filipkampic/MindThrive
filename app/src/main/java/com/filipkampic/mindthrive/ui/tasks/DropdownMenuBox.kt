package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.Priority
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = false)
fun DropdownMenuBoxPreview() {
    DropdownMenuBox(selected = Priority.HIGH, onChange = {})
}

@Composable
fun DropdownMenuBox(selected: Priority, onChange: (Priority) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val priorityColor = getPriorityColor(selected)

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = priorityColor, contentColor = White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = selected.name)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Peach)
        ) {
            Priority.entries.forEach { priority ->
                val bgColor = getPriorityColor(priority)
                val text = priority.name

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .background(bgColor, shape = RoundedCornerShape(8.dp))
                        .clickable {
                            onChange(priority)
                            expanded = false
                        }
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Text(text = text, color = White)
                }
            }
        }
    }
}

private fun getPriorityColor(priority: Priority): Color {
    return when (priority) {
        Priority.HIGH -> Color(0xFFFF5C5C)
        Priority.MEDIUM -> Color(0xFFFFA500)
        Priority.LOW -> Color(0xFF64B5F6)
        Priority.NONE -> DarkBlue.copy(alpha = 0.6f)
    }
}
