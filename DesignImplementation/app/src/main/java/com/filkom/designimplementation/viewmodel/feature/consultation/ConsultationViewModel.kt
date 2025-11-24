package com.filkom.designimplementation.viewmodel.feature.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.ConsultationRepository
import com.filkom.designimplementation.model.data.consultation.Doctor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultationViewModel : ViewModel() {
    private val repository = ConsultationRepository()

    // State untuk Detail Dokter yang dipilih
    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> = _selectedDoctor.asStateFlow()

    // State untuk List Dokter (Rekomendasi)
    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getDoctorDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val doctor = repository.getDoctorById(id)
            _selectedDoctor.value = doctor
            _isLoading.value = false
        }
    }

    fun fetchDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            val list = repository.getAllDoctors()
            _doctors.value = list
            _isLoading.value = false
        }
    }
}