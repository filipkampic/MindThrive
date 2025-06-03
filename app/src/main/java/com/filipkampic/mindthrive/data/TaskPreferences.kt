package com.filipkampic.mindthrive.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.filipkampic.mindthrive.model.tasks.SortDirection
import com.filipkampic.mindthrive.model.tasks.TaskSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "task_prefs")

class TaskPreferences(private val context: Context) {
    object Keys {
        val SORT_OPTION = stringPreferencesKey("sort_option")
        val SORT_DIRECTION = stringPreferencesKey("sort_direction")
        val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    }

    val sortOptionFlow: Flow<TaskSortOption> = context.dataStore.data
        .map { prefs -> prefs[Keys.SORT_OPTION] ?: TaskSortOption.DEFAULT.name }
        .map { TaskSortOption.valueOf(it) }

    val sortDirectionFlow: Flow<SortDirection> = context.dataStore.data
        .map { prefs -> prefs[Keys.SORT_DIRECTION] ?: SortDirection.ASCENDING.name }
        .map { SortDirection.valueOf(it) }

    val showCompletedFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[Keys.SHOW_COMPLETED] ?: true }

    suspend fun saveSortPreferences(option: TaskSortOption, direction: SortDirection) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SORT_OPTION] = option.name
            prefs[Keys.SORT_DIRECTION] = direction.name
        }
    }

    suspend fun saveShowCompleted(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_COMPLETED] = show
        }
    }
}
