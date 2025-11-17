package com.filkom.designimplementation.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.model.data.auth.User
import com.filkom.designimplementation.model.data.src.FirestoreClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Failed(val message: String) : SignUpState()
}

class SignUpViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestoreClient = FirestoreClient()

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun signUpUser(name: String, email: String, password: String) {
        _signUpState.value = SignUpState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    firebaseUser?.let { user ->
                        val newUser = User(
                            id = user.uid,
                            name = name,
                            email = email
                        )

                        viewModelScope.launch {
                            firestoreClient.insertUser(newUser).collect { isSuccess ->
                                if (isSuccess) {
                                    _signUpState.value = SignUpState.Success
                                } else {
                                    _signUpState.value =
                                        SignUpState.Failed("Gagal menyimpan data ke Firestore.")
                                    user.delete()
                                }
                            }
                        }
                    }
                } else {
                    val rawMessage = task.exception?.message ?: "Sign up gagal."
                    val customMessage = when {
                        rawMessage.contains("badly formatted", ignoreCase = true) ->
                            "Format email tidak valid. Pastikan kamu menulis email dengan benar."

                        rawMessage.contains("at least 6 characters", ignoreCase = true) ->
                            "Password minimal harus 6 karakter."

                        else -> rawMessage
                    }

                    println("Authentication failed: $customMessage")
                    _signUpState.value = SignUpState.Failed(customMessage)
                }
            }
    }
}
