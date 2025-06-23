package com.filipkampic.mindthrive.ui.habitTracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.habitTracker.HabitStats
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun HabitStatistics(stats: HabitStats) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Current streak: ${stats.currentStreak}", color = Peach)
        Text("Best streak: ${stats.bestStreak}", color = Peach)
        Text("Success rate: ${stats.successRate}%", color = Peach)
    }
}
