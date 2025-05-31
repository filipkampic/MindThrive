package com.filipkampic.mindthrive.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun AddFolderButtonPreview() {
    AddFolderButton(onClick = {})
}

@Composable
fun AddFolderButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
            .background(color = Peach, shape = MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("+", color = DarkBlue, fontWeight = FontWeight.Bold)
    }
}
