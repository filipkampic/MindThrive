package com.filipkampic.mindthrive.data.focus

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmPreferences(val context: Context) {
    val alarmDataStore = AlarmPreferencesProvider.getDataStore(context)

    companion object {
        private val ALARM_ENABLED_KEY = booleanPreferencesKey("alarm_enabled")
    }

    val isAlarmEnabledFlow: Flow<Boolean> = alarmDataStore.data
        .map { prefs -> prefs[ALARM_ENABLED_KEY] ?: true }

    suspend fun saveAlarmEnabled(enabled: Boolean) {
        alarmDataStore.edit { prefs ->
            prefs[ALARM_ENABLED_KEY] = enabled
        }
    }
}