package com.filipkampic.mindthrive.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.model.goals.Goal
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun GoalCard(
    goal: Goal,
    daysLeft: Long,
    isOverdue: Boolean,
    hasSteps: Boolean,
    progress: Float,
    isCompleted: Boolean,
    isCompletedOnTime: Boolean?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = Peach, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = goal.name,
            color = DarkBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when {
                    isCompleted -> if (isCompletedOnTime == false) "Completed Late" else "Completed"
                    isOverdue -> "Deadline Passed"
                    daysLeft == 0L -> "Deadline Today"
                    daysLeft == 1L -> "1 day left"
                    else -> "$daysLeft days left"
                },
                color = when {
                    isCompleted && isCompletedOnTime == false -> Color(0xFFF57C00)
                    isCompleted -> Color(0xFF2E7D32)
                    isOverdue -> Color.Red
                    else -> DarkBlue
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            if (hasSteps) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (hasSteps) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = DarkBlue,
                trackColor = DarkBlue.copy(alpha = 0.3f),
            )
        }
    }
}
