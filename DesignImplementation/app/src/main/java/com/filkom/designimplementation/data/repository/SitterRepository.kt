package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.sitter.Sitter
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SitterRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("sitters")

    // Ambil Semua Sitter (Realtime)
    fun getAllSittersFlow(): Flow<List<Sitter>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {
                it.toObject(Sitter::class.java)?.copy(id = it.id)
            } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    // Update Rating Sitter
    suspend fun submitRating(sitterId: String, userRating: Int) {
        try {
            firestore.runTransaction { transaction ->
                val docRef = collection.document(sitterId)
                val snapshot = transaction.get(docRef)

                val currentRating = snapshot.getDouble("rating") ?: 0.0
                val currentCount = snapshot.getLong("reviewCount") ?: 0

                val newCount = currentCount + 1
                val newAverage = ((currentRating * currentCount) + userRating) / newCount

                transaction.update(docRef, "rating", newAverage)
                transaction.update(docRef, "reviewCount", newCount)
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Tambah jumlah job selesai (Dipanggil saat checkout sukses)
    suspend fun incrementCompletedJobs(sitterId: String) {
        collection.document(sitterId).update("completedJobs", FieldValue.increment(1)).await()
    }

    suspend fun addSitter(sitter: Sitter): Boolean {
        return try {
            // Firestore otomatis membuat ID dokumen unik
            collection.add(sitter).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}