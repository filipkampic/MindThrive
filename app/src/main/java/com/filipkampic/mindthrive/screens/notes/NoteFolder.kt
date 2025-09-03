@file:Suppress("DEPRECATION")

package com.filipkampic.mindthrive.screens.notes

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.model.notes.NotesSortOption
import com.filipkampic.mindthrive.ui.notes.NoteCard
import com.filipkampic.mindthrive.ui.notes.NotesSortDropdown
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Montserrat
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.NotesViewModel
import com.filipkampic.mindthrive.viewmodel.NotesViewModelFactory

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteFolder(navController: NavController, folderId: Int?) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(context.applicationContext as Application))

    val notes by viewModel.notes.collectAsState()
    val folderNotes = notes.filter { it.folderId == folderId }
    val folderName = viewModel.folders.collectAsState().value.find { it.id == folderId }?.name ?: ""
    val selectedFolder = viewModel.folders.collectAsState().value.find { it.id == folderId }
    var sortOption by rememberSaveable { mutableStateOf(NotesSortOption.BY_DATE_DESC) }
    val sortedNotes = remember(folderNotes, sortOption) {
        when (sortOption) {
            NotesSortOption.BY_DATE_DESC -> folderNotes.sortedByDescending { it.timestamp }
            NotesSortOption.BY_DATE_ASC -> folderNotes.sortedBy { it.timestamp }
            NotesSortOption.BY_TITLE_ASC -> folderNotes.sortedBy { it.title.lowercase() }
            NotesSortOption.BY_TITLE_DESC -> folderNotes.sortedByDescending { it.title.lowercase() }
        }
    }
    var showRenameDialog by remember { mutableStateOf(false) }

    LaunchedEffect(folderId) {
        viewModel.loadNotesInFolder(folderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        folderName,
                        color = Peach,
                        modifier = Modifier.clickable {
                            showRenameDialog = true
                        },
                        fontFamily = Montserrat
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Peach)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showDeleteFolderDialog() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Folder", tint = Peach)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
                .padding(padding)
        ) {
            NotesSortDropdown(current = sortOption, onSelected = { sortOption = it })

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (sortedNotes.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 100.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text("No notes in this folder", color = Peach)
                        }
                    }
                } else {
                    items(sortedNotes) { note ->
                        NoteCard(note = note, onClick = {
                            navController.navigate("editNote/${note.id}")
                        })
                    }
                }
            }
        }
    }

    if (viewModel.showDeleteFolderDialog.collectAsState().value && selectedFolder != null) {
        var deleteNotes by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteFolderDialog() },
            title = { Text("Delete Folder '${selectedFolder.name}'?", color = DarkBlue, fontFamily = Montserrat) },
            text = {
                if (folderNotes.isEmpty()) {
                    Text(
                        "Are you sure you want to delete this empty folder?",
                        color = DarkBlue
                    )
                } else {
                    Column {
                        Text(
                            "Do you want to delete all notes in this folder or move them out of it?",
                            color = DarkBlue
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { deleteNotes = false }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = !deleteNotes,
                                onClick = { deleteNotes = false },
                                colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Move notes out of folder", color = DarkBlue)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { deleteNotes = true }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = deleteNotes,
                                onClick = { deleteNotes = true },
                                colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete notes", color = DarkBlue)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (deleteNotes) {
                            viewModel.deleteFolderAndNotes(selectedFolder)
                        } else {
                            viewModel.deleteFolderOnly(selectedFolder)
                        }
                        navController.navigate("notes")
                    }
                ) {
                    Text("Delete", color = Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteFolderDialog() }) {
                    Text("Cancel", color = DarkBlue)
                }
            },
            containerColor = Peach
        )
    }

    if (showRenameDialog && selectedFolder != null) {
        var newName by remember { mutableStateOf(selectedFolder.name) }

        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Folder", color = DarkBlue, fontFamily = Montserrat) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    placeholder = { Text("Folder name", color = DarkBlue) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = DarkBlue,
                        unfocusedBorderColor = DarkBlue.copy(alpha = 0.5f),
                        cursorColor = DarkBlue,
                        selectionColors = TextSelectionColors(
                            handleColor = DarkBlue,
                            backgroundColor = DarkBlue.copy(alpha = 0.2f)
                        )
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.renameFolder(selectedFolder.id, newName)
                        showRenameDialog = false
                    }
                ) {
                    Text("Rename", color = DarkBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text("Cancel", color = DarkBlue)
                }
            },
            containerColor = Peach
        )
    }
}
