package com.filipkampic.mindthrive.ui.habitTracker.habitOverview

import androidx.compose.runtime.Composable
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck


@Composable
fun WeeklyProgressOverview(
    habitId: Int,
    isMeasurable: Boolean,
    checks: List<HabitCheck>
) {
    if (isMeasurable) {
        WeeklyMeasurableOverview(habitId = habitId, checks = checks)
    } else {
        WeeklyYesOrNoOverview(habitId = habitId, checks = checks)
    }
}
