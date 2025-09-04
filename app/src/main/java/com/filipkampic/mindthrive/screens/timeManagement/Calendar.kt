package com.filipkampic.mindthrive.screens.timeManagement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.filipkampic.mindthrive.ui.DatePickerDialog
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Inter
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
@Preview(showBackground = true)
fun CalendarPreview() {
    Calendar(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(navController: NavController) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()

    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value + 6) % 7
    val previousMonth = currentMonth.minusMonths(1)
    val nextMonth = currentMonth.plusMonths(1)

    val daysInPrevMonth = previousMonth.lengthOfMonth()
    val daysBefore = List(firstDayOfWeek) {
        val day = daysInPrevMonth - firstDayOfWeek + it + 1
        LocalDate.of(previousMonth.year, previousMonth.month, day)
    }

    val daysCurrent = List(daysInMonth) {
        LocalDate.of(currentMonth.year, currentMonth.month, it + 1)
    }

    val totalCells = daysBefore.size + daysCurrent.size
    val extraCells = (7 - totalCells % 7).let { if (it == 7) 0 else it }

    val daysAfter = List(extraCells) {
        LocalDate.of(nextMonth.year, nextMonth.month, it + 1)
    }

    val fullCalendar = daysBefore + daysCurrent + daysAfter
    val weekDays = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = LocalDate.of(currentMonth.year, currentMonth.month, 1),
            onDismiss = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                currentMonth = YearMonth.of(selectedDate.year, selectedDate.month)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar", fontFamily = Montserrat) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = Peach)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    navigationIconContentColor = Peach,
                    titleContentColor = Peach,
                    actionIconContentColor = Peach
                )
            )
        },
        containerColor = DarkBlue
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Peach, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous",
                        tint = DarkBlue
                    )
                }
                Text(
                    text = "${
                        currentMonth.month.getDisplayName(TextStyle.FULL, Locale.UK)
                    } ${currentMonth.year}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Inter,
                    color = DarkBlue,
                    modifier = Modifier.clickable { showDatePicker = true }
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = DarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weekDays.forEach {
                            Text(
                                text = it,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Peach,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        userScrollEnabled = false,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        items(fullCalendar.size) { index ->
                            val date = fullCalendar[index]
                            val isCurrentMonth = date.month == currentMonth.month
                            val isToday = date == today

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxWidth()
                                    .background(
                                        color = if (isToday) DarkBlue.copy(alpha = 0.95f) else Peach,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = if (isToday) 2.dp else 1.dp,
                                        color = if (isToday) Peach else DarkBlue,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = isCurrentMonth) {
                                        val formattedDate = date.toString()
                                        if (formattedDate.isNotEmpty()) {
                                            navController.navigate("time/$formattedDate")
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 16.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        isToday -> Peach
                                        isCurrentMonth -> DarkBlue
                                        else -> DarkBlue.copy(alpha = 0.4f)
                                    }
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = { currentMonth = YearMonth.now() },
                        containerColor = Peach,
                        contentColor = DarkBlue,
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                    ) {
                        Icon(imageVector = Icons.Default.Today, contentDescription = "Today")
                    }
                }
            }
        }
    }
}
