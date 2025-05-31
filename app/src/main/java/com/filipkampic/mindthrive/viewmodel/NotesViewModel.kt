package com.filipkampic.mindthrive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.model.notes.NoteFolder
import com.filipkampic.mindthrive.model.notes.NotesSortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val _folders = MutableStateFlow<List<NoteFolder>>(listOf())
    val folders: StateFlow<List<NoteFolder>> = _folders

    private val _notes = MutableStateFlow<List<Note>>(listOf())
    val notes: StateFlow<List<Note>> = _notes

    val selectedFolderId = MutableStateFlow<Int?>(null)
    val searchQuery = MutableStateFlow("")
    val sortOption = MutableStateFlow(NotesSortOption.BY_DATE_DESC)

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
}

@Suppress("UNCHECKED_CAST")
class NotesViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}