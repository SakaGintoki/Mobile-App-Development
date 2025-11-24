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

    init {
        fetchDonations()
    }

    fun fetchDonations() {
        viewModelScope.launch {
            _uiState.value = DonationListUiState.Loading
            val result = repository.getAllDonations()
            if (result.isNotEmpty()) {
                _uiState.value = DonationListUiState.Success(result)
            } else {
                // Bisa diganti Error atau Success list kosong tergantung preferensi
                _uiState.value = DonationListUiState.Success(emptyList())
            }
        }
    }
}