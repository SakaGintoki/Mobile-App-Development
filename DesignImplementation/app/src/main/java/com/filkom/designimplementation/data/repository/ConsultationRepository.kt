package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.consultation.Doctor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ConsultationRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return try {
            val document = firestore.collection("doctors")
                .document(doctorId)
                .get()
                .await()

            document.toObject(Doctor::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun incrementPatientCount(doctorId: String) {
        try {
            // Asumsi field 'patientCount' di Firestore bertipe Number
            firestore.collection("doctors")
                .document(doctorId)
                .update("patientCount", FieldValue.increment(1))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun submitRating(doctorId: String, newRating: Int) {
        val docRef = firestore.collection("doctors").document(doctorId)

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
    suspend fun getAllDoctors(): List<Doctor> {
        return try {
            val snapshot = firestore.collection("doctors").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Doctor::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}