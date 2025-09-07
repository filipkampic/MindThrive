package com.filipkampic.mindthrive.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.focus.AlarmPreferences
import com.filipkampic.mindthrive.data.focus.FocusPreferences
import com.filipkampic.mindthrive.model.focus.FocusEntry
import com.filipkampic.mindthrive.model.focus.FocusPeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FocusViewModel(application: Application) : AndroidViewModel(application) {
    private val focusPreferences = FocusPreferences(getApplication<Application>().applicationContext)
    val totalFocusFromPrefs: Flow<Int> = focusPreferences.totalFocusFlow

    private val _focusEntriesState = mutableStateOf<List<FocusEntry>>(emptyList())
    val focusEntries: State<List<FocusEntry>> = _focusEntriesState

    var pomodoroDurationIndex by mutableStateOf(3)
    var pomodoroSessionIndex by mutableStateOf(3)
    var pomodoroBreakIndex by mutableStateOf(4)
    var pomodoroSessionPlans = mutableStateListOf<String>()
    var pomodoroPlannedActivities by mutableStateOf<List<String>?>(null)

    var stopwatchActivityName by mutableStateOf("")

    var isAlarmEnabled by mutableStateOf(true)

    init {
        viewModelScope.launch {
            focusPreferences.focusEntriesFlow.collect { savedEntries ->
                _focusEntriesState.value = savedEntries
            }
        }
    }

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

    fun logFocusSession(duration: Int) {
        val newEntry = FocusEntry(System.currentTimeMillis(), duration)
        _focusEntriesState.value += newEntry

        viewModelScope.launch {
            focusPreferences.saveFocusEntry(newEntry)
        }
    }

    fun getFocusPerDay(period: FocusPeriod): Map<String, Int> {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        val dateFormat = when (period) {
            FocusPeriod.WEEK -> SimpleDateFormat("EEE", Locale.ENGLISH)
            FocusPeriod.MONTH -> SimpleDateFormat("d", Locale.ENGLISH)
            FocusPeriod.YEAR -> SimpleDateFormat("MMM", Locale.ENGLISH)
        }

        val timeFrame = when (period) {
            FocusPeriod.WEEK -> 7L * 24 * 60 * 60 * 1000
            FocusPeriod.MONTH -> 30L * 24 * 60 * 60 * 1000
            FocusPeriod.YEAR -> 365L * 24 * 60 * 60 * 1000
        }

        return focusEntries.value
            .filter { it.timestamp >= now - timeFrame }
            .groupBy { dateFormat.format(Date(it.timestamp)) }
            .mapValues { entry -> entry.value.sumOf { it.durationSeconds } }
    }

    fun addFocusToPreferences(seconds: Int) {
        viewModelScope.launch {
            focusPreferences.addFocusSession(seconds)
        }
    }
}