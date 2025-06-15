package com.filipkampic.mindthrive.data.focus

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.filipkampic.mindthrive.model.focus.FocusEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val Context.focusDataStore by preferencesDataStore(name = "focus_preferences")

class FocusPreferences(private val context: Context) {

    companion object {
        private val TOTAL_FOCUS_SECONDS = intPreferencesKey("total_focus_seconds")
    }

    private val FOCUS_ENTRIES = stringPreferencesKey("focus_entries")

    val totalFocusFlow: Flow<Int> = context.focusDataStore.data.map { prefs ->
        prefs[TOTAL_FOCUS_SECONDS] ?: 0
    }

    val focusEntriesFlow: Flow<List<FocusEntry>> = context.focusDataStore.data.map { prefs ->
        val json = prefs[FOCUS_ENTRIES]
        if (json.isNullOrEmpty()) emptyList()
        else {
            val type = object : TypeToken<List<FocusEntry>>() {}.type
            Gson().fromJson<List<FocusEntry>>(json, type)
        }
    }

    suspend fun addFocusSession(durationSeconds: Int) {
        context.focusDataStore.edit { prefs ->
            val current = prefs[TOTAL_FOCUS_SECONDS] ?: 0
            prefs[TOTAL_FOCUS_SECONDS] = current + durationSeconds
        }
    }

    suspend fun saveFocusEntry(entry: FocusEntry) {
        context.focusDataStore.edit { prefs ->
            val currentJson = prefs[FOCUS_ENTRIES]
            val type = object : TypeToken<MutableList<FocusEntry>>() {}.type
            val currentList: MutableList<FocusEntry> = if (currentJson != null) {
                Gson().fromJson(currentJson, type)
            } else mutableListOf()
            currentList += entry
            prefs[FOCUS_ENTRIES] = Gson().toJson(currentList)
        }
    }
}