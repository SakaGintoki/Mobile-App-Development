package com.filkom.designimplementation.viewmodel.feature.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.ConsultationRepository
import com.filkom.designimplementation.model.data.consultation.ChatMessageDoctor
import com.filkom.designimplementation.model.data.consultation.Doctor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ConsultationViewModel : ViewModel() {
    private val repository = ConsultationRepository()

    private val _selectedDoctor = MutableStateFlow<Doctor?>(null)
    val selectedDoctor: StateFlow<Doctor?> = _selectedDoctor.asStateFlow()

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _bookedSlots = MutableStateFlow<List<String>>(emptyList())
    val bookedSlots: StateFlow<List<String>> = _bookedSlots.asStateFlow()

    private var allDoctorsList: List<Doctor> = emptyList()
    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()
    private val _chatMessages = MutableStateFlow<List<ChatMessageDoctor>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageDoctor>> = _chatMessages.asStateFlow()
    private var currentCategoryFilter = "Semua"

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
            repository.getAllDoctorsFlow().collect { list ->
                allDoctorsList = list
                _doctors.value = list
                _isLoading.value = false
            }
        }
    }

    fun searchDoctors(query: String) {
        if (query.isBlank()) {
            _doctors.value = allDoctorsList
            return
        }

        val lowerCaseQuery = query.lowercase()

        // Filter berdasarkan Nama ATAU Spesialisasi
        val filteredList = allDoctorsList.filter { doctor ->
            doctor.name.lowercase().contains(lowerCaseQuery) ||
                    doctor.specialization.lowercase().contains(lowerCaseQuery)
        }

        _doctors.value = filteredList
    }

    fun fetchBookedSlots(doctorId: String, dateFullString: String) {
        viewModelScope.launch {
            _bookedSlots.value = emptyList()
            val slots = repository.getBookedTimes(doctorId, dateFullString)
            _bookedSlots.value = slots
        }
    }

    fun checkActiveSession(doctorId: String, userId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val locale = Locale("id", "ID")

            val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", locale)
            val currentDate = dateFormat.format(calendar.time)

            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val bookedTimesToday = repository.getBookedTimes(doctorId, currentDate)

            var isActive = false

            for (time in bookedTimesToday) {
                val bookedHour = time.split(".").firstOrNull()?.toIntOrNull() ?: -1

                if (currentHour == bookedHour) {
                    isActive = true
                    break
                }
            }
            onResult(isActive)
        }
    }

//    fun checkActiveSession(doctorId: String, userId: String, onResult: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            val calendar = Calendar.getInstance()
//            val locale = Locale("id", "ID")
//
//            val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", locale)
//            val currentDate = dateFormat.format(calendar.time)
//
//            val bookedTimesToday = repository.getBookedTimes(doctorId, currentDate)
//
//            val isActive = bookedTimesToday.isNotEmpty()
//
//            onResult(isActive)
//        }
//    }

    fun loadChatSession(doctorId: String, userId: String) {
        viewModelScope.launch {
            // A. Ambil Pesan Realtime
            repository.getChatMessagesFlow(userId, doctorId).collect { msgs ->
                _chatMessages.value = msgs
            }
        }
        checkSessionValidity(doctorId)
    }

    private fun checkSessionValidity(doctorId: String) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val locale = Locale("id", "ID")
            val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", locale)
            val currentDate = dateFormat.format(calendar.time)
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

            val bookedTimesToday = repository.getBookedTimes(doctorId, currentDate)

            var isActive = false
            for (time in bookedTimesToday) {
                val bookedHour = time.split(".").firstOrNull()?.toIntOrNull() ?: -1
                if (currentHour == bookedHour) {
                    isActive = true
                    break
                }
            }
            _isSessionActive.value = isActive
        }
    }

    fun sendChatMessage(doctorId: String, userId: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(userId, doctorId, text)
        }
    }

    fun checkBookingHistory(doctorId: String, userId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val hasHistory = repository.hasBookingHistory(doctorId, userId)
            onResult(hasHistory)
        }
    }

    fun sendChatMessage(doctorId: String, userId: String, text: String, doctorName: String, doctorSpecialization: String) { // Tambah param doctorName
        viewModelScope.launch {
            repository.sendMessage(userId, doctorId, text)

            kotlinx.coroutines.delay(2000)

            repository.autoReplyAsDoctor(userId, doctorId, doctorName, doctorSpecialization, text)        }
    }

    fun filterDoctorsByCategory(category: String) {
        currentCategoryFilter = category

        val filteredList = if (category == "Semua") {
            allDoctorsList
        } else if (category == "Lainnya") {
            allDoctorsList.filter {
                it.specialization != "Dokter Umum" &&
                        it.specialization != "Dokter Anak" &&
                        it.specialization != "Kandungan"
            }
        } else {
            val dbSpecialization = when(category) {
                "Umum" -> "Dokter Umum"
                "Anak" -> "Dokter Anak"
                else -> category
            }

            allDoctorsList.filter { it.specialization == dbSpecialization }
        }

        _doctors.value = filteredList
    }
}