package com.filipkampic.mindthrive.screens.habitTracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.filipkampic.mindthrive.model.habitTracker.Habit
import com.filipkampic.mindthrive.ui.habitTracker.HabitStatistics
import com.filipkampic.mindthrive.ui.habitTracker.habitOverview.MonthlyProgressOverview
import com.filipkampic.mindthrive.ui.habitTracker.habitOverview.WeeklyProgressOverview
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetail(
    habit: Habit,
    navController: NavController,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Weekly Progress", "Monthly Progress")

    Scaffold(
        containerColor = DarkBlue,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Peach)
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Peach)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Peach)
                    }
                },
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBlue,
                contentColor = Peach,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Peach
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTab == index) Peach else Peach.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == 0) {
                WeeklyProgressOverview(habitId = habit.id, isMeasurable = habit.isMeasurable)
            } else {
                MonthlyProgressOverview(habitId = habit.id, isMeasurable = habit.isMeasurable)
            }

            Spacer(modifier = Modifier.height(32.dp))

            HabitStatistics(habit = habit)
        }
    }
}
