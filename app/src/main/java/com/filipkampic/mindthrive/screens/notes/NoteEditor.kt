@file:Suppress("UNCHECKED_CAST")

package com.filipkampic.mindthrive.screens.notes

import android.annotation.SuppressLint
import android.app.Application
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Looks3
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.LooksTwo
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.filipkampic.mindthrive.ui.theme.DarkBlue
import com.filipkampic.mindthrive.ui.theme.Peach
import com.filipkampic.mindthrive.viewmodel.NotesViewModel
import com.filipkampic.mindthrive.viewmodel.NotesViewModelFactory
import jp.wasabeef.richeditor.RichEditor

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditor(
    navController: NavController,
    noteId: Int?
) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(context.applicationContext as Application))

    val note by viewModel.getNoteById(noteId).collectAsState(initial = null)

    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var isContentEditing by remember { mutableStateOf(false) }
    val editorRef = remember { mutableStateOf<RichEditor?>(null) }
    val activeStyles = remember { mutableStateMapOf<String, Boolean>() }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Peach,
        backgroundColor = Peach.copy(alpha = 0.4f)
    )

    val hasChanges = (title != (note?.title ?: "")) || (content != (note?.content ?: ""))
    val showExitDialog = remember { mutableStateOf(false) }
    BackHandler(enabled = hasChanges && !showExitDialog.value) {
        showExitDialog.value = true
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val titleFocus = remember { MutableInteractionSource() }
    val contentFocus = remember { MutableInteractionSource() }
    val titleFocused by titleFocus.collectIsFocusedAsState()
    val contentFocused by contentFocus.collectIsFocusedAsState()
    var isEditing = titleFocused || contentFocused || isContentEditing

    val scrollState = rememberScrollState()

    LaunchedEffect(content) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (noteId == null) "New Note" else "Edit Note",
                            color = Peach
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (hasChanges) {
                                showExitDialog.value = true
                            } else {
                                navController.popBackStack()
                            }
                        }) {
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
                                focusManager.clearFocus()
                                keyboardController?.hide()
                                isEditing = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Finish Editing",
                                    tint = Peach
                                )
                            }
                        }

                        if (noteId != null && !isEditing) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Note",
                                    tint = Peach
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isEditing) Color(0xFF052236) else DarkBlue
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        viewModel.saveNote(noteId, title, content)
                        navController.popBackStack()
                    },
                    containerColor = Peach,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save", tint = DarkBlue)
                }
            },
            containerColor = DarkBlue,
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .padding(padding)
                        .padding(16.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { focusManager.clearFocus() })
                        }
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Title", color = Peach) },
                        interactionSource = titleFocus,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Peach,
                            unfocusedIndicatorColor = Peach,
                            cursorColor = Peach
                        ),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        textStyle = LocalTextStyle.current.copy(
                            color = Peach,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {

                        AndroidView(
                            factory = { context ->
                                RichEditor(context).apply {
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    note?.let { setHtml(it.content) }
                                    setPadding(16, 16, 16, 16)
                                    setBackgroundColor(DarkBlue.value.toInt())
                                    setEditorFontColor(android.graphics.Color.parseColor("#F6C8B3"))
                                    setEditorFontSize(16)
                                    setInputEnabled(true)
                                    setOnTextChangeListener { text ->
                                        content = text
                                        getCurrentStyles()?.let { types ->
                                            activeStyles.clear()
                                            types.forEach { type ->
                                                activeStyles[type.name.lowercase()] = true
                                            }
                                        }
                                    }
                                    setOnFocusChangeListener { _, hasFocus ->
                                        isContentEditing = hasFocus
                                        if (hasFocus) {
                                            getCurrentStyles()?.let { types ->
                                                activeStyles.clear()
                                                types.forEach { type ->
                                                    activeStyles[type.name.lowercase()] = true
                                                }
                                            }
                                        }
                                    }
                                    setOnDecorationChangeListener { _, types ->
                                        activeStyles.clear()
                                        types?.forEach { type ->
                                            activeStyles[type.name.lowercase()] = true
                                        }
                                    }
                                    editorRef.value = this
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (content.isBlank()) {
                            Text(
                                text = "Content",
                                color = Peach,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isContentEditing) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            val buttons = listOf(
                                Triple(Icons.Default.FormatBold, "bold") {
                                    editorRef.value?.setBold()
                                    activeStyles["bold"] = !(activeStyles["bold"] ?: false)
                                },
                                Triple(Icons.Default.FormatItalic, "italic") {
                                    editorRef.value?.setItalic()
                                    activeStyles["italic"] = !(activeStyles["italic"] ?: false)
                                },
                                Triple(Icons.Default.FormatUnderlined, "underline") {
                                    editorRef.value?.setUnderline()
                                    activeStyles["underline"] =
                                        !(activeStyles["underline"] ?: false)
                                },
                                Triple(Icons.Default.FormatStrikethrough, "strikethrough") {
                                    editorRef.value?.setStrikeThrough()
                                    activeStyles["strikethrough"] = !(activeStyles["strikethrough"] ?: false)
                                },
                                Triple(Icons.AutoMirrored.Filled.FormatListBulleted, "unordered") {
                                    editorRef.value?.setBullets()
                                    activeStyles["unordered"] = !(activeStyles["unordered"] ?: false)
                                },
                                Triple(Icons.Default.FormatListNumbered, "ordered") {
                                    editorRef.value?.setNumbers()
                                    activeStyles["ordered"] = !(activeStyles["ordered"] ?: false)
                                },
                                Triple(Icons.Default.SubdirectoryArrowRight, "indent") {
                                    editorRef.value?.setIndent()
                                    activeStyles["indent"] = !(activeStyles["indent"] ?: false)
                                },
                                Triple(Icons.Default.HorizontalRule, "outdent") {
                                    editorRef.value?.setOutdent()
                                    activeStyles["outdent"] = !(activeStyles["outdent"] ?: false)
                                },
                                Triple(Icons.AutoMirrored.Filled.FormatAlignLeft, "justifyLeft") {
                                    editorRef.value?.setAlignLeft()
                                    activeStyles["justifyleft"] = !(activeStyles["justifyleft"] ?: false)
                                },
                                Triple(Icons.Default.FormatAlignCenter, "justifyCenter") {
                                    editorRef.value?.setAlignCenter()
                                    activeStyles["justifycenter"] = !(activeStyles["justifycenter"] ?: false)
                                },
                                Triple(Icons.AutoMirrored.Filled.FormatAlignRight, "justifyRight") {
                                    editorRef.value?.setAlignRight()
                                    activeStyles["justifyright"] = !(activeStyles["justifyright"] ?: false)
                                },
                                Triple(Icons.Default.LooksOne, "h1") {
                                    editorRef.value?.setHeading(1)
                                    activeStyles["h1"] = !(activeStyles["h1"] ?: false)
                                },
                                Triple(Icons.Default.LooksTwo, "h2") {
                                    editorRef.value?.setHeading(2)
                                    activeStyles["h2"] = !(activeStyles["h2"] ?: false)
                                },
                                Triple(Icons.Default.Looks3, "h3") {
                                    editorRef.value?.setHeading(3)
                                    activeStyles["h3"] = !(activeStyles["h3"] ?: false)
                                }
                            )
                            buttons.forEach { (icon, desc, action) ->
                                item {
                                    val isActive = activeStyles[desc] == true
                                    IconButton(
                                        onClick = action,
                                        modifier = Modifier.background(
                                            color = if (isActive) Color(0xFF052236) else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    ) {
                                        Icon(icon, contentDescription = desc, tint = Peach)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )

        LaunchedEffect(note) {
            note?.let {
                title = it.title
                content = it.content
                editorRef.value?.setHtml(it.content)
            }
        }

        if (showExitDialog.value) {
            AlertDialog(
                onDismissRequest = { showExitDialog.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog.value = false
                        navController.popBackStack()
                    }) {
                        Text("Exit", color = Peach)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog.value = false }) {
                        Text("Cancel", color = Peach)
                    }
                },
                title = { Text("Unsaved Changes", color = Peach) },
                text = { Text("You have unsaved changes. Are you sure you want to exit?", color = Peach) },
                containerColor = DarkBlue
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Note", color = Peach) },
                text = { Text("Are you sure you want to delete this note?", color = Peach) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteNote()
                            showDeleteDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Delete", color = Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel", color = Peach)
                    }
                },
                containerColor = DarkBlue
            )
        }
    }
}

private fun RichEditor.getCurrentStyles(): List<RichEditor.Type>? {
    return try {
        val field = RichEditor::class.java.getDeclaredField("mCurrentType")
        field.isAccessible = true
        field.get(this) as? List<RichEditor.Type>
    } catch (e: Exception) {
        null
    }
}
