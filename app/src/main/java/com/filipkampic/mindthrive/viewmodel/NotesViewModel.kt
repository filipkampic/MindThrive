package com.filipkampic.mindthrive.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.data.NoteDao
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.model.notes.NoteFolder
import com.filipkampic.mindthrive.model.notes.NotesSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotesViewModel(private val noteDao: NoteDao) : ViewModel() {
    private val _folders = MutableStateFlow<List<NoteFolder>>(listOf())
    val folders: StateFlow<List<NoteFolder>> = _folders

    private val _notes = MutableStateFlow<List<Note>>(listOf())
    val notes: StateFlow<List<Note>> = _notes

    val selectedFolderId = MutableStateFlow<Int?>(null)
    val searchQuery = MutableStateFlow("")
    val sortOption = MutableStateFlow(NotesSortOption.BY_DATE_DESC)

    init {
        viewModelScope.launch {
            noteDao.getAllNotes().collect { noteList ->
                _notes.value = noteList
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun updateSortOption(option: NotesSortOption) {
        sortOption.value = option
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
}

@Suppress("UNCHECKED_CAST")
class NotesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(application)
        val noteDao = database.noteDao()
        return NotesViewModel(noteDao) as T
    }
}