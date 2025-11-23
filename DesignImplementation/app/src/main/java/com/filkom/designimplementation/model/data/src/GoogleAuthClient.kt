package com.filkom.designimplementation.model.data.src

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthClient(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun signInWithGoogle(context: Context, webClientId: String): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()

            val response = credentialManager.getCredential(context, request)

            val token = extractIdToken(response)
                ?: return Result.failure(Exception("ID Token tidak ditemukan."))

            val firebaseCredential = GoogleAuthProvider.getCredential(token, null)

            val user = auth.signInWithCredential(firebaseCredential)
                .await().user ?: return Result.failure(Exception("Tidak bisa login Firebase."))

            Result.success(user.uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractIdToken(response: GetCredentialResponse): String? {
        val credential = response.credential

        if (credential !is CustomCredential) {
            Log.e("GoogleAuth", "Credential bukan instance CustomCredential")
            return null
        }

        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                return googleIdTokenCredential.idToken
            } catch (e: GoogleIdTokenParsingException) {
                Log.e("GoogleAuth", "Gagal memparsing Google ID Token", e)
            }
        } else {
            Log.e("GoogleAuth", "Tipe credential tidak dikenali: ${credential.type}")
        }

        return null
    }
}