package com.filipkampic.mindthrive.screens.notes

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.notes.AddFolderButton
import com.filipkampic.mindthrive.ui.notes.FolderCard
import com.filipkampic.mindthrive.ui.notes.NoteCard
import com.filipkampic.mindthrive.ui.notes.SearchBar
import com.filipkampic.mindthrive.ui.notes.NotesSortDropdown
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.NotesViewModel
import com.filipkampic.mindthrive.viewmodel.NotesViewModelFactory

@Composable
@Preview(showBackground = true)
fun NotesPreview() {
    Notes(navController = NavController(LocalContext.current))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notes(navController: NavController) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(context.applicationContext as Application))

    val folders by viewModel.folders.collectAsState()
    val notes by viewModel.visibleNotes.collectAsState()
    val selectedFolderId by viewModel.selectedFolderId.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
            },
            actions = {
                IconButton(onClick = { /* TO-DO: Hamburger menu action */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkBlue,
                titleContentColor = Peach,
                navigationIconContentColor = Peach,
                actionIconContentColor = Peach
            )
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (folders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No folders yet.", color = Peach)
                    }
                }
            }

            items(folders) { folder ->
                FolderCard(
                    folder = folder,
                    selected = folder.id == selectedFolderId,
                    onClick = { viewModel.selectFolder(folder.id) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            AddFolderButton(onClick = { viewModel.showAddFolderDialog() })
        }

        Spacer(modifier = Modifier.height(24.dp))

        SearchBar(
            query = searchQuery,
            onQueryChanged = { viewModel.updateSearchQuery(it) }
        )

        NotesSortDropdown(
            current = sortOption,
            onSelected = { viewModel.updateSortOption(it) }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            if (notes.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notes yet.", color = Peach)
                    }
                }
            }

            items(notes) { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    NoteCard(
                        note = note,
                        onClick = { navController.navigate("editNote/${note.id}")}
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("editNote") },
                modifier = Modifier.padding(16.dp),
                containerColor = Peach
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note", tint = DarkBlue)
            }
        }
    }
}