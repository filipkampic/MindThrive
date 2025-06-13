package com.filipkampic.mindthrive.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.alarmDataStore by preferencesDataStore(name = "alarm_prefs")

class AlarmPreferences(private val context: Context) {
    companion object {
        private val ALARM_ENABLED_KEY = booleanPreferencesKey("alarm_enabled")
    }

    val isAlarmEnabledFlow: Flow<Boolean> = context.alarmDataStore.data
        .map { prefs -> prefs[ALARM_ENABLED_KEY] ?: true }

    suspend fun saveAlarmEnabled(enabled: Boolean) {
        context.alarmDataStore.edit { prefs ->
            prefs[ALARM_ENABLED_KEY] = enabled
        }
    }
}