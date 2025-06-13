package com.filipkampic.mindthrive.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.AlarmPreferences
import kotlinx.coroutines.launch

class FocusViewModel : ViewModel() {
    var pomodoroDurationIndex by mutableStateOf(3)
    var pomodoroSessionIndex by mutableStateOf(3)
    var pomodoroBreakIndex by mutableStateOf(4)
    var pomodoroSessionPlans = mutableStateListOf<String>()
    var pomodoroPlannedActivities by mutableStateOf<List<String>?>(null)

    var stopwatchActivityName by mutableStateOf("")

    var isAlarmEnabled by mutableStateOf(true)

    fun observeAlarmEnabled(context: Context) {
        val alarmPrefs = AlarmPreferences(context)
        viewModelScope.launch {
            alarmPrefs.isAlarmEnabledFlow.collect {
                isAlarmEnabled = it
            }
        }
    }

    fun saveAlarmEnabled(context: Context, enabled: Boolean) {
        val alarmPrefs = AlarmPreferences(context)
        viewModelScope.launch {
            alarmPrefs.saveAlarmEnabled(enabled)
        }
    }
}