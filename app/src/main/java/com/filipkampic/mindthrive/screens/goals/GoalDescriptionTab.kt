package com.filipkampic.mindthrive.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.GoalsViewModel
import com.filipkampic.mindthrive.viewmodel.GoalsViewModelFactory
import java.time.format.DateTimeFormatter

@Composable
fun GoalDescriptionTab(
    goalId: Int,
    navController: NavController,
) {
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao(), db.goalStepDao(), db.goalNoteDao())
    }
    val viewModel: GoalsViewModel = viewModel(factory = GoalsViewModelFactory(repository))

    val goal by viewModel.getGoalById(goalId).collectAsState(initial = null)

    goal?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = it.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Peach,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            it.motivation?.takeIf { it.isNotBlank() }?.let { m ->
                Text("Motivation", color = Peach, style  = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(m, color = Peach)
            }

            it.reward?.takeIf { it.isNotBlank() }?.let { r ->
                Text("Reward", color = Peach, style  = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(r, color = Peach)
            }

            Text("Deadline", color = Peach, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            if (viewModel.isDeadlinePassed(it.deadline)) {
                Text(
                    text = "DEADLINE PASSED",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = it.deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(it.deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")), color = Peach)
            }

            Text("Category", color = Peach, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(it.category.ifBlank { "General" }, color = Peach)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        navController.navigate("editGoal/${it.id}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Peach, contentColor = DarkBlue)
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = {
                        viewModel.deleteGoal(it)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Peach, contentColor = DarkBlue)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
