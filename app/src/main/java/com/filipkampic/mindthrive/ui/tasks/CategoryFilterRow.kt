package com.filipkampic.mindthrive.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
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
        categories = listOf("All", "Category 1", "Category 2"),
        onCategoryChange = {},
        onAddCategoryClick = {},
        onDeleteCategoryClick = {}
    )
}

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    categories: List<String>,
    onCategoryChange: (String) -> Unit,
    onAddCategoryClick: () -> Unit,
    onDeleteCategoryClick: (String) -> Unit
) {
    val allCategories = listOf("All") + categories
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
                allCategories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category,
                            color = DarkBlue,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onCategoryChange(category)
                                    expanded = false
                                }
                        )

                        if (category != "All" && category != "General") {
                            IconButton(
                                onClick = {
                                    expanded = false
                                    onDeleteCategoryClick(category)
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DarkBlue)
                            }
                        }
                    }
                }
            }
        }
        IconButton(onClick = onAddCategoryClick) {
            Icon(Icons.Default.Add, contentDescription = "Add Category", tint = Peach)
        }
    }
}