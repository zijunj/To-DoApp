package com.example.to_doapp.userinterface

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.to_doapp.data.Task


@Composable
fun TaskScreen() {
    val context = LocalContext.current.applicationContext as Application
    val factory = remember { TaskViewModelFactory(context) }
    val viewModel: TaskViewModel = viewModel(factory = factory)

    val tasks by viewModel.tasks.observeAsState(emptyList())
    var newTaskText by remember { mutableStateOf("") }

    // Track edit mode
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var editedText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            TextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                label = { Text("New Task") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (newTaskText.isNotBlank()) {
                    viewModel.addTask(newTaskText)
                    newTaskText = ""
                }
            }) {
                Text("Add")
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                if (editingTask?.id == task.id) {
                    // Edit Mode UI
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        TextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            if (editedText.isNotBlank()) {
                                viewModel.updateTaskTitle(task, editedText)
                                editingTask = null
                                editedText = ""
                            }
                        }) {
                            Text("Save")
                        }
                    }
                } else {
                    // Normal Task View
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { viewModel.toggleDone(task) }
                        )
                        Text(
                            text = task.title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                        )
                        IconButton(onClick = {
                            editingTask = task
                            editedText = task.title
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.deleteTask(task) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}