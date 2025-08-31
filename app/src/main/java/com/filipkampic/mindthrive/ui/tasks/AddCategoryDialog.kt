package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    errorMessage: String? = null
) {
    val forbidden = listOf("All", "General")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Category", color = DarkBlue) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("Category name", color = DarkBlue) },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null,
                supportingText = {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = DarkBlue,
                    unfocusedBorderColor = DarkBlue.copy(alpha = 0.5f),
                    cursorColor = DarkBlue,
                    focusedLabelColor = DarkBlue,
                    unfocusedLabelColor = DarkBlue.copy(alpha = 0.8f),
                    selectionColors = TextSelectionColors(
                        handleColor = DarkBlue,
                        backgroundColor = DarkBlue.copy(alpha = 0.2f)
                    )
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = Peach)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
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