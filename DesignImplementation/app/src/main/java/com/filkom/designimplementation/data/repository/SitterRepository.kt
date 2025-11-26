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

    suspend fun incrementCompletedJobs(sitterId: String) {
        collection.document(sitterId).update("completedJobs", FieldValue.increment(1)).await()
    }

    suspend fun addSitter(sitter: Sitter): Boolean {
        return try {
            collection.add(sitter).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getBookedTimes(sitterId: String, date: String): List<String> {
        return try {
            val snapshot = firestore.collection("sitter_appointments") // Boleh beda collection biar rapi
                .whereEqualTo("sitterId", sitterId)
                .whereEqualTo("date", date)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.getString("time") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveBookingSlot(sitterId: String, date: String, time: String) {
        val bookingData = hashMapOf(
            "sitterId" to sitterId,
            "date" to date,
            "time" to time,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        try {
            // ID Unik gabungan
            val uniqueId = "${sitterId}_${date}_${time}"
            firestore.collection("sitter_appointments").document(uniqueId).set(bookingData).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}