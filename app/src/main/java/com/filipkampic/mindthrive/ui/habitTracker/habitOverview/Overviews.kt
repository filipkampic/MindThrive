package com.filipkampic.mindthrive.ui.habitTracker.habitOverview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun WeeklyYesOrNoOverview(habitId: Int, checks: List<HabitCheck>) {
    val today = LocalDate.now()
    val currentWeekDates = (0..6).map { today.with(DayOfWeek.MONDAY).plusDays(it.toLong()) }
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            currentWeekDates.forEachIndexed { index, date ->
                val isToday = date == today
                Text(
                    text = daysOfWeek[index],
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (isToday) Peach else LightGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            currentWeekDates.forEach { date ->
                val dateStr = date.toString()
                val check = checks.find { it.habitId == habitId && it.date == dateStr }
                val isChecked = check?.isChecked == true

                Text(
                    text = if (isChecked) "✔" else "✘",
                    color = if (isChecked) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
fun MonthlyYesOrNoOverview(checks: List<HabitCheck>) {
    val today = LocalDate.now()
    val firstDayOfMonth = today.withDayOfMonth(1)
    val daysInMonth = today.lengthOfMonth()
    val startOffset = (firstDayOfMonth.dayOfWeek.value % 7)

    val days = buildList {
        repeat(startOffset) { add(null) }
        for (day in 1..daysInMonth) {
            add(LocalDate.of(today.year, today.month, day))
        }
    }

    Column {
        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { date ->
                    val isChecked = date != null && checks.any { it.date == date.toString() && it.isChecked }
                    val isToday = date == today

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isChecked && isToday -> Peach
                                    isChecked -> DarkBlue
                                    isToday -> Peach.copy(alpha = 0.3f)
                                    else -> LightGray
                                }
                            )
                            .border(
                                width = if (isToday) 2.dp else 0.dp,
                                color = Peach,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = date?.dayOfMonth?.toString() ?: "",
                                color = if (isToday) DarkBlue else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyMeasurableOverview(habitId: Int) {
}

@Composable
fun MonthlyMeasurableOverview(habitId: Int) {
}
