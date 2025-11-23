package com.filkom.designimplementation.viewmodel.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {
    private val repository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    // State Form
    var name by mutableStateOf("")
    var username by mutableStateOf("")
    var phone by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)

    // Load data awal agar form terisi data lama
    fun loadInitialData() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                name = user.name
                username = user.username
                phone = user.phone
            }
        }
    }

    fun saveChanges() {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            isLoading = true

            // Map data yang akan diupdate
            val updates = mapOf(
                "name" to name,
                "username" to username,
                "phone" to phone
            )

            val success = repository.updateUser(userId, updates)
            isLoading = false
            if (success) {
                isSuccess = true
            }
        }
    }
}