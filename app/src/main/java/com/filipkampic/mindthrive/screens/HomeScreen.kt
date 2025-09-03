package com.filipkampic.mindthrive.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Inter
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview(modifier: Modifier = Modifier) {
    HomeScreen(navController = rememberNavController())
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(16.dp)
    ) {
        Text(
            text = "Ready to thrive?",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Peach,
            modifier = Modifier.padding(top = 64.dp)
        )

        val cards = listOf(
            "Calendar" to Icons.Filled.CalendarToday,
            "Tasks" to Icons.Filled.CheckCircle,
            "Notes" to Icons.Filled.EditNote,
            "Focus" to Icons.Filled.CenterFocusStrong,
            "Habit Tracker" to Icons.Filled.Insights,
            "Goals" to Icons.Filled.Flag
        )

        Spacer(modifier = Modifier.height(48.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(cards.size) { index ->
                val (title, icon) = cards[index]
                DashboardCard(title = title, icon = icon) {
                    when (title) {
                        "Calendar" -> navController.navigate("calendar")
                        "Tasks" -> navController.navigate("tasks")
                        "Notes" -> navController.navigate("notes")
                        "Focus" -> navController.navigate("focus")
                        "Habit Tracker" -> navController.navigate("habitTracker")
                        "Goals" -> navController.navigate("goals")
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Peach)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = DarkBlue
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Inter,
                color = DarkBlue,
                modifier = Modifier.width(100.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}