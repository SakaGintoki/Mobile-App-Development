package com.filkom.designimplementation.viewmodel.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.UserRepository
import com.filkom.designimplementation.model.data.auth.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    // Fungsi dipanggil saat layar dibuka atau setelah edit berhasil
    fun fetchUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val user = repository.getCurrentUser()
            if (user != null) {
                _uiState.value = ProfileUiState.Success(user)
            } else {
                _uiState.value = ProfileUiState.Error("Gagal memuat profil atau user belum login.")
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}