package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.daycare.Daycare
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DaycareRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllDaycares(): List<Daycare> {
        return try {
            val snapshot = firestore.collection("daycares").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Daycare::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDaycareById(id: String): Daycare? {
        return try {
            val document = firestore.collection("daycares").document(id).get().await()
            document.toObject(Daycare::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    // Update jumlah booking saat checkout berhasil
    suspend fun incrementBookingCount(id: String) {
        try {
            firestore.collection("daycares").document(id)
                .update("bookingCount", FieldValue.increment(1))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun submitRating(daycareId: String, newRating: Int) {
        val docRef = firestore.collection("daycares").document(daycareId)

        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                // 1. Ambil data lama
                // Pastikan di Firestore field ini tipe Number/Double, bukan String
                val currentRating = snapshot.getDouble("rating") ?: 0.0
                val currentReviewCount = snapshot.getLong("reviewCount") ?: 0

                // 2. Hitung Rata-rata Baru
                val newReviewCount = currentReviewCount + 1
                val newAverageRating = ((currentRating * currentReviewCount) + newRating) / newReviewCount

                // 3. Update ke Firestore
                transaction.update(docRef, "rating", newAverageRating)
                transaction.update(docRef, "reviewCount", newReviewCount)
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}