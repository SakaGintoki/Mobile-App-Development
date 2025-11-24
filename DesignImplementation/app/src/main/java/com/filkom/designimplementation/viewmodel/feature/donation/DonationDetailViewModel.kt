package com.filkom.designimplementation.viewmodel.feature.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.DonationRepository
import com.filkom.designimplementation.model.data.donation.Donation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State untuk UI
sealed class DonationDetailUiState {
    object Loading : DonationDetailUiState()
    data class Success(val donation: Donation) : DonationDetailUiState()
    data class Error(val message: String) : DonationDetailUiState()
}

class DonationDetailViewModel : ViewModel() {
    private val repository = DonationRepository()

    private val _uiState = MutableStateFlow<DonationDetailUiState>(DonationDetailUiState.Loading)
    val uiState: StateFlow<DonationDetailUiState> = _uiState.asStateFlow()


    fun getDonationDetail(id: String) {
        viewModelScope.launch {
            repository.incrementViewCount(id)

            _uiState.value = DonationDetailUiState.Loading
            val result = repository.getDonationById(id)
            if (result != null) {
                _uiState.value = DonationDetailUiState.Success(result)
            } else {
                _uiState.value = DonationDetailUiState.Error("Data tidak ditemukan")
            }
        }
    }
}