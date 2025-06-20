package com.filipkampic.mindthrive.screens.habitTracker

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.TimePickerDialog
import com.filipkampic.mindthrive.ui.habitTracker.FrequencyDialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.util.Calendar

@SuppressLint("DefaultLocale", "DiscouragedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YesOrNoHabit(
    navController: NavController,
    onSave: (String, String, String?, String?) -> Unit
) {
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Every day") }
    var reminderTime by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }

    var showFrequencyDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Peach,
            backgroundColor = Peach.copy(alpha = 0.4f)
        )
    ) {
        val colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Peach,
            unfocusedTextColor = Peach,
            focusedContainerColor = DarkBlue,
            unfocusedContainerColor = DarkBlue,
            cursorColor = Peach,
            focusedBorderColor = Peach,
            unfocusedBorderColor = Peach
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Peach
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            onSave(name, frequency, reminderTime, description)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = Peach)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
                )
            },
            containerColor = DarkBlue
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = Peach) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = Peach),
                    keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                    colors = colors
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFrequencyDialog = true }
                ) {
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Frequency", color = Peach) },
                        textStyle = LocalTextStyle.current.copy(color = Peach),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledBorderColor = Peach,
                            disabledLabelColor = Peach,
                            disabledTextColor = Peach,
                            unfocusedBorderColor = Peach,
                            focusedBorderColor = Peach,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(-1f)
                    )
                }

                if (showFrequencyDialog) {
                    FrequencyDialog(
                        currentValue = frequency,
                        onDismiss = { showFrequencyDialog = false },
                        onSave = { selected ->
                            frequency = selected
                            showFrequencyDialog = false
                        }
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", color = Peach) },
                    placeholder = { Text("(Optional)", color = Peach) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(color = Peach),
                    keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences),
                    colors = colors
                )

                var showTimePicker by remember { mutableStateOf(false) }

                Button(
                    onClick = { showTimePicker = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Peach)
                ) {
                    Text("Reminder: ${reminderTime ?: "Off"}", color = DarkBlue)
                }

                if (showTimePicker) {
                    val now = Calendar.getInstance()

                    val initialHour: Int
                    val initialMinute: Int

                    if (reminderTime != null && reminderTime!!.contains(":")) {
                        val parts = reminderTime!!.split(":")
                        initialHour = parts[0].toIntOrNull() ?: now.get(Calendar.HOUR_OF_DAY)
                        initialMinute = parts[1].toIntOrNull() ?: now.get(Calendar.MINUTE)
                    } else {
                        initialHour = now.get(Calendar.HOUR_OF_DAY)
                        initialMinute = now.get(Calendar.MINUTE)
                    }

                    TimePickerDialog(
                        initialHour = initialHour,
                        initialMinute = initialMinute,
                        onDismiss = { showTimePicker = false },
                        onTimeSelected = { hour, minute ->
                            reminderTime = if (hour == -1 && minute == -1) {
                                null
                            } else {
                                String.format("%02d:%02d", hour, minute)
                            }
                        },
                        onRemove = {
                            reminderTime = null
                        }
                    )
                }
            }
        }
    }
}
