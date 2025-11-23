package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.auth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun getCurrentUser(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            snapshot.toObject(User::class.java)?.copy(id = userId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Boolean {
        return try {
            usersCollection.document(userId).update(updates).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun processTransaction(userId: String, totalPrice: Double, pointsEarned: Int): Boolean {
        return try {
            val userRef = usersCollection.document(userId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentBalance = snapshot.getDouble("balance") ?: 0.0
                val currentPoints = snapshot.getLong("points") ?: 0

                if (currentBalance < totalPrice) {
                    throw Exception("Saldo tidak cukup")
                }

                val newBalance = currentBalance - totalPrice
                val newPoints = currentPoints + pointsEarned

                transaction.update(userRef, "balance", newBalance)
                transaction.update(userRef, "points", newPoints)
            }.await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun logout() {
        auth.signOut()
    }


}