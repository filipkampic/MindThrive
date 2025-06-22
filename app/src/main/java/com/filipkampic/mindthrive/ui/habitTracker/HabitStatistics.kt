package com.filipkampic.mindthrive.ui.habitTracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun HabitStatistics(habit: Habit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Current streak: ${habit.streak}", color = Peach)
        Text("Best streak: 0", color = Peach)
        Text("Success rate: 0%", color = Peach)
    }
}
