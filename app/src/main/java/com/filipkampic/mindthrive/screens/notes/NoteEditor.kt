package com.filipkampic.mindthrive.screens.notes

import android.annotation.SuppressLint
import android.app.Application
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("RememberReturnType", "ClickableViewAccessibility")
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun NoteEditor(
    navController: NavController,
    noteId: Int?
) {
    val context = LocalContext.current
    val viewModel: NotesViewModel = viewModel(factory = NotesViewModelFactory(context.applicationContext as Application))
    val note by viewModel.getNoteById(noteId).collectAsState(initial = null)
    val folders by viewModel.folders.collectAsState()

    var selectedFolderId by remember { mutableStateOf<Int?>(null) }
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var editingNoteId by remember { mutableStateOf(noteId) }
    var baseline by remember { mutableStateOf(Triple("", "", null as Int?)) }
    var hasBaseline by remember { mutableStateOf(false) }

    val editorRef = remember { mutableStateOf<RichEditor?>(null) }
    val activeStyles = remember { mutableStateMapOf<String, Boolean>() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val titleFocus = remember { MutableInteractionSource() }
    val contentFocus = remember { MutableInteractionSource() }
    val titleFocused by titleFocus.collectIsFocusedAsState()
    val contentFocused by contentFocus.collectIsFocusedAsState()
    var isContentEditing by remember { mutableStateOf(false) }
    var isEditing = titleFocused || contentFocused || isContentEditing

    var folderExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = Peach,
        backgroundColor = Peach.copy(alpha = 0.4f)
    )

    LaunchedEffect(Unit) {
        snapshotFlow { Triple(title, content, selectedFolderId) }
            .debounce(500)
            .distinctUntilChanged()
            .collectLatest { (title, content, folderId) ->
                if (!hasBaseline) return@collectLatest
                if (Triple(title, content, folderId) == baseline) return@collectLatest
                if (title.isBlank() && content.isBlank()) return@collectLatest

                val id = viewModel.upsertNoteAutosave(editingNoteId, title, content, folderId)
                editingNoteId = id
                baseline = Triple(title, content, folderId)
            }
    }

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        LaunchedEffect(note) {
            if (note != null && selectedFolderId == null) {
                selectedFolderId = note!!.folderId
            }
        }

        val scrollState = rememberScrollState()
        LaunchedEffect(content) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }

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
                        IconButton(onClick = { navController.popBackStack() }) {
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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Peach, RoundedCornerShape(8.dp))
                            .clickable { folderExpanded = true }
                            .padding(12.dp)
                    ) {
                        val selectedFolderName = folders.find { it.id == selectedFolderId }?.name ?: "Select folder"

                        Text(
                            text = selectedFolderName,
                            color = DarkBlue
                        )

                        DropdownMenu(
                            expanded = folderExpanded,
                            onDismissRequest = { folderExpanded = false },
                            modifier = Modifier.background(Peach)
                        ) {
                            DropdownMenuItem(
                                text = { Text("No folder", color = DarkBlue) },
                                onClick = {
                                    selectedFolderId = null
                                    folderExpanded = false
                                },
                                colors = MenuDefaults.itemColors(textColor = DarkBlue),
                                modifier = Modifier.background(Peach)
                            )
                            folders.forEach { folder ->
                                DropdownMenuItem(
                                    text = { Text(folder.name, color = DarkBlue) },
                                    onClick = {
                                        selectedFolderId = folder.id
                                        folderExpanded = false
                                    },
                                    modifier = Modifier.background(Peach),
                                    colors = MenuDefaults.itemColors(textColor = DarkBlue)
                                )
                            }
                        }
                    }

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
                                        post { refreshDecorations() }
                                    }
                                    setOnFocusChangeListener { _, hasFocus ->
                                        isContentEditing = hasFocus
                                        if (hasFocus) post { refreshDecorations() }
                                    }
                                    setOnDecorationChangeListener { _, types ->
                                        activeStyles.clear()
                                        types?.forEach { type ->
                                            activeStyles[type.name.lowercase()] = true
                                        }
                                        if (activeStyles["justifyleft"] != true &&
                                            activeStyles["justifycenter"] != true &&
                                            activeStyles["justifyright"]  != true) {
                                            activeStyles["justifyleft"] = true
                                        }
                                        if (activeStyles["unorderedlist"] == true) activeStyles.remove("orderedlist")
                                        if (activeStyles["orderedlist"]   == true) activeStyles.remove("unorderedlist")
                                    }

                                    setOnClickListener {
                                        post { refreshDecorations() }
                                    }
                                    setOnTouchListener { v, event ->
                                        if (event.action == MotionEvent.ACTION_UP) {
                                            v.post { refreshDecorations() }
                                        }
                                        false
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
                            val buttons: List<Triple<ImageVector, String, () -> Unit>> = listOf(
                                Triple(Icons.Default.FormatBold, "bold") {
                                    editorRef.value?.apply {
                                        setBold()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.FormatItalic, "italic") {
                                    editorRef.value?.apply {
                                        setItalic()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.FormatUnderlined, "underline") {
                                    editorRef.value?.apply {
                                        setUnderline()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.FormatStrikethrough, "strikethrough") {
                                    editorRef.value?.apply {
                                        setStrikeThrough()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.AutoMirrored.Filled.FormatListBulleted, "unorderedlist") {
                                    editorRef.value?.apply {
                                        setBullets()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.FormatListNumbered, "orderedlist") {
                                    editorRef.value?.apply {
                                        setNumbers()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.SubdirectoryArrowRight, "indent") {
                                    editorRef.value?.apply {
                                        setIndent()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.HorizontalRule, "outdent") {
                                    editorRef.value?.apply {
                                        setOutdent()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.AutoMirrored.Filled.FormatAlignLeft, "justifyLeft") {
                                    editorRef.value?.apply {
                                        setAlignLeft()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.FormatAlignCenter, "justifyCenter") {
                                    editorRef.value?.apply {
                                        setAlignCenter()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.AutoMirrored.Filled.FormatAlignRight, "justifyRight") {
                                    editorRef.value?.apply {
                                        setAlignRight()
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.LooksOne, "h1") {
                                    editorRef.value?.apply {
                                        if (activeStyles["h1"] == true) setParagraph() else setHeading(1)
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.LooksTwo, "h2") {
                                    editorRef.value?.apply {
                                        if (activeStyles["h2"] == true) setParagraph() else setHeading(2)
                                        refreshDecorations()
                                    }
                                },
                                Triple(Icons.Default.Looks3, "h3") {
                                    editorRef.value?.apply {
                                        if (activeStyles["h3"] == true) setParagraph() else setHeading(3)
                                        refreshDecorations()
                                    }
                                }
                            )
                            buttons.forEach { (icon, desc, action) ->
                                item {
                                    val isActive = when (desc) {
                                        "justifyLeft" -> {
                                            val anyAlign = activeStyles["justifyleft"] == true ||
                                                    activeStyles["justifycenter"] == true ||
                                                    activeStyles["justifyright"] == true
                                            if (!anyAlign) true else activeStyles["justifyleft"] == true
                                        }
                                        "justifyCenter" -> activeStyles["justifycenter"] == true
                                        "justifyRight"  -> activeStyles["justifyright"] == true
                                        else -> activeStyles[desc] == true
                                    }

                                    IconButton(
                                        onClick = { action() },
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

        LaunchedEffect(note?.id) {
            note?.let { noteVal ->
                title = noteVal.title
                content = noteVal.content
                editingNoteId = noteVal.id
                baseline = Triple(noteVal.title, noteVal.content, noteVal.folderId)
                hasBaseline = true
                editorRef.value?.let { editor ->
                    editor.setHtml(noteVal.content)
                    editor.post { editor.refreshDecorations() }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Note", color = DarkBlue) },
                text = { Text("Are you sure you want to delete this note?", color = DarkBlue.copy(alpha = 0.8f)) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteNote()
                            showDeleteDialog = false
                            navController.popBackStack()
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
                        onClick = { showDeleteDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = DarkBlue
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Peach,
                titleContentColor = DarkBlue,
                textContentColor = DarkBlue
            )
        }
    }
}

private fun RichEditor.refreshDecorations() {
    this.loadUrl("javascript:RE.enabledEditingItems();")
}

private fun RichEditor.setParagraph() {
    this.loadUrl("javascript:document.execCommand('formatBlock', false, 'p'); RE.enabledEditingItems();")
}
