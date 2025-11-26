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

    private var allSittersList: List<Sitter> = emptyList()

    private var currentFilter: String = "Semua"

    private val _selectedSitter = MutableStateFlow<Sitter?>(null)
    val selectedSitter: StateFlow<Sitter?> = _selectedSitter.asStateFlow()

    private val _bookedSlots = MutableStateFlow<List<String>>(emptyList())
    val bookedSlots: StateFlow<List<String>> = _bookedSlots.asStateFlow()

    init {
        fetchSitters()
    }

    private fun fetchSitters() {
        viewModelScope.launch {
            repository.getAllSittersFlow().collect { list ->
                allSittersList = list
                applyFilter(currentFilter)
            }
        }
    }

    fun applyFilter(filterType: String) {
        currentFilter = filterType

        if (filterType == "Semua") {
            _sitters.value = allSittersList
        } else {
            _sitters.value = allSittersList.filter { sitter ->
                sitter.packages.contains(filterType)
            }
        }
    }

    fun fetchBookedSlots(sitterId: String, dateFullString: String) {
        viewModelScope.launch {
            _bookedSlots.value = emptyList()
            val slots = repository.getBookedTimes(sitterId, dateFullString)
            _bookedSlots.value = slots
        }
    }

    // Memilih sitter untuk ditampilkan di Detail
    fun selectSitter(sitter: Sitter) {
        _selectedSitter.value = sitter
    }
}