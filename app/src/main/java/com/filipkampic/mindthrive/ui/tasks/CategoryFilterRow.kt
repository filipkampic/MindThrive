package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach

@Composable
@Preview(showBackground = false)
fun CategoryFilterRowPreview() {
    CategoryFilterRow(
        selectedCategory = "All",
        onCategoryChange = {}
    )
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    onCategoryChange: (String) -> Unit
) {
    val categories = listOf("All")
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Box {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = Peach, contentColor = DarkBlue)
            ) {
                Text(text = "Categories ($selectedCategory)")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Peach)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, color = DarkBlue) },
                        onClick = {
                            onCategoryChange(category)
                            expanded = false
                        }
                    )
                }
            }
        }
        IconButton(onClick = { /* TODO: Add new category */ }) {
            Icon(Icons.Default.Add, contentDescription = "Add Category", tint = Peach)
        }
    }
}