package com.filkom.designimplementation.viewmodel.feature.daycare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.DaycareRepository
import com.filkom.designimplementation.model.data.daycare.Daycare
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DaycareViewModel : ViewModel() {
    private val repository = DaycareRepository()

    private val _daycares = MutableStateFlow<List<Daycare>>(emptyList())
    val daycares: StateFlow<List<Daycare>> = _daycares.asStateFlow()

    private val _selectedDaycare = MutableStateFlow<Daycare?>(null)
    val selectedDaycare: StateFlow<Daycare?> = _selectedDaycare.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchDaycares() {
        viewModelScope.launch {
            _isLoading.value = true
            _daycares.value = repository.getAllDaycares()
            _isLoading.value = false
        }
    }

    fun getDaycareDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedDaycare.value = repository.getDaycareById(id)
            _isLoading.value = false
        }
    }
}