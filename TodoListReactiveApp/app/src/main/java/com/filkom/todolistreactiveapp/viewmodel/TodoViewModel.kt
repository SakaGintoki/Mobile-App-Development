package com.filkom.todolistreactiveapp.viewmodel

import androidx.lifecycle.ViewModel
import com.filkom.todolistreactiveapp.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

enum class FilterType { ALL, ACTIVE, DONE }

class TodoViewModel : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    private val _filter = MutableStateFlow(FilterType.ALL)
    private val _searchQuery = MutableStateFlow("")

    val todos = combine(_todos, _filter, _searchQuery) { todos, filter, query ->
        val filteredByStatus = when (filter) {
            FilterType.ALL -> todos
            FilterType.ACTIVE -> todos.filter { !it.isDone }
            FilterType.DONE -> todos.filter { it.isDone }
        }

        if (query.isBlank()) {
            filteredByStatus
        } else {
            filteredByStatus.filter { todo ->
                todo.title.contains(query, ignoreCase = true)
            }
        }
    }

    val activeCount = _todos.map { list -> list.count { !it.isDone } }
    val doneCount   = _todos.map { list -> list.count { it.isDone } }

    val searchQuery: StateFlow<String> = _searchQuery
    val filter: StateFlow<FilterType> = _filter

    fun setFilter(type: FilterType) {
        _filter.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addTask(title: String) {
        val nextId = (_todos.value.maxOfOrNull { it.id } ?: 0) + 1
        val newTask = Todo(id = nextId, title = title)
        _todos.value = _todos.value + newTask
    }

    fun toggleTask(id: Int) {
        _todos.value = _todos.value.map { t ->
            if (t.id == id) t.copy(isDone = !t.isDone) else t
        }
    }

    fun deleteTask(id: Int) {
        _todos.value = _todos.value.filterNot { it.id == id }
    }
}
