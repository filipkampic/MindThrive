package com.filipkampic.mindthrive.screens.goals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.viewmodel.GoalsViewModel
import com.filipkampic.mindthrive.viewmodel.GoalsViewModelFactory

@Composable
fun EditGoal(goalId: Int, navController: NavController) {
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao(), db.goalStepDao())
    }
    val viewModel: GoalsViewModel = viewModel(factory = GoalsViewModelFactory(repository))
    val categories by viewModel.categories.collectAsState()
    val goal by viewModel.getGoalById(goalId).collectAsState(initial = null)

    goal?.let {
        GoalForm(
            initialGoal = it,
            categories = categories,
            onSave = { updated -> viewModel.updateGoal(updated.copy(id = it.id)) },
            navController = navController
        )
    }
}
