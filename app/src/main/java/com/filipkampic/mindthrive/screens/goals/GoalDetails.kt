package com.filipkampic.mindthrive.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetails(
    goalId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao(), db.goalStepDao(), db.goalNoteDao(), db.goalCategoryDao())
    }
    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModelFactory(repository)
    )

    val goal by viewModel.getGoalById(goalId).collectAsState(initial = null)

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Description", "Steps", "Notes")

    var hasEmittedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(goal) {
        if (goal != null) {
            hasEmittedOnce = true
        } else if (hasEmittedOnce) {
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = DarkBlue,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue,
                    navigationIconContentColor = Peach
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(DarkBlue),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabs.forEachIndexed { i, tab ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedTab = i }
                    ) {
                        if (selectedTab == i) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .background(Peach)
                                    .align(Alignment.TopCenter)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = tab,
                                modifier = Modifier.padding(top = 12.dp),
                                color = if (selectedTab == i) Peach else Peach.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> GoalDescriptionTab(goalId, navController)
                1 -> GoalStepsTab(goalId)
                2 -> GoalNotesTab(goalId, navController)
            }
        }
    }
}