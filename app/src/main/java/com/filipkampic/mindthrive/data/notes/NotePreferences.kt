package com.filipkampic.mindthrive.data.notes

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.filipkampic.mindthrive.model.notes.NotesSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.notesDataStore by preferencesDataStore(name = "notes_preferences")

class NotesPreferences(private val context: Context) {

    companion object {
        private val SORT_OPTION_KEY = intPreferencesKey("notes_sort_option")
    }

    suspend fun saveSortOption(option: NotesSortOption) {
        context.notesDataStore.edit { preferences ->
            preferences[SORT_OPTION_KEY] = option.ordinal
        }
    }

    fun getSortOption(): Flow<NotesSortOption> {
        return context.notesDataStore.data.map { preferences ->
            val ordinal = preferences[SORT_OPTION_KEY] ?: NotesSortOption.BY_DATE_DESC.ordinal
            NotesSortOption.entries.toTypedArray().getOrElse(ordinal) { NotesSortOption.BY_DATE_DESC }
        }
    }
}
