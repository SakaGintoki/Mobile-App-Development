package com.filkom.designimplementation.viewmodel.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.model.data.auth.User
import com.filkom.designimplementation.model.data.src.FirestoreClient
import com.filkom.designimplementation.model.data.src.GoogleAuthClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: User) : LoginState()
    data class Failed(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirestoreClient()
    private val googleClient = GoogleAuthClient(auth)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun signInWithGoogle(context: Context, webClientId: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = googleClient.signInWithGoogle(context, webClientId)
            result.fold(
                onSuccess = { uid ->
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        firestore.getUser(firebaseUser.email!!).collect { userProfile ->
                            if (userProfile != null) {
                                _loginState.value = LoginState.Success(userProfile)
                            } else {
                                val newUser = User(
                                    id = uid,
                                    name = firebaseUser.displayName ?: "User",
                                    email = firebaseUser.email!!
                                )
                                firestore.insertUser(newUser).collect { ok ->
                                    _loginState.value =
                                        if (ok) LoginState.Success(newUser)
                                        else LoginState.Failed("Gagal menyimpan user ke Firestore.")
                                }
                            }
                        }
                    }
                },
                onFailure = { e ->
                    _loginState.value = LoginState.Failed(e.message ?: "Login Google gagal.")
                }
            )
        }
    }

    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _loginState.value = LoginState.Failed("Email dan password tidak boleh kosong.")
                return@launch
            }

            _loginState.value = LoginState.Loading

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        if (firebaseUser != null) {
                            viewModelScope.launch {
                                firestore.getUser(firebaseUser.email!!).collect { user ->
                                    if (user != null) {
                                        _loginState.value = LoginState.Success(user)
                                    } else {
                                        val newUser = User(
                                            id = firebaseUser.uid,
                                            name = firebaseUser.email!!.substringBefore("@"),
                                            email = firebaseUser.email!!
                                        )
                                        firestore.insertUser(newUser).collect { ok ->
                                            _loginState.value =
                                                if (ok) LoginState.Success(newUser)
                                                else LoginState.Failed("Gagal menambahkan user.")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        val exception = task.exception
                        val message = when (exception?.message) {
                            "The email address is badly formatted." ->
                                "Format email tidak valid."

                            "There is no user record corresponding to this identifier. The user may have been deleted." ->
                                "Akun tidak ditemukan. Silakan daftar terlebih dahulu."

                            "The supplied auth credential is incorrect, malformed or has expired." ->
                                "Akun tidak ditemukan. Silakan daftar terlebih dahulu."

                            "The password is invalid or the user does not have a password." ->
                                "Password salah. Coba lagi."

                            "The user account has been disabled by an administrator." ->
                                "Akun Anda dinonaktifkan."

                            else -> exception?.message ?: "Login gagal. Periksa koneksi."
                        }
                        _loginState.value = LoginState.Failed(message)
                    }
                }
        }
    }
}
