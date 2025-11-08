package com.filkom.todolistreactiveapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filkom.todolistreactiveapp.viewmodel.FilterType
import com.filkom.todolistreactiveapp.viewmodel.TodoViewModel

@Composable
fun TodoScreen(
    vm: TodoViewModel = viewModel()
) {
    val todos by vm.todos.collectAsState(initial = emptyList())
    val search by vm.searchQuery.collectAsState()
    val selectedFilter by vm.filter.collectAsState()
    val activeCount by vm.activeCount.collectAsState(initial = 0)
    val doneCount by vm.doneCount.collectAsState(initial = 0)
    var text by rememberSaveable { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tambah tugas...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    vm.addTask(text.trim())
                    text = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Tambah")
        }

        OutlinedTextField(
            value = search,
            onValueChange = { vm.setSearchQuery(it) },
            label = { Text("Cari tugas berdasarkan judul") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterButton(
                text = "Semua",
                active = selectedFilter == FilterType.ALL,
                onClick = { vm.setFilter(FilterType.ALL) }
            )
            FilterButton(
                text = "Aktif",
                active = selectedFilter == FilterType.ACTIVE,
                onClick = { vm.setFilter(FilterType.ACTIVE) }
            )
            FilterButton(
                text = "Selesai",
                active = selectedFilter == FilterType.DONE,
                onClick = { vm.setFilter(FilterType.DONE) }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tugas aktif: $activeCount")
            Text("Tugas selesai: $doneCount")
        }

        Divider()

        LazyColumn {
            items(todos) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.id) },
                    onDelete = { vm.deleteTask(todo.id) }
                )
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val colors =
        if (active) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        else ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)

    Button(
        onClick = onClick,
        colors = colors
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
