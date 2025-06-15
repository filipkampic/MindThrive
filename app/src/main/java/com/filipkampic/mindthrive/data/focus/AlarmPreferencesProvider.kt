package com.filipkampic.mindthrive.data.focus

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context._alarmDataStore: DataStore<Preferences> by preferencesDataStore(name = "alarm")

object AlarmPreferencesProvider {
    fun getDataStore(context: Context): DataStore<Preferences> {
        return context._alarmDataStore
    }
}