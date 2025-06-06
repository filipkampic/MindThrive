package com.filipkampic.mindthrive.screens.focus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.focus.FocusBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Stopwatch(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                },
                title = { Text("Stopwatch") },
                actions = {
                    IconButton(onClick = { navController.navigate("statistics") }) {
                        Icon(Icons.Default.PieChart, contentDescription = "Statistics")
                    }
                    IconButton(onClick = { /* TODO: hamburger meni */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        },
        bottomBar = {
            FocusBottomNavigation(current = "stopwatch") {
                navController.navigate(it)
            }
        }
    ) { padding ->

    }
}
