package com.filkom.designimplementation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.model.data.auth.User
import com.filkom.designimplementation.model.data.src.FirestoreClient
import com.filkom.designimplementation.utils.IdGenerator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // PASTIKAN IMPORT INI ADA

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    data class Success(val user: User) : SignUpState()
    data class Failed(val message: String) : SignUpState()
}

class SignUpViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirestoreClient()

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUpUser(name: String, email: String, pass: String) {
        viewModelScope.launch {
            // 1. Validasi Input
            if (name.isBlank()) {
                _signUpState.value = SignUpState.Failed("Nama tidak boleh kosong.")
                return@launch
            }
            if (email.isBlank()) {
                _signUpState.value = SignUpState.Failed("Email tidak boleh kosong.")
                return@launch
            }
            if (pass.isBlank()) {
                _signUpState.value = SignUpState.Failed("Password tidak boleh kosong.")
                return@launch
            }

            // 2. Set Loading
            _signUpState.value = SignUpState.Loading

            try {
                // 3. Buat Akun di Firebase Auth (Tunggu sampai selesai dengan await())
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    firebaseUser.sendEmailVerification()

                    val uniqueLongId = IdGenerator.generateUniqueId()

                    val newUser = User(
                        id = firebaseUser.uid,
                        usernumber = uniqueLongId,
                        name = name,
                        email = email,
                        balance = 0.0,
                        points = 50
                    )


                    firestore.insertUser(newUser).collect { isSuccess ->
                        if (isSuccess) {
                            _signUpState.value = SignUpState.Success(newUser)
                        } else {
                            _signUpState.value = SignUpState.Failed("Gagal menyimpan data pengguna.")
                        }
                    }
                } else {
                    _signUpState.value = SignUpState.Failed("Terjadi kesalahan: User null.")
                }

            } catch (e: Exception) {
                // Handle Error Firebase Auth (Email sudah ada, format salah, dll)
                val errorMsg = when {
                    e.message?.contains("badly formatted", true) == true -> "Format email tidak valid."
                    e.message?.contains("at least 6 characters", true) == true -> "Password minimal 6 karakter."
                    e.message?.contains("email address is already in use", true) == true -> "Email sudah terdaftar."
                    else -> e.message ?: "Sign up gagal."
                }
                _signUpState.value = SignUpState.Failed(errorMsg)
            }
        }
    }
}