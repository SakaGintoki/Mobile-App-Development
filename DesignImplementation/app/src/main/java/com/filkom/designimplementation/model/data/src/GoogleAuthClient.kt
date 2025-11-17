package com.filkom.designimplementation.model.data.src

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthClient(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun signInWithGoogle(context: Context, webClientId: String): Result<String> {
        return try {
            val credentialManager = CredentialManager.Companion.create(context)

            val googleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .setNonce("optional-nonce") // opsional
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val token = extractIdToken(result)
                ?: return Result.failure(Exception("Token Google tidak ditemukan."))

            val credential = GoogleAuthProvider.getCredential(token, null)
            val user = auth.signInWithCredential(credential).await().user
                ?: return Result.failure(Exception("Login Firebase gagal."))

            Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractIdToken(result: GetCredentialResponse): String? {
        val credential = result.credential
        return if (credential is CustomCredential) {
            credential.data.getString("googleIdToken")
        } else null
    }
}