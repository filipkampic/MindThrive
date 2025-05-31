package com.filipkampic.mindthrive

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.TaskRepository
import com.filipkampic.mindthrive.screens.Calendar
import com.filipkampic.mindthrive.screens.Focus
import com.filipkampic.mindthrive.screens.Goals
import com.filipkampic.mindthrive.screens.HabitTracker
import com.filipkampic.mindthrive.screens.HomeScreen
import com.filipkampic.mindthrive.screens.notes.Notes
import com.filipkampic.mindthrive.screens.Profile
import com.filipkampic.mindthrive.screens.Settings
import com.filipkampic.mindthrive.screens.tasks.Tasks
import com.filipkampic.mindthrive.screens.TimeManagementWrapper
import com.filipkampic.mindthrive.screens.notes.NoteEditor
import com.filipkampic.mindthrive.screens.tasks.EisenhowerMatrix
import com.filipkampic.mindthrive.viewmodel.TaskListViewModel

val Context.dataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Composable
fun MindThriveApp() {
    val navController = rememberNavController()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val activity = context as? Activity
        val intent = activity?.intent
        val target = intent?.getStringExtra("navigate_to")
        val date = intent?.getStringExtra("date")
        if (target == "time" && date != null) {
            navController.navigate("time/$date")
        }
    }

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
                    TimeManagementWrapper(navController, selectedDate)
                }
                composable("tasks") { Tasks(navController) }
                composable("eisenhower") {
                    val localContext = LocalContext.current
                    val db = AppDatabase.getDatabase(localContext)
                    val repo = TaskRepository(localContext, db.taskDao(), db.categoryDao())
                    val viewModel = remember { TaskListViewModel(repo) }
                    val tasks by viewModel.tasks.collectAsState()

                    EisenhowerMatrix(
                        tasks = tasks,
                        onCheck = viewModel::toggleTask,
                        navController = navController
                    )
                }
                composable("notes") { Notes(navController) }
                composable("editNote") { NoteEditor(navController, noteId = null) }
                composable("editNote/{noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
                    NoteEditor(navController = navController, noteId = noteId)
                }
                composable("focus") { Focus(navController) }
                composable("habitTracker") { HabitTracker(navController) }
                composable("goals") { Goals(navController) }
            }
        }
    }
}