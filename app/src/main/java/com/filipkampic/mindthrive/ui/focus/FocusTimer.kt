package com.filipkampic.mindthrive.ui.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp


@Composable
fun FocusTimer(timeText: String, ringColor: Color, progress: Float = 1f) {
    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12f
            drawCircle(
                color = ringColor.copy(alpha = 0.2f),
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
        }
        Text(
            text = timeText,
            style = MaterialTheme.typography.displayLarge,
            color = ringColor
        )
    }
}