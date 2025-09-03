package com.filipkampic.mindthrive.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.filipkampic.mindthrive.model.goals.GoalStep
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStepDialog(
    existingStep: GoalStep? = null,
    goalId: Int,
    onDismissRequest: () -> Unit,
    onConfirm: (id: Int?, name: String, description: String?) -> Unit,
    onDelete: ((stepToDelete: GoalStep) -> Unit)? = null
) {
    var name by remember(existingStep) { mutableStateOf(existingStep?.name ?: "") }
    var description by remember(existingStep) { mutableStateOf(existingStep?.description ?: "") }
    var nameError by remember { mutableStateOf<String?>(null) }

    var showConfirmDeleteDialog by remember { mutableStateOf(false) }

    val isEditMode = existingStep != null
    val dialogTitle = if (isEditMode) "Edit Step" else "Add New Step"
    val confirmButtonText = if (isEditMode) "Save Changes" else "Add Step"

    val customTextSelectionColors = TextSelectionColors(
        handleColor = DarkBlue,
        backgroundColor = DarkBlue.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Dialog(onDismissRequest = onDismissRequest) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Peach)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dialogTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = Montserrat,
                            color = DarkBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                        },
                        label = { Text("Name") },
                        placeholder = { Text("Next step") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = DarkBlue.copy(alpha = 0.5f),
                            cursorColor = DarkBlue,
                            focusedLabelColor = DarkBlue,
                            unfocusedLabelColor = DarkBlue.copy(alpha = 0.5f),
                        )
                    )
                    if (nameError != null) {
                        Text(
                            text = nameError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (Optional)") },
                        placeholder = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = DarkBlue.copy(alpha = 0.5f),
                            cursorColor = DarkBlue,
                            focusedLabelColor = DarkBlue,
                            unfocusedLabelColor = DarkBlue.copy(alpha = 0.5f),
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isEditMode && onDelete != null) Arrangement.SpaceBetween else Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEditMode && onDelete != null) {
                            Button(
                                onClick = {
                                    showConfirmDeleteDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkBlue,
                                    contentColor = Peach
                                )
                            ) {
                                Text("Delete")
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }

                        Row {
                            Button(
                                onClick = {
                                    if (name.isBlank()) {
                                        nameError = "Step name cannot be empty"
                                    } else {
                                        onConfirm(
                                            existingStep?.id,
                                            name,
                                            description.takeIf {
                                                it.isNotBlank()
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DarkBlue,
                                    contentColor = Peach
                                )
                            ) {
                                Text(confirmButtonText)
                            }
                        }
                    }
                }
            }
        }

        if (showConfirmDeleteDialog && existingStep != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDeleteDialog = false },
                title = { Text("Confirm Deletion", color = DarkBlue, fontFamily = Montserrat) },
                text = { Text("Are you sure you want to delete this step?", color = DarkBlue.copy(alpha = 0.8f)) },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete?.invoke(existingStep)
                            showConfirmDeleteDialog = false
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkBlue,
                            contentColor = Peach
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmDeleteDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = DarkBlue)
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Peach,
                titleContentColor = DarkBlue,
                textContentColor = DarkBlue
            )
        }
    }
}