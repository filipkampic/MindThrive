package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.tasks.Priority
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.LocalDate

@Composable
@Preview(showBackground = true)
fun AddTaskDialogPreview() {
    AddTaskDialog(onDismiss = {}, onAdd = {}, categories = listOf("All", "General"), defaultCategory = "General")
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAdd: (Task) -> Unit,
    categories: List<String>,
    defaultCategory: String
) {
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf< LocalDate?>(null) }
    var priority by remember { mutableStateOf(Priority.NONE) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(defaultCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task", color = DarkBlue) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = DarkBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Due date: ", color = DarkBlue)
                    Spacer(Modifier.width(8.dp))
                    Text(dueDate?.toString() ?: "None", color = DarkBlue)
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { showDatePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = Peach)
                    ) {
                        Text("Pick")
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Priority: ", color = DarkBlue)
                    Spacer(Modifier.width(8.dp))
                    PriorityDropdownBox(priority, onChange = { priority = it })
                }

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Category: ", color = DarkBlue)
                    Spacer(Modifier.width(8.dp))
                    CategoryDropdownBox(
                        selected = selectedCategory,
                        options = categories,
                        onSelect = { selectedCategory = it }
                    )
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onAdd(
                        Task(
                            title = title,
                            dueDate = dueDate,
                            priority = priority,
                            category = selectedCategory
                        )
                    )
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = Peach)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = DarkBlue)
            }
        },
        containerColor = Peach
    )

    if (showDatePicker) {
        DatePickerDialogContent(
            onDateSelected = {
                dueDate = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
