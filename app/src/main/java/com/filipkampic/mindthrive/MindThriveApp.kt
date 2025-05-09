package com.filipkampic.mindthrive

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.filipkampic.mindthrive.screens.Calendar
import com.filipkampic.mindthrive.screens.Focus
import com.filipkampic.mindthrive.screens.Goals
import com.filipkampic.mindthrive.screens.HabitTracker
import com.filipkampic.mindthrive.screens.HomeScreen
import com.filipkampic.mindthrive.screens.Notes
import com.filipkampic.mindthrive.screens.Profile
import com.filipkampic.mindthrive.screens.Settings
import com.filipkampic.mindthrive.screens.Tasks
import com.filipkampic.mindthrive.screens.TimeManagement

@Composable
fun MindThriveApp() {
    val navController = rememberNavController()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen(navController) }
                composable("profile") { Profile(navController) }
                composable("settings") { Settings(navController) }
                composable("calendar") { Calendar(navController) }
                composable(
                    "time/{selectedDate}",
                    arguments = listOf(navArgument("selectedDate") { type = NavType.StringType })
                ) { backStackEntry ->
                    val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
                    TimeManagement(navController, selectedDate)
                }
                composable("tasks") { Tasks(navController) }
                composable("notes") { Notes(navController) }
                composable("focus") { Focus(navController) }
                composable("habitTracker") { HabitTracker(navController) }
                composable("goals") { Goals(navController) }
            }
        }
    }
}