package com.filipkampic.mindthrive.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun NoteCardPreview() {
    NoteCard(
        note = Note(
            id = 1,
            title = "Sample Note",
            content = "This is a sample note content.",
            timestamp = System.currentTimeMillis()
        ),
        onClick = {}
    )
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(color = Peach, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
        }
    }
}
