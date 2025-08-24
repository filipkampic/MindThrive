package com.filipkampic.mindthrive.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.GoalsViewModel
import com.filipkampic.mindthrive.viewmodel.GoalsViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GoalNotesTab(
    goalId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao(), db.goalStepDao(), db.goalNoteDao(), db.goalCategoryDao())
    }
    val viewModel: GoalsViewModel = viewModel(factory = GoalsViewModelFactory(repository))

    val notes by viewModel.getNotes(goalId).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(notes, key = { it.id }) { note ->
                val date = remember(note.updatedAt) {
                    SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault()).format(Date(note.updatedAt))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .clickable { navController.navigate("goalNoteEditor/$goalId?noteId=${note.id}") }
                ) {
                    Text(text = note.title, color = Peach, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(2.dp))
                    Text(text = date, color = Peach.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
                }
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = Peach.copy(alpha = 0.3f)
                )
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("goalNoteEditor/$goalId?noteId=-1") },
            containerColor = Peach,
            contentColor = DarkBlue,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Note")
        }
    }
}