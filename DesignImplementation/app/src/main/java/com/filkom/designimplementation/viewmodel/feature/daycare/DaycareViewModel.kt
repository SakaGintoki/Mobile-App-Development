package com.filkom.designimplementation.viewmodel.feature.daycare

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.DaycareRepository
import com.filkom.designimplementation.model.data.daycare.Daycare
import com.filkom.designimplementation.utils.LocationUtils
import com.google.android.gms.location.* // Import semua dari location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DaycareViewModel : ViewModel() {
    private val repository = DaycareRepository()

    private val _daycares = MutableStateFlow<List<Daycare>>(emptyList())
    val daycares: StateFlow<List<Daycare>> = _daycares.asStateFlow()

    private var lastUserLat: Double? = null
    private var lastUserLng: Double? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedDaycare = MutableStateFlow<Daycare?>(null)
    val selectedDaycare: StateFlow<Daycare?> = _selectedDaycare.asStateFlow()

    // Backup Data
    private var allDaycaresList: List<Daycare> = emptyList()
    private var currentFilter = "Semua"

    // Client Location
    private var fusedLocationClient: FusedLocationProviderClient? = null

    // Callback untuk update lokasi realtime
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation
            if (location != null) {
                // Setiap kali user bergerak, fungsi ini dipanggil
                updateDistances(location.latitude, location.longitude)
            }
        }
    }

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

    fun updateDistances(userLat: Double, userLng: Double) {
        lastUserLat = userLat
        lastUserLng = userLng

        // Update Backup List
        allDaycaresList = calculateDistanceForList(allDaycaresList, userLat, userLng)
        // Refresh UI
        applyFilter(currentFilter)
    }

    fun applyFilter(filterType: String) {
        currentFilter = filterType
        val baseList = allDaycaresList

        val filteredList = when (filterType) {
            "Terdekat" -> baseList.sortedBy { it.distanceInKm ?: Float.MAX_VALUE }
            "Termurah" -> baseList.sortedBy { it.price }
            "Rating 4+" -> baseList.filter { it.rating >= 4.0 }
            else -> baseList
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
        }
    }

    // --- FUNGSI START LOCATION UPDATE (GANTI YANG LAMA) ---
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Setting Request: Update setiap 5 detik atau jika pindah 10 meter
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000 // 5000ms = 5 detik
        ).apply {
            setMinUpdateDistanceMeters(10f) // Update jika pindah 10 meter
        }.build()

        // Mulai mendengarkan lokasi
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // Penting: Hentikan update saat ViewModel hancur agar tidak boros baterai
    override fun onCleared() {
        super.onCleared()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    // ... (Fungsi getDaycareDetail & searchDaycares tetap sama) ...
    fun getDaycareDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val daycare = repository.getDaycareById(id)
            if (daycare != null && lastUserLat != null && lastUserLng != null) {
                val dist = LocationUtils.calculateDistance(
                    lastUserLat!!, lastUserLng!!,
                    daycare.latitude, daycare.longitude
                )
                _selectedDaycare.value = daycare.copy(distanceInKm = dist)
            } else {
                _selectedDaycare.value = daycare
            }
            _isLoading.value = false
        }
    }

    fun searchDaycares(query: String) {
        if (query.isBlank()) {
            applyFilter(currentFilter)
            return
        }
        val lowerCaseQuery = query.lowercase()
        val searchResults = allDaycaresList.filter { daycare ->
            daycare.name.lowercase().contains(lowerCaseQuery) ||
                    daycare.location.lowercase().contains(lowerCaseQuery)
        }
        _daycares.value = searchResults
    }
}