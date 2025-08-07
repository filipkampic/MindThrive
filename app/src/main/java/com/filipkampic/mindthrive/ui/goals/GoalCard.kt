package com.filipkampic.mindthrive.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        Text(
            text = when {
                isOverdue -> "Deadline Passed"
                daysLeft == 0L -> "Deadline Today"
                daysLeft == 1L -> "1 day left"
                else -> "$daysLeft days left"
            },
            color = if (isOverdue) Color.Red else DarkBlue,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { goal.progress },
            modifier = Modifier.fillMaxWidth(),
            color = DarkBlue,
            trackColor = DarkBlue.copy(alpha = 0.3f),
        )
    }
}
