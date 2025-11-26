package com.filkom.designimplementation.viewmodel.feature.daycare

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.DaycareRepository
import com.filkom.designimplementation.model.data.daycare.Daycare
import com.filkom.designimplementation.utils.LocationUtils
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DaycareViewModel : ViewModel() {
    private val repository = DaycareRepository()

    private val _daycares = MutableStateFlow<List<Daycare>>(emptyList())
    val daycares: StateFlow<List<Daycare>> = _daycares.asStateFlow()

    // --- TAMBAHAN: Simpan Lokasi Terakhir User ---
    private var lastUserLat: Double? = null
    private var lastUserLng: Double? = null

    private val _selectedDaycare = MutableStateFlow<Daycare?>(null)
    val selectedDaycare: StateFlow<Daycare?> = _selectedDaycare.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var allDaycaresList: List<Daycare> = emptyList()

    private var currentFilter = "Semua"

    fun fetchDaycares() {
        viewModelScope.launch {
            _isLoading.value = true
            val rawList = repository.getAllDaycares()
            val listWithDistance = if (lastUserLat != null && lastUserLng != null) {
                calculateDistanceForList(rawList, lastUserLat!!, lastUserLng!!)
            } else {
                rawList
            }
            allDaycaresList = listWithDistance
            applyFilter(currentFilter)
            _isLoading.value = false
        }
    }

    fun applyFilter(filterType: String) {
        currentFilter = filterType
        val baseList = allDaycaresList

        val filteredList = when (filterType) {
            "Terdekat" -> {
                baseList.sortedBy { it.distanceInKm ?: Float.MAX_VALUE }
            }
            "Termurah" -> {
                baseList.sortedBy { it.price }
            }
            "Rating 4+" -> {
                baseList.filter { it.rating >= 4.0 }
            }
            else -> {
                baseList
            }
        }

        _daycares.value = filteredList
    }

    private fun calculateDistanceForList(list: List<Daycare>, lat: Double, lng: Double): List<Daycare> {
        return list.map { daycare ->
            val dist = LocationUtils.calculateDistance(
                lat1 = lat, lon1 = lng,
                lat2 = daycare.latitude, lon2 = daycare.longitude
            )
            daycare.copy(distanceInKm = dist)
        }.sortedBy { it.distanceInKm ?: Float.MAX_VALUE }
    }

    fun updateDistances(userLat: Double, userLng: Double) {
        lastUserLat = userLat
        lastUserLng = userLng

        val currentList = _daycares.value
        _daycares.value = calculateDistanceForList(currentList, userLat, userLng)
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                updateDistances(location.latitude, location.longitude)
            }
        }
    }
    fun getDaycareDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedDaycare.value = repository.getDaycareById(id)
            _isLoading.value = false
        }
    }

    fun searchDaycares(query: String) {
        // Jika search kosong, kembalikan ke filter yang sedang aktif (atau tampilkan semua)
        if (query.isBlank()) {
            applyFilter(currentFilter)
            return
        }

        val lowerCaseQuery = query.lowercase()

        // Filter dari data backup (allDaycaresList)
        val searchResults = allDaycaresList.filter { daycare ->
            // Cek apakah Nama ATAU Lokasi mengandung kata pencarian
            daycare.name.lowercase().contains(lowerCaseQuery) ||
                    daycare.location.lowercase().contains(lowerCaseQuery)
        }

        _daycares.value = searchResults
    }
}