package com.filipkampic.mindthrive.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.model.notes.NoteFolder
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun FolderCardPreview() {
    FolderCard(
        folder = NoteFolder(id = 1, name = "Sample Folder"),
        selected = true,
        onClick = {}
    )
}

@Composable
fun FolderCard(
    folder: NoteFolder,
    selected: Boolean,
    onClick: () -> Unit
) {
    val border = if (selected) Modifier.border(2.dp, Color.Cyan) else Modifier
    Box(
        modifier = Modifier
            .padding(4.dp)
            .then(border)
            .clickable(onClick = onClick)
            .background(Peach)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(folder.name, color = DarkBlue, fontWeight = FontWeight.Medium)
    }
}