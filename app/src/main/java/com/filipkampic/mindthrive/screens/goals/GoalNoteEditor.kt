package com.filipkampic.mindthrive.screens.goals

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.goals.GoalRepository
import com.filipkampic.mindthrive.model.goals.GoalNote
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.GoalsViewModel
import com.filipkampic.mindthrive.viewmodel.GoalsViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalNoteEditor(
    goalId: Int,
    noteId: Int?,
    navController: NavController
) {
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    val repository = remember {
        val db = AppDatabase.getDatabase(context)
        GoalRepository(db.goalDao(), db.goalStepDao(), db.goalNoteDao(), db.goalCategoryDao())
    }
    val viewModel: GoalsViewModel = viewModel(factory = GoalsViewModelFactory(repository))

    var title by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    val isEditing = noteId != null
    val scope = rememberCoroutineScope()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<GoalNote?>(null) }

    val loadedNote by viewModel.selectedGoalNote.collectAsState()

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Peach,
        backgroundColor = Peach.copy(alpha = 0.4f)
    )

    var initialTitle by remember { mutableStateOf("") }
    var initialText by remember { mutableStateOf("") }

    val hasChanges = (title != initialTitle) || (text != initialText)
    var showExitConfirmDialog by remember { mutableStateOf(false) }
    BackHandler(enabled = hasChanges && !showExitConfirmDialog) {
        showExitConfirmDialog = true
    }

    LaunchedEffect(noteId) {
        if (isEditing && noteId != null) {
            viewModel.loadGoalNote(noteId)
        } else {
            viewModel.clearSelectedGoalNote()
            title = ""
            text = ""
        }
    }

    LaunchedEffect(loadedNote) {
        loadedNote?.let {
            if (isEditing && it.id == noteId) {
                title = it.title
                initialTitle = it.title

                text = it.text
                initialText = it.text
            }
        } ?: run {
            initialTitle = ""
            initialText = ""
        }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (isEditing) "Edit Note" else "Add Note",
                            color = Peach
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (title != initialTitle || text != initialText) {
                                    showExitConfirmDialog = true
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Peach
                            )
                        }
                    },
                    actions = {
                        if (isEditing) {
                            IconButton(onClick = {
                                noteToDelete = loadedNote
                                showDeleteConfirmDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Note",
                                    tint = Peach
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.upsertNote(goalId, noteId, title, text)
                        initialTitle = title
                        initialText = text
                        showExitConfirmDialog = false
                        navController.popBackStack()
                    },
                    containerColor = Peach,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save Note", tint = DarkBlue)
                }
            },
            containerColor = DarkBlue
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .background(DarkBlue)
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    }
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = Peach.copy(alpha = 0.7f)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    textStyle = LocalTextStyle.current.copy(color = Peach),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Peach,
                        unfocusedBorderColor = Peach.copy(alpha = 0.6f),
                        cursorColor = Peach,
                        focusedLabelColor = Peach,
                        unfocusedLabelColor = Peach.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Text", color = Peach.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 150.dp),
                    minLines = 8,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    textStyle = LocalTextStyle.current.copy(color = Peach),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Peach,
                        unfocusedBorderColor = Peach.copy(alpha = 0.6f),
                        cursorColor = Peach,
                        focusedLabelColor = Peach,
                        unfocusedLabelColor = Peach.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Confirm Deletion", color = DarkBlue) },
            text = {
                Text(
                    "Are you sure you want to delete this note?",
                    color = DarkBlue.copy(alpha = 0.8f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            noteToDelete?.let { viewModel.deleteNote(it) }
                            showDeleteConfirmDialog = false
                            noteToDelete = null
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkBlue,
                        contentColor = Peach
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        noteToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = DarkBlue)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Peach,
            titleContentColor = DarkBlue,
            textContentColor = DarkBlue
        )
    }

    if (showExitConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showExitConfirmDialog = false
                    navController.popBackStack()
                }) {
                    Text("Exit", color = Peach)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirmDialog = false }) {
                    Text("Cancel", color = Peach)
                }
            },
            title = { Text("Unsaved Changes", color = Peach) },
            text = { Text("You have unsaved changes. Are you sure you want to exit?", color = Peach) },
            containerColor = DarkBlue
        )
    }
}