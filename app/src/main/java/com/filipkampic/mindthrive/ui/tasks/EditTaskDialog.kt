package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.tasks.Priority
import com.filipkampic.mindthrive.model.tasks.Task
import com.filipkampic.mindthrive.ui.DatePickerDialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.LocalDate

@Composable
@Preview(showBackground = true)
fun EditTaskDialogPreview(modifier: Modifier = Modifier) {
    EditTaskDialog(
        task = Task(
            title = "Task Title",
            dueDate = LocalDate.now(),
            priority = Priority.HIGH,
            isDone = false
        ),
        categories = listOf("All", "General"),
        onDismiss = {}, onSave = {}, onDelete = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var dueDate by remember { mutableStateOf(task.dueDate) }
    var priority by remember { mutableStateOf(task.priority) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(task.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task", color = DarkBlue, fontFamily = Montserrat) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = DarkBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = DarkBlue,
                        unfocusedBorderColor = DarkBlue.copy(alpha = 0.6f),
                        cursorColor = DarkBlue,
                        focusedLabelColor = DarkBlue,
                        unfocusedLabelColor = DarkBlue.copy(alpha = 0.8f),
                        selectionColors = TextSelectionColors(
                            handleColor = DarkBlue,
                            backgroundColor = DarkBlue.copy(alpha = 0.3f)
                        )
                    )
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
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onDelete(task) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                ) {
                    Text("Delete")
                }

                Spacer(Modifier.width(6.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Cancel", color = DarkBlue)
                }

                Spacer(Modifier.width(6.dp))

                OutlinedButton(
                    onClick = {
                        onSave(task.copy(title = title, dueDate = dueDate, priority = priority, category = selectedCategory))
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = DarkBlue, contentColor = Peach),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Save")
                }
            }
        },
        containerColor = Peach
    )

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = dueDate ?: LocalDate.now(),
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                dueDate = it
                showDatePicker = false
            }
        )
    }
}