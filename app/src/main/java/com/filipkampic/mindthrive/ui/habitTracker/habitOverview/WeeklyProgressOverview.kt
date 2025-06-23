package com.filipkampic.mindthrive.ui.habitTracker.habitOverview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import java.time.LocalDate

@Composable
fun WeeklyProgressOverview(
    habitId: Int,
    isMeasurable: Boolean,
    checks: List<HabitCheck>
) {
    if (isMeasurable) {
        WeeklyMeasurableOverview(habitId = habitId)
    } else {
        WeeklyYesOrNoOverview(habitId = habitId, checks = checks)
    }
}
