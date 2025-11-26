package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.history.HistoryTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class HistoryRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getUserHistoryFlow(): Flow<List<HistoryTransaction>> = callbackFlow {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(HistoryTransaction::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createTransaction(transaction: HistoryTransaction) {
        try {
            firestore.collection("transactions").add(transaction).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun setTransactionReviewed(transactionId: String) {
        try {
            firestore.collection("transactions")
                .document(transactionId)
                .update("reviewed", true)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}