package com.example.to_doapp.userinterface

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TaskDatabase
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val db = TaskDatabase.getDatabase(application)
    private val dao = db.taskDao()

    val tasks = dao.getAll().asLiveData()

    fun addTask(title: String) {
        viewModelScope.launch {
            dao.insert(Task(title = title))
        }
    }

    fun toggleDone(task: Task) {
        viewModelScope.launch {
            dao.update(task.copy(isDone = !task.isDone))
        }
    }

    fun updateTaskTitle(task: Task, newTitle: String) {
        viewModelScope.launch {
            dao.update(task.copy(title = newTitle))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }
}

class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
