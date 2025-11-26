package com.filkom.designimplementation.model.data.src

import android.util.Log
import com.filkom.designimplementation.model.data.auth.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.String

class FirestoreClient {
    private val tag = "FirestoreClient"
    private val db = FirebaseFirestore.getInstance()
    private val collection = "users"

    fun insertUser(user: User): Flow<Boolean> = callbackFlow {
        db.collection(collection)
            .document(user.id)
            .set(user.toHashMap())
            .addOnSuccessListener { trySend(true) }
            .addOnFailureListener { e ->
                e.printStackTrace()
                trySend(false)
            }
        awaitClose {}
    }

    fun updateUser(user: User): Flow<Boolean> = callbackFlow {
        db.collection(collection)
            .document(user.id)
            .set(user.toHashMap())
            .addOnSuccessListener {
                Log.d(tag, "Updating user: ${user.id}")
                trySend(true)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Error updating user: ${e.message}")
                trySend(false)
            }
        awaitClose {}
    }

    fun getUser(email: String?): Flow<User?> = callbackFlow {
        if (email == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listenerRegistration = db.collection(collection)
            .whereEqualTo("email", email)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val document = snapshot.documents[0]
                    val user = document.data?.toUser(document.id)
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
        awaitClose {
            listenerRegistration.remove()
        }
    }

    private fun User.toHashMap(): HashMap<String, Any?> {
        return hashMapOf(
            "id" to id,
            "usernumber" to usernumber,
            "name" to name,
            "email" to email,
            "username" to username,
            "phone" to phone,
            "imageUrl" to imageUrl,
            "role" to role,
            "points" to points,
            "balance" to balance
        )
    }

    private fun Map<String, Any?>.toUser(docId: String): User {
        return User(
            id = docId,

            usernumber = (this["usernumber"] as? Number)?.toLong() ?: 0L,

            name = this["name"] as? String ?: "",
            email = this["email"] as? String ?: "",
            username = this["username"] as? String ?: "",
            phone = this["phone"] as? String ?: "",
            imageUrl = this["imageUrl"] as? String ?: "",
            role = this["role"] as? String ?: "user",

            // Konversi Number
            points = (this["points"] as? Number)?.toInt() ?: 0,
            balance = (this["balance"] as? Number)?.toDouble() ?: 0.0
        )
    }
}