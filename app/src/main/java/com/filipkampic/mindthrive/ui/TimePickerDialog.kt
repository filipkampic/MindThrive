package com.filipkampic.mindthrive.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val customColors = darkColorScheme(
        primary = DarkBlue,
        onPrimary = Peach,
        primaryContainer = Peach,
        onPrimaryContainer = DarkBlue,
        surface = Peach,
        onSurface = DarkBlue,
        secondary = Peach,
        onSecondary = DarkBlue,
        tertiary = Peach,
        onTertiary = DarkBlue,
        surfaceVariant = Peach,
        onSurfaceVariant = DarkBlue,
        outline = Peach
    )

    Dialog(onDismissRequest = onDismiss) {
        MaterialTheme(colorScheme = customColors) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                color = DarkBlue
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Time", style = MaterialTheme.typography.titleMedium, color = Peach)

                    Spacer(modifier = Modifier.height(16.dp))

                    TimePicker(state = state)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            onTimeSelected(-1, -1)
                            onDismiss()
                        }) {
                            Text("Remove", color = Peach)
                        }
                        Row {
                            TextButton(onClick = onDismiss) {
                                Text("Cancel", color = Peach)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = {
                                onTimeSelected(state.hour, state.minute)
                                onDismiss()
                            }) {
                                Text("OK", color = Peach)
                            }
                        }
                    }
                }
            }
        }
    }
}
