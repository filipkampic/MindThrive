package com.filipkampic.mindthrive.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: LocalDate = LocalDate.now(),
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val initialMillis = initialDate
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    val customColors = darkColorScheme(
        primary = DarkBlue,
        onPrimary = Peach,
        primaryContainer = Peach,
        onPrimaryContainer = DarkBlue,
        background = DarkBlue,
        onBackground = Peach,
        surface = DarkBlue,
        onSurface = Peach,
        surfaceVariant = DarkBlue,
        onSurfaceVariant = Peach,
        secondary = Peach,
        onSecondary = DarkBlue,
        tertiary = Peach,
        onTertiary = DarkBlue,
        outline = Peach
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        MaterialTheme(colorScheme = customColors) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                color = DarkBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Select Date",
                        style = MaterialTheme.typography.titleMedium,
                        color = Peach,
                        modifier = Modifier.padding(16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DatePicker(
                            state = state,
                            modifier = Modifier.fillMaxWidth(),
                            colors = DatePickerDefaults.colors(
                                containerColor = DarkBlue,
                                headlineContentColor = Peach,
                                weekdayContentColor = Peach,
                                subheadContentColor = Peach,
                                yearContentColor = Peach.copy(alpha = 0.8f),
                                currentYearContentColor = Peach,
                                selectedYearContentColor = DarkBlue,
                                selectedYearContainerColor = Peach,
                                dayContentColor = Peach,
                                disabledDayContentColor = Peach.copy(alpha = 0.4f),
                                todayContentColor = Peach,
                                todayDateBorderColor = Peach,
                                selectedDayContentColor = DarkBlue,
                                selectedDayContainerColor = Peach
                            )
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel", color = Peach)
                        }

                        Spacer(Modifier.width(8.dp))

                        TextButton(onClick = {
                            val selected = state.selectedDateMillis
                            if (selected != null) {
                                val localDate = Instant.ofEpochMilli(selected)
                                    .atZone(ZoneOffset.UTC)
                                    .toLocalDate()
                                onDateSelected(localDate)
                            }
                            onDismiss()
                        }) {
                            Text("OK", color = Peach)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}