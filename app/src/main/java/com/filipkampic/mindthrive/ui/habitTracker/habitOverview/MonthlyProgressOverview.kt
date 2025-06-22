package com.filipkampic.mindthrive.ui.habitTracker.habitOverview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filipkampic.mindthrive.viewmodel.HabitViewModel

@Composable
fun MonthlyProgressOverview(
    habitId: Int,
    isMeasurable: Boolean
) {
    val viewModel: HabitViewModel = viewModel()
    val checks by viewModel.getAllChecksForHabit(habitId).collectAsState(initial = emptyList())

    if (isMeasurable) {
        MonthlyMeasurableOverview(habitId = habitId)
    } else {
        MonthlyYesOrNoOverview(checks = checks)
    }
}
