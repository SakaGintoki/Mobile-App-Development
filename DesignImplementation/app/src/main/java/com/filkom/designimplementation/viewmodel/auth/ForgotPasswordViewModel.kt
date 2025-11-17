package com.filkom.designimplementation.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val onSubmit: (String) -> Unit = { email ->
        if (email.isBlank()) {
            println("Forgot Password Error: Email cannot be empty.")
//            Tampilkan pesan error di UI
//            return@let
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Forgot Password Success: Email sent to $email")
                    // TODO: Beri tahu UI bahwa email telah terkirim, dan navigasi kembali ke login.
                } else {
                    println("Forgot Password Failed: ${task.exception?.message}")
                    // TODO: Tampilkan pesan error spesifik (misalnya, email tidak terdaftar).
                }
            }
    }
}