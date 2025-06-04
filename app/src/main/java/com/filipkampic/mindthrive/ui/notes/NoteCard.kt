package com.filipkampic.mindthrive.ui.notes

import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            .fillMaxSize()
            .padding(4.dp)
            .background(color = Peach, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(note.timestamp)),
                color = DarkBlue,
                fontSize = 12.sp
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                thickness = 1.dp,
                color = DarkBlue
            )
            Text(
                text = stripHtml(note.content).take(100),
                color = DarkBlue,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun stripHtml(content: String): String {
    return Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString()
}