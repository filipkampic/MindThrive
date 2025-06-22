package com.filipkampic.mindthrive.ui.habitTracker.habitOverview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun MonthlyProgressOverview(
    habitId: Int,
    isMeasurable: Boolean
) {
    Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        Text("Monthly progress will be shown here.", color = Peach)
    }
    if (isMeasurable) {
        WeeklyMeasurableOverview(habitId)
    } else {
        WeeklyYesOrNoOverview(habitId)
    }
}
