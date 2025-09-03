package com.filipkampic.mindthrive.screens.habitTracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.habitTracker.HabitRepository
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Inter
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.HabitViewModel
import com.filipkampic.mindthrive.viewmodel.HabitViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitStats(
    navController: NavController
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val repository = HabitRepository(
        habitDao = db.habitDao(),
        habitCheckDao = db.habitCheckDao()
    )
    val factory = HabitViewModelFactory(repository)
    val viewModel: HabitViewModel = viewModel(factory = factory)

    var stats by remember { mutableStateOf<Triple<Int, Pair<String, Int>, Int>?>(null) }

    LaunchedEffect(Unit) {
        stats = viewModel.calculateOverallStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habit Statistics", fontFamily = Montserrat) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    titleContentColor = Peach,
                    navigationIconContentColor = Peach,
                    actionIconContentColor = Peach
                )
            )
        },
        containerColor = DarkBlue
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .offset(y = (-32).dp),
            contentAlignment = Alignment.Center
        ) {
            stats?.let { (totalHabits, longest, successRate) ->
                val streakLabel = if (longest.first.isNotBlank()) { "Longest Streak (${longest.first})" } else { "Longest Streak" }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$totalHabits", fontSize = 40.sp, color = Peach)
                    Text("Active Habits", fontSize = 20.sp, color = Peach, fontFamily = Inter)
                    Spacer(Modifier.height(80.dp))
                    Text(text = "${longest.second}", fontSize = 40.sp, color = Peach)
                    Text(streakLabel, fontSize = 20.sp, color = Peach, fontFamily = Inter)
                    Spacer(Modifier.height(80.dp))
                    Text(text = "$successRate%", fontSize = 40.sp, color = Peach)
                    Text("Success Rate", fontSize = 20.sp, color = Peach, fontFamily = Inter)
                }
            }
        }
    }
}
