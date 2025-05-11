import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filipkampic.mindthrive.data.AppDatabase
import com.filipkampic.mindthrive.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        loadTasks(LocalDate.now())
    }

    fun loadTasks(date: LocalDate) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val startOfDay = LocalDateTime.of(date, LocalTime.MIN).format(formatter)
        val endOfDay = LocalDateTime.of(date, LocalTime.MAX).format(formatter)

        viewModelScope.launch {
            taskDao.getTasksByDate(date, startOfDay, endOfDay).collectLatest { fetchedTasks ->
                _tasks.value = splitTasksByDay(fetchedTasks, date)
            }
        }
    }

    private fun splitTasksByDay(tasks: List<Task>, date: LocalDate): List<Task> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()

        return tasks.mapNotNull { task ->
            val overlapStart = maxOf(task.start ?: return@mapNotNull null, startOfDay)
            val overlapEnd = minOf(task.end ?: return@mapNotNull null, endOfDay)

            if (overlapStart < overlapEnd) {
                Task(
                    id = task.id,
                    name = task.name ?: "",
                    description = task.description ?: "",
                    start = overlapStart,
                    end = overlapEnd,
                    date = date
                )
            } else {
                null
            }
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskDao.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }
}

class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED.Cast")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}