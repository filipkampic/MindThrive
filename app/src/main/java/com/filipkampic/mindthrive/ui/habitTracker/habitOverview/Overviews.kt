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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filipkampic.mindthrive.model.habitTracker.HabitCheck
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WeeklyYesOrNoOverview(
    habitId: Int,
    checks: List<HabitCheck>
) {
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
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value

    val days = buildList {
        val daysToAddBeforeMonday = (startDayOfWeek - 1 + 7) % 7
        repeat(daysToAddBeforeMonday) { add(null) }
        for (day in 1..daysInMonth) {
            add(LocalDate.of(today.year, today.month, day))
        }
    }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .width(302.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = today.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = Peach
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    color = LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .size(42.dp)
                        .padding(4.dp)
                )
            }
        }

        val weeks = days.chunked(7)
        Column {
            weeks.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    week.forEach { date ->
                        val check = date?.let { d -> checks.find { it.date == d.toString() } }
                        val isChecked = check?.isChecked == true
                        val isToday = date == today

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(DarkBlue, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = date?.dayOfMonth?.toString() ?: "",
                                    color = if (isToday) Peach else Color.White,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = when {
                                        isChecked -> "✓"
                                        check != null && !isChecked -> "✘"
                                        else -> ""
                                    },
                                    color = when {
                                        isChecked -> Color(0xFF4CAF50)
                                        check != null && !isChecked -> Color(0xFFFF5252)
                                        else -> Color.Transparent
                                    },
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyMeasurableOverview(
    habitId: Int,
    checks: List<HabitCheck>
) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val datesOfWeek = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val viewModel: HabitViewModel = viewModel()
    val habit by viewModel.getHabitById(habitId).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEachIndexed { index, _ ->
                val date = datesOfWeek[index]
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
            datesOfWeek.forEach { date ->
                val check = checks.find { it.date == date.toString() }
                val isToday = date == today
                val amount = check?.amount
                val color = when {
                    amount == null -> Peach
                    habit?.target != null && amount >= habit!!.target!! -> Color.Green
                    else -> Color.Red
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, DarkBlue, CircleShape)
                        .background(
                            if (isToday) Peach.copy(alpha = 0.2f) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = check?.amount?.let {
                            if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
                        } ?: "-",
                        color = color,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyMeasurableOverview(
    habitId: Int,
    checks: List<HabitCheck>
) {
    val today = LocalDate.now()
    val firstDayOfMonth = today.withDayOfMonth(1)
    val daysInMonth = today.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value

    val days = buildList {
        val daysToAddBeforeMonday = (startDayOfWeek - 1 + 7) % 7
        repeat(daysToAddBeforeMonday) { add(null) }
        for (day in 1..daysInMonth) {
            add(LocalDate.of(today.year, today.month, day))
        }
    }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val viewModel: HabitViewModel = viewModel()
    val habit by viewModel.getHabitById(habitId).collectAsState(initial = null)

    Column(
        modifier = Modifier
            .width(302.dp)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = today.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = Peach
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    color = LightGray,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .size(42.dp)
                        .padding(4.dp)
                )
            }
        }

        val weeks = days.chunked(7)
        Column {
            weeks.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    week.forEach { date ->
                        val check = date?.let { d -> checks.find { it.date == d.toString() } }
                        val isToday = date == today
                        val amount = check?.amount
                        val amountText = amount?.let {
                            if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
                        } ?: ""
                        val color = when {
                            amount == null -> Peach
                            habit?.target != null && amount >= habit!!.target!! -> Color.Green
                            else -> Color.Red
                        }


                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(DarkBlue, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = date?.dayOfMonth?.toString() ?: "",
                                    color = if (isToday) Peach else Color.White,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = amountText,
                                    color = color,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
