package com.filipkampic.mindthrive.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class FocusViewModel : ViewModel() {
    var pomodoroDurationIndex by mutableStateOf(3)
    var pomodoroSessionIndex by mutableStateOf(3)
    var pomodoroBreakIndex by mutableStateOf(4)
    var pomodoroSessionPlans = mutableStateListOf<String>()
    var pomodoroPlannedActivities by mutableStateOf<List<String>?>(null)

    var stopwatchActivityName by mutableStateOf("")
}