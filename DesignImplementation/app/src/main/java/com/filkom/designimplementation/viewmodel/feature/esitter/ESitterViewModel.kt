package com.filkom.designimplementation.viewmodel.feature.esitter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.SitterRepository
import com.filkom.designimplementation.model.data.sitter.Sitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ESitterViewModel : ViewModel() {
    private val repository = SitterRepository()

    private val _sitters = MutableStateFlow<List<Sitter>>(emptyList())
    val sitters: StateFlow<List<Sitter>> = _sitters.asStateFlow()

    private val _selectedSitter = MutableStateFlow<Sitter?>(null)
    val selectedSitter: StateFlow<Sitter?> = _selectedSitter.asStateFlow()

    init {
        fetchSitters()
    }

    private fun fetchSitters() {
        viewModelScope.launch {
            repository.getAllSittersFlow().collect {
                _sitters.value = it
            }
        }
    }

    fun selectSitter(sitter: Sitter) {
        _selectedSitter.value = sitter
    }
}