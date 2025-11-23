package com.filkom.designimplementation.viewmodel.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.ProductRepository // Pastikan import repository
import com.filkom.designimplementation.model.data.product.Product // Pastikan import model Product
import com.filkom.designimplementation.model.data.auth.User
import com.filkom.designimplementation.model.data.src.FirestoreClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()

    // Tambahkan List<Product> di sini
    data class Success(
        val user: User,
        val recommendedProducts: List<Product> = emptyList()
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirestoreClient()

    private val productRepository = ProductRepository()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            viewModelScope.launch {
                firestore.getUser(currentUser.email ?: "").collect { user ->
                    if (user != null) {
                        try {
                            val products = productRepository.getAllProducts().take(5)

                            _uiState.value = HomeUiState.Success(user, products)
                        } catch (e: Exception) {
                            _uiState.value = HomeUiState.Success(user, emptyList())
                        }
                    } else {
                        _uiState.value = HomeUiState.Error("Data user tidak ditemukan")
                    }
                }
            }
        } else {
            _uiState.value = HomeUiState.Error("User belum login")
        }
    }
}