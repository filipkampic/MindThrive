package com.filipkampic.mindthrive.ui.habitTracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun FrequencyDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val options = listOf(
        "Every day",
        "Every _ days",
        "_ times per week",
        "_ times per month"
    )

    var selectedOption by remember {
        mutableStateOf(
            when {
                currentValue == "Every day" -> "Every day"
                currentValue.startsWith("Every ") -> "Every _ days"
                currentValue.contains("times per week") -> "_ times per week"
                currentValue.contains("times per month") -> "_ times per month"
                else -> "Every day"
            }
        )
    }
    var numberInput by remember {
        mutableStateOf(
            when {
                currentValue.startsWith("Every ") && currentValue != "Every day" ->
                    currentValue.removePrefix("Every ").removeSuffix(" days").trim()
                currentValue.contains("times per week") ->
                    currentValue.removeSuffix(" times per week").trim()
                currentValue.contains("times per month") ->
                    currentValue.removeSuffix(" times per month").trim()
                else -> ""
            }
        )
    }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Peach,
        unfocusedTextColor = Peach,
        focusedContainerColor = DarkBlue,
        unfocusedContainerColor = DarkBlue,
        cursorColor = Peach,
        focusedBorderColor = Peach,
        unfocusedBorderColor = Peach
    )
    val keyboardType = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Frequency", color = Peach) },
        containerColor = DarkBlue,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    when (option) {
                        "Every day" -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedOption = option },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedOption == option,
                                    onClick = { selectedOption = option },
                                    colors = RadioButtonDefaults.colors(selectedColor = Peach)
                                )
                                Text("Every day", color = Peach)
                            }
                        }

                        "Every _ days" -> {
                            if (selectedOption == option) {
                                FrequencyOptionRow(
                                    labelStart = "Every ",
                                    labelEnd = " days",
                                    isSelected = true,
                                    onSelect = { selectedOption = option },
                                    inputValue = numberInput,
                                    onValueChange = { numberInput = it },
                                    colors = colors,
                                    keyboardType = keyboardType
                                )
                            } else {
                                RadioOption(option, selected = false) {
                                    selectedOption = option
                                    numberInput = ""
                                }
                            }
                        }

                        "_ times per week" -> {
                            if (selectedOption == option) {
                                FrequencyOptionRow(
                                    labelStart = "",
                                    labelEnd = " times per week",
                                    isSelected = true,
                                    onSelect = { selectedOption = option },
                                    inputValue = numberInput,
                                    onValueChange = { numberInput = it },
                                    colors = colors,
                                    keyboardType = keyboardType
                                )
                            } else {
                                RadioOption(option, selected = false) {
                                    selectedOption = option
                                    numberInput = ""
                                }
                            }
                        }

                        "_ times per month" -> {
                            if (selectedOption == option) {
                                FrequencyOptionRow(
                                    labelStart = "",
                                    labelEnd = " times per month",
                                    isSelected = true,
                                    onSelect = { selectedOption = option },
                                    inputValue = numberInput,
                                    onValueChange = { numberInput = it },
                                    colors = colors,
                                    keyboardType = keyboardType
                                )
                            } else {
                                RadioOption(option, selected = false) {
                                    selectedOption = option
                                    numberInput = ""
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val final = when (selectedOption) {
                    "Every _ days" -> if (numberInput.isNotBlank()) "Every $numberInput days" else "Every day"
                    "_ times per week" -> if (numberInput.isNotBlank()) "$numberInput times per week" else "Every day"
                    "_ times per month" -> if (numberInput.isNotBlank()) "$numberInput times per month" else "Every day"
                    else -> "Every day"
                }
                onSave(final)
            }) {
                Text("Save", color = Peach)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Peach)
            }
        }
    )
}

