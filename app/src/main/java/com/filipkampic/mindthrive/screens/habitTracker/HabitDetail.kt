package com.filipkampic.mindthrive.screens.habitTracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetail(
    habitId: Int,
    viewModel: HabitViewModel,
    navController: NavController,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onStats: () -> Unit
) {
    val habits by viewModel.habits.collectAsState()
    val currentHabit = habits.find { it.id == habitId } ?: return

    val today = remember { LocalDate.now() }
    val weekStart = remember { today.with(DayOfWeek.MONDAY) }

    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val year = currentMonth.year
    val month = currentMonth.month
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.dayOfWeek.value % 7

    val dates = buildList {
        repeat(firstDayOfWeek) { add(null) }
        for (day in 1..daysInMonth) {
            add(LocalDate.of(year, month, day))
        }
    }

    val allChecks by viewModel.getAllChecksForHabit(habitId).collectAsState(initial = emptyList())

    val todayDateStr = LocalDate.now().toString()
    val todayCheck by viewModel.getCheckForDate(currentHabit.id, todayDateStr).collectAsState(initial = null)

    LaunchedEffect(todayCheck, allChecks) {}

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Peach)
                    }
                },
                actions = {
                    IconButton(onClick = { onStats() }) {
                        Icon(Icons.Default.PieChart, contentDescription = "Stats", tint = Peach)
                    }
                    IconButton(onClick = { onEdit() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Peach)
                    }
                    IconButton(onClick = { onDelete() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Peach)
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = DarkBlue
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(currentHabit.name, color = Peach, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (currentHabit.target != null) {
                        Text("${currentHabit.target} ${currentHabit.unit ?: ""}", color = Peach)
                    }
                    Text(currentHabit.reminder?.ifBlank { "Off" } ?: "Off", color = Peach)
                    Text(currentHabit.frequency ?: "", color = Peach)
                }
            }

            if (!currentHabit.description.isNullOrBlank()) {
                Text(currentHabit.description, color = Peach)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                (0..6).forEach { i ->
                    val dayDate = weekStart.plusDays(i.toLong())
                    val dateStr = dayDate.toString()
                    val isToday = dayDate == LocalDate.now()

                    val isChecked = if (dateStr == todayDateStr) {
                        todayCheck?.isChecked ?: currentHabit.isDoneToday
                    } else {
                        allChecks.find { it.date == dateStr }?.isChecked ?: false
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = dayDate.dayOfWeek.name.take(3),
                            color = if (isToday) Color.Red else Peach,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                        )

                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                val newChecked = !isChecked
                                viewModel.toggleCheck(currentHabit.id, dateStr, newChecked)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = DarkBlue,
                                checkmarkColor = Peach,
                                uncheckedColor = Color.LightGray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${month.name.lowercase().replaceFirstChar { it.uppercase() }} $year",
                color = Peach,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month", tint = Peach)
                }
                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month", tint = Peach)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                items(dates) { date ->
                    if (date == null) {
                        Spacer(modifier = Modifier.aspectRatio(1f))
                    } else {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val dateStr = date.toString()

                            val isChecked = if (dateStr == todayDateStr) {
                                todayCheck?.isChecked ?: currentHabit.isDoneToday
                            } else {
                                allChecks.find { it.date == dateStr }?.isChecked ?: false
                            }

                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    val newChecked = !isChecked
                                    viewModel.toggleCheck(currentHabit.id, dateStr, newChecked)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = DarkBlue,
                                    checkmarkColor = Peach,
                                    uncheckedColor = Color.LightGray
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
