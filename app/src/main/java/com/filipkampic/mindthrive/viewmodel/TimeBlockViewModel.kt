package com.filipkampic.mindthrive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.notification.timeManagement.cancelTimeBlockNotification
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.model.timeManagement.TimeBlock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.filipkampic.mindthrive.notification.timeManagement.scheduleTimeBlockNotification
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeBlockViewModel(application: Application) : AndroidViewModel(application) {
    private val timeBlockDao = AppDatabase.getDatabase(application).timeBlockDao()
    private val _timeBlocks = MutableStateFlow<List<TimeBlock>>(emptyList())
    private val _currentDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val todaysTimeBlocks: StateFlow<List<TimeBlock>> = _currentDate
        .flatMapLatest { date ->
            val start = LocalDateTime.of(date, LocalTime.MIN)
            val end = LocalDateTime.of(date, LocalTime.MAX)
            timeBlockDao
                .getTimeBlocksInRange(start.toString(), end.toString())
                .map { list ->
                    splitTimeBlocksByDay(list, date).sortedBy { it.start }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadTimeBlocks(LocalDate.now())
    }

    fun loadTimeBlocks(date: LocalDate) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val startOfDay = LocalDateTime.of(date, LocalTime.MIN).format(formatter)
        val endOfDay = LocalDateTime.of(date, LocalTime.MAX).format(formatter)

        viewModelScope.launch {
            timeBlockDao.getTimeBlocksByDate(date, startOfDay, endOfDay).collectLatest { fetchedTimeBlocks ->
                _timeBlocks.value = splitTimeBlocksByDay(fetchedTimeBlocks, date)
            }
        }
    }

    private fun splitTimeBlocksByDay(timeBlocks: List<TimeBlock>, date: LocalDate): List<TimeBlock> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(23, 59)

        return timeBlocks.mapNotNull { timeBlock ->
            val overlapStart = maxOf(timeBlock.start ?: return@mapNotNull null, startOfDay)
            val overlapEnd = minOf(timeBlock.end ?: return@mapNotNull null, endOfDay)

            if (overlapStart < overlapEnd) {
                TimeBlock(
                    id = timeBlock.id,
                    name = timeBlock.name,
                    description = timeBlock.description,
                    start = overlapStart,
                    end = overlapEnd,
                    date = date
                )
            } else {
                null
            }
        }
    }

    fun insertTimeBlock(timeBlock: TimeBlock) {
        viewModelScope.launch {
            timeBlockDao.insertTimeBlock(timeBlock)
            scheduleTimeBlockNotification(getApplication(), timeBlock)
        }
    }

    fun updateTimeBlock(timeBlock: TimeBlock) {
        viewModelScope.launch {
            timeBlockDao.updateTimeBlock(timeBlock)
            scheduleTimeBlockNotification(getApplication(), timeBlock)
        }
    }

    fun deleteTimeBlock(timeBlock: TimeBlock) {
        viewModelScope.launch {
            cancelTimeBlockNotification(getApplication(), timeBlock.id)
            timeBlockDao.deleteTimeBlock(timeBlock)
        }
    }

    fun setDate(date: LocalDate) {
        _currentDate.value = date
    }
}

class TimeBlockViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeBlockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimeBlockViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}