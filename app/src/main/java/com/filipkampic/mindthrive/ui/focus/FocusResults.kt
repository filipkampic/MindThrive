package com.filipkampic.mindthrive.ui.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun FocusResults(
    title: String,
    totalTime: Int,
    sessions: Int? = null,
    activityName: String?,
    onDone: () -> Unit
) {
    val minutes = totalTime / 60
    val seconds = totalTime % 60
    val timeFormatted = "%d:%02d".format(minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Congrats",
            tint = Peach,
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text("Deep Work Complete!", color = Peach, fontSize = 24.sp)

        Spacer(Modifier.height(8.dp))

        Text("Time: $timeFormatted", color = Peach)

        sessions?.let {
            Spacer(Modifier.height(8.dp))
            Text("Sessions: $it", color = Peach)
        }

        activityName?.takeIf { it.isNotBlank() }?.let { names ->
            if (names.startsWith("Activity:")) {
                Spacer(Modifier.height(8.dp))
                Text(text = names, color = Peach)
            } else {
                names.split("|").forEachIndexed { index, activity ->
                    Spacer(Modifier.height(8.dp))
                    Text("Activity ${index + 1}: $activity", color = Peach)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = onDone, colors = ButtonDefaults.buttonColors(containerColor = Peach)) {
            Text("Return", color = DarkBlue)
        }
    }
}