package com.filipkampic.mindthrive.ui.notes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filipkampic.mindthrive.R
import com.filipkampic.mindthrive.model.notes.NoteFolder
import com.filipkampic.mindthrive.ui.theme.Inter
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun FolderCardPreview() {
    FolderCard(
        folder = NoteFolder(id = 1, name = "Sample Folder"),
        onClick = {}
    )
}

@Composable
fun FolderCard(
    folder: NoteFolder,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.folder),
            contentDescription = "Folder image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        Text(
            text = folder.name,
            color = Peach,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Inter,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 12.dp, start = 6.dp, end = 6.dp)
                .align(Alignment.BottomCenter),
            maxLines = 2
        )
    }
}