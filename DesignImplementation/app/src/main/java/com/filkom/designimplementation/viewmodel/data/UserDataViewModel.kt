package com.filkom.designimplementation.viewmodel.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.filkom.designimplementation.data.repository.ProductRepository
import com.filkom.designimplementation.model.data.auth.User
import com.filkom.designimplementation.model.data.src.FirestoreClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class UserDataViewModel : ViewModel(){
    private val firestoreClient = FirestoreClient() // Untuk ambil User
    private val productRepository = ProductRepository() // Untuk ambil Produk
    private val auth = FirebaseAuth.getInstance()
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    var isAdmin by mutableStateOf(false)
        private set

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val email = auth.currentUser?.email
        if (email != null) {
            viewModelScope.launch {
                firestoreClient.getUser(email).collect { user ->
                    _userState.value = user
                    isAdmin = (user?.role == "admin")
                }
            }
        }
    }


}