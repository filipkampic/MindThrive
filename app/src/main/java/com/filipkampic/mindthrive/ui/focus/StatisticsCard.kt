package com.filipkampic.mindthrive.ui.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
fun StatisticCard(title: String, duration: Int, modifier: Modifier = Modifier) {
    val hours = duration / 3600
    val minutes = (duration % 3600) / 60
    Column(
        modifier = Modifier
            .background(Peach, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(title, color = DarkBlue)
        Spacer(Modifier.height(4.dp))
        Text("${hours}h ${minutes}m", color = DarkBlue)
    }
}