package com.filipkampic.mindthrive.ui.habitTracker.habitOverview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import java.time.LocalDate

@Composable
fun WeeklyProgressOverview(
    habitId: Int,
    isMeasurable: Boolean
) {
    val viewModel: HabitViewModel = viewModel()
    val checks by viewModel.getAllChecksForHabit(habitId).collectAsState(initial = emptyList())

    LaunchedEffect(checks) {
        val today = LocalDate.now().toString()
        val todayCheck = checks.find { it.date == today }
        println("DEBUG >>> Check for today: $todayCheck")
    }


    if (isMeasurable) {
        WeeklyMeasurableOverview(habitId = habitId)
    } else {
        WeeklyYesOrNoOverview(habitId = habitId, checks = checks)
    }

}
