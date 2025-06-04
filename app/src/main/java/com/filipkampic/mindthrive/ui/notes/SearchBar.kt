package com.filipkampic.mindthrive.ui.notes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun SearchBarPreview() {
    SearchBar(query = "", onQueryChanged = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Peach,
        backgroundColor = Peach.copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = { Text("Search notes", color = Peach) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Peach)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = DarkBlue,
                focusedTextColor = Peach,
                unfocusedTextColor = Peach,
                focusedIndicatorColor = Peach,
                unfocusedIndicatorColor = Peach,
                cursorColor = Peach
            )
        )
    }
}