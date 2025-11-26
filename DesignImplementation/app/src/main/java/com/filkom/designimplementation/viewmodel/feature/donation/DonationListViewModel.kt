package com.filkom.designimplementation.viewmodel.feature.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.DonationRepository
import com.filkom.designimplementation.model.data.donation.Donation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DonationListUiState {
    object Loading : DonationListUiState()
    data class Success(val donations: List<Donation>) : DonationListUiState()
    data class Error(val message: String) : DonationListUiState()
}

class DonationListViewModel : ViewModel() {
    private val repository = DonationRepository()

    private val _uiState = MutableStateFlow<DonationListUiState>(DonationListUiState.Loading)
    val uiState: StateFlow<DonationListUiState> = _uiState.asStateFlow()

    // Backup Data (PENTING!)
    private var allDonationsList: List<Donation> = emptyList()

    // Filter saat ini
    private var currentFilter: String = "Semua"

    init {
        fetchDonations()
    }

    fun fetchDonations() {
        viewModelScope.launch {
            _uiState.value = DonationListUiState.Loading
            val result = repository.getAllDonations()

            // 1. SIMPAN DATA KE BACKUP (INI YANG KURANG)
            allDonationsList = result

            // 2. Terapkan filter default ("Semua")
            applyFilter(currentFilter)
        }
    }

    fun applyFilter(filterType: String) {
        currentFilter = filterType // Simpan state filter

        val filteredList = if (filterType == "Semua") {
            allDonationsList
        } else {
            allDonationsList.filter { it.category == filterType }
        }

        updateUiState(filteredList)
    }

    // --- FUNGSI SEARCH BARU ---
    fun searchDonations(query: String) {
        // Jika search kosong, kembalikan ke filter kategori yang sedang aktif
        if (query.isBlank()) {
            applyFilter(currentFilter)
            return
        }

        val lowerQuery = query.lowercase()

        // Filter dari SEMUA data (allDonationsList)
        // Mencari berdasarkan Judul ATAU Nama Penyelenggara
        val searchResult = allDonationsList.filter { donation ->
            donation.title.lowercase().contains(lowerQuery) ||
                    donation.organizerName.lowercase().contains(lowerQuery)
        }

        updateUiState(searchResult)
    }

    // Helper function biar rapi
    private fun updateUiState(list: List<Donation>) {
        if (list.isNotEmpty()) {
            _uiState.value = DonationListUiState.Success(list)
        } else {
            _uiState.value = DonationListUiState.Success(emptyList())
        }
    }
}