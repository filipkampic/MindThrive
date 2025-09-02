package com.filipkampic.mindthrive.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.NoteDao
import com.filipkampic.mindthrive.data.NotesPreferences
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.model.notes.NoteFolder
import com.filipkampic.mindthrive.model.notes.NotesSortOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesViewModel(
    private val noteDao: NoteDao,
    private val preferences: NotesPreferences
) : ViewModel() {
    val folders = noteDao.getAllNoteFolders()
        .map { list -> list.sortedBy { it.name.lowercase() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _notes = MutableStateFlow<List<Note>>(listOf())
    val notes: StateFlow<List<Note>> = _notes

    private val _currentNote = MutableStateFlow<Note?>(null)

    private val selectedFolderId = MutableStateFlow<Int?>(null)

    private val _showAddFolderDialog = MutableStateFlow(false)
    val showAddFolderDialog: StateFlow<Boolean> = _showAddFolderDialog

    private val _showDeleteFolderDialog = MutableStateFlow(false)
    val showDeleteFolderDialog: StateFlow<Boolean> = _showDeleteFolderDialog

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOption = MutableStateFlow(NotesSortOption.BY_DATE_DESC)
    val sortOption: StateFlow<NotesSortOption> = _sortOption

    private val sortedNotes = combine(_notes, _sortOption) { notes, sort ->
        when (sort) {
            NotesSortOption.BY_DATE_ASC -> notes.sortedBy { it.timestamp }
            NotesSortOption.BY_DATE_DESC -> notes.sortedByDescending { it.timestamp }
            NotesSortOption.BY_TITLE_ASC -> notes.sortedBy { it.title.lowercase() }
            NotesSortOption.BY_TITLE_DESC -> notes.sortedByDescending { it.title.lowercase() }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val visibleNotes = combine(sortedNotes, _searchQuery) { notes, query ->
        if (query.isBlank()) notes
        else notes.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            preferences.getSortOption().collect {
                _sortOption.value = it
                loadNotes()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOption(option: NotesSortOption) {
        viewModelScope.launch {
            preferences.saveSortOption(option)
        }
    }

    fun selectFolder(folderId: Int) {
        selectedFolderId.value = folderId
    }

    fun renameFolder(folderId: Int, newName: String) {
        viewModelScope.launch {
            noteDao.updateFolderName(folderId, newName)
        }
    }

    fun addFolder(name: String) {
        viewModelScope.launch {
            val currentFolders = folders.value
            val exists = currentFolders.any { it.name.equals(name, ignoreCase = true) }

            if (!exists) {
                val newFolder = NoteFolder(name = name)
                noteDao.insertNoteFolder(newFolder)
            }
        }
    }

    fun openAddFolderDialog() {
        _showAddFolderDialog.value = true
    }

    fun closeAddFolderDialog() {
        _showAddFolderDialog.value = false
    }

    fun showDeleteFolderDialog() {
        _showDeleteFolderDialog.value = true
    }

    fun hideDeleteFolderDialog() {
        _showDeleteFolderDialog.value = false
    }

    fun deleteFolderOnly(folder: NoteFolder) {
        viewModelScope.launch {
            noteDao.clearFolderIdFromNotes(folder.id)
            noteDao.deleteFolder(folder)
            _showDeleteFolderDialog.value = false
        }
    }

    fun deleteFolderAndNotes(folder: NoteFolder) {
        viewModelScope.launch {
            noteDao.deleteNotesByFolderId(folder.id)
            noteDao.deleteFolder(folder)
            _showDeleteFolderDialog.value = false
        }
    }

    fun loadNotesInFolder(folderId: Int?) {
        viewModelScope.launch {
            val flow = if (folderId == null) {
                noteDao.getNotesWithoutFolder()
            } else {
                noteDao.getNotesByFolderId(folderId)
            }
            flow.collect { notesInFolder ->
                _notes.value = notesInFolder
            }
        }
    }

    fun getNoteById(noteId: Int?): Flow<Note?> {
        return if (noteId == null) flowOf(null)
        else noteDao.getNoteById(noteId).map { note ->
            _currentNote.value = note
            note
        }
    }

    suspend fun upsertNoteAutosave(
        currentId: Int?,
        title: String,
        content: String,
        folderId: Int?
    ): Int {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            if (currentId == null) {
                val newNote = Note(
                    folderId = folderId,
                    title = title,
                    content = content,
                    timestamp = now
                )
                val newId = noteDao.insertNote(newNote).toInt()
                _currentNote.value = newNote.copy(id = newId)
                newId
            } else {
                val existing = noteDao.getNoteByIdOnce(currentId)
                if (existing != null &&
                    existing.title == title &&
                    existing.content == content &&
                    existing.folderId == folderId
                ) {
                    return@withContext currentId
                }
                noteDao.updateNote(
                    Note(
                        id = currentId,
                        folderId = folderId,
                        title = title,
                        content = content,
                        timestamp = now
                    )
                )
                currentId
            }
        }
    }

    fun deleteNote() {
        val currentNote = _currentNote.value
        if (currentNote != null) {
            viewModelScope.launch {
                noteDao.deleteNote(currentNote)
            }
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            noteDao.getAllNotes().collect { allNotes ->
                val sortedNotes = when (_sortOption.value) {
                    NotesSortOption.BY_DATE_DESC -> allNotes.sortedByDescending { it.timestamp }
                    NotesSortOption.BY_DATE_ASC -> allNotes.sortedBy { it.timestamp }
                    NotesSortOption.BY_TITLE_ASC -> allNotes.sortedBy { it.title.lowercase() }
                    NotesSortOption.BY_TITLE_DESC -> allNotes.sortedByDescending { it.title.lowercase() }
                }
                _notes.value = sortedNotes
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class NotesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application)
        val preferences = NotesPreferences(application)
        return NotesViewModel(database.noteDao(), preferences) as T
    }
}