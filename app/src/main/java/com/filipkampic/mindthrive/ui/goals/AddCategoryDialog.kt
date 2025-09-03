package com.filipkampic.mindthrive.ui.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Category", color = DarkBlue, fontFamily = Montserrat) },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("Category name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = DarkBlue,
                        unfocusedBorderColor = DarkBlue.copy(alpha = 0.5f),
                        cursorColor = DarkBlue,
                        focusedLabelColor = DarkBlue,
                        unfocusedLabelColor = DarkBlue.copy(alpha = 0.5f),
                        selectionColors = TextSelectionColors(
                            handleColor = DarkBlue,
                            backgroundColor = DarkBlue.copy(alpha = 0.2f)
                        )
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(value) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue, contentColor = Peach)
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = DarkBlue)
            ) { Text("Cancel") }
        },
        containerColor = Peach,
        titleContentColor = DarkBlue,
        textContentColor = DarkBlue
    )
}
