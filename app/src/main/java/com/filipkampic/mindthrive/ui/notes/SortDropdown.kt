package com.filipkampic.mindthrive.ui.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme.colors
import com.filipkampic.mindthrive.model.notes.NotesSortOption
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = true)
fun SortDropdownPreview(modifier: Modifier = Modifier) {
    SortDropdown(current = NotesSortOption.BY_DATE_DESC, onSelected = {})
}

@Composable
fun SortDropdown(
    current: NotesSortOption,
    onSelected: (NotesSortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(containerColor = Peach),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text("Sort: ${current.label}", color = DarkBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = DarkBlue
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-32).dp, y = 0.dp),
            modifier = Modifier.background(Peach)
        ) {
            NotesSortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.label,
                            color = DarkBlue
                        )
                    },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = DarkBlue
                    ),
                    modifier = Modifier.background(Peach)
                )
            }
        }
    }
}
