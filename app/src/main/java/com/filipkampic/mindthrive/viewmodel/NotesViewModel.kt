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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val noteDao: NoteDao,
    private val preferences: NotesPreferences
) : ViewModel() {
    private val _folders = MutableStateFlow<List<NoteFolder>>(listOf())
    val folders: StateFlow<List<NoteFolder>> = _folders

    private val _notes = MutableStateFlow<List<Note>>(listOf())
    val notes: StateFlow<List<Note>> = _notes

    val selectedFolderId = MutableStateFlow<Int?>(null)
    val searchQuery = MutableStateFlow("")

    private val _sortOption = MutableStateFlow(NotesSortOption.BY_DATE_DESC)
    val sortOption: StateFlow<NotesSortOption> = _sortOption

    val sortedNotes = combine(_notes, _sortOption) { notes, sort ->
        when (sort) {
            NotesSortOption.BY_DATE_ASC -> notes.sortedBy { it.timestamp }
            NotesSortOption.BY_DATE_DESC -> notes.sortedByDescending { it.timestamp }
            NotesSortOption.BY_TITLE_ASC -> notes.sortedBy { it.title.lowercase() }
            NotesSortOption.BY_TITLE_DESC -> notes.sortedByDescending { it.title.lowercase() }
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
        searchQuery.value = query
    }

    fun updateSortOption(option: NotesSortOption) {
        viewModelScope.launch {
            preferences.saveSortOption(option)
        }
    }

    fun selectFolder(folderId: Int) {
        selectedFolderId.value = folderId
    }

    fun showAddFolderDialog() {
        /* TO-DO */
    }

    fun getNoteById(noteId: Int?): Flow<Note?> {
        return if (noteId == null) flowOf(null)
        else notes.map { list -> list.find { it.id == noteId }}
    }

    fun saveNote(noteId: Int?, title: String, content: String) {
        viewModelScope.launch {
            if (noteId == null) {
                val newNote = Note(
                    folderId = null,
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
                noteDao.insertNote(newNote)
            } else {
                val updatedNote = Note(
                    id = noteId,
                    folderId = null,
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
                noteDao.updateNote(updatedNote)
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