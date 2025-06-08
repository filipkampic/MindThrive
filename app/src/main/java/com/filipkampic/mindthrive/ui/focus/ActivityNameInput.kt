package com.filipkampic.mindthrive.ui.focus

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun ActivityNameInput(
    value: String,
    onValueChange: (String) -> Unit,
    isReadOnly: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }

    CompositionLocalProvider(LocalTextSelectionColors provides TextSelectionColors(
        handleColor = Peach,
        backgroundColor = Peach.copy(alpha = 0.4f)
    )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Activity name", color = Peach) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Peach,
                focusedTextColor = Peach,
                unfocusedTextColor = Peach,
                focusedBorderColor = Peach,
                unfocusedBorderColor = Peach
            ),
            interactionSource = interactionSource,
            readOnly = isReadOnly
        )
    }
}
