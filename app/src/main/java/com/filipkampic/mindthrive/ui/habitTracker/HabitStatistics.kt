package com.filipkampic.mindthrive.ui.habitTracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.HabitViewModel

@Composable
fun HabitStatistics(
    habit: Habit,
    viewModel: HabitViewModel
) {
    val checks by viewModel.getAllChecksForHabit(habit.id).collectAsState(initial = emptyList())
    val stats = remember(checks, habit) {
        viewModel.calculateHabitStats(checks, habit)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(DarkBlue, shape = RoundedCornerShape(12.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Habit Statistics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Peach
        )
        StatRow(label = "Current streak", value = "${stats.currentStreak}")
        StatRow(label = "Best streak", value = "${stats.bestStreak}")
        StatRow(label = "Success rate", value = "${stats.successRate} %")
    }
}

@Composable
fun StatRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 16.sp)
        Text(value, color = Peach, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

