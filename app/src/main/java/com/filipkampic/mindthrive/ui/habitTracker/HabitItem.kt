package com.filipkampic.mindthrive.ui.habitTracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.LocalDate

@Composable
fun HabitItem(
    habit: Habit,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onEnterAmount: (Habit) -> Unit,
    checks: List<HabitCheck>
) {
    val today = LocalDate.now().toString()
    val todayCheck = checks.find { it.date == today && it.habitId == habit.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Peach),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(habit.name, color = DarkBlue)

            if (habit.isMeasurable) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (todayCheck?.amount != null) {
                        Text(
                            text = buildString {
                                val amount = todayCheck.amount
                                append(
                                    if (amount % 1.0 == 0.0) amount.toInt().toString()
                                    else amount.toString()
                                )
                                append(" ${habit.unit ?: ""}")
                            },
                            color = DarkBlue
                        )
                    }
                    IconButton(onClick = { onEnterAmount(habit) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Enter amount", tint = DarkBlue)
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(habit.streak.toString(), color = DarkBlue)
                    Checkbox(
                        checked = habit.isDoneToday,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = DarkBlue,
                            checkmarkColor = Peach,
                            uncheckedColor = DarkBlue
                        )
                    )
                }
            }
        }
    }
}