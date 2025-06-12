package com.filipkampic.mindthrive.ui.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun SessionPlanner(
    sessionCount: Int,
    sessionPlans: List<String>,
    onSessionPlansChanged: (Int, String) -> Unit,
    onPlanConfirmed: () -> Unit,
    onCancel: () -> Unit
) {
    val allFilled = sessionPlans.all { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Spacer(Modifier.height(64.dp))

        if (!allFilled) {
            IconButton(onClick = onCancel) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Peach)
            }
        }

        Text("Plan your activities:", color = Peach)
        Spacer(Modifier.height(12.dp))

        CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
                handleColor = Peach,
                backgroundColor = Peach.copy(alpha = 0.4f)
            )
        ) {
            sessionPlans.forEachIndexed { index, value ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { onSessionPlansChanged(index, it) },
                    label = { Text("Session ${index + 1}", color = Peach) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Peach,
                        unfocusedBorderColor = Peach,
                        focusedLabelColor = Peach,
                        unfocusedLabelColor = Peach,
                        cursorColor = Peach,
                        focusedTextColor = Peach,
                        unfocusedTextColor = Peach
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        keyboardType = KeyboardType.Text
                    ),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { if (allFilled) onPlanConfirmed() },
                enabled = allFilled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (allFilled) Peach else Peach.copy(alpha = 0.5f),
                    disabledContainerColor = Peach.copy(alpha = 0.5f),
                    contentColor = if (allFilled) DarkBlue else DarkBlue.copy(alpha = 0.4f),
                    disabledContentColor = DarkBlue.copy(alpha = 0.4f)
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Start Focus", color = DarkBlue)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    sessionPlans.forEachIndexed { index, _ ->
                        onSessionPlansChanged(index, "")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Peach),
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset", color = DarkBlue)
            }
        }
    }
}
