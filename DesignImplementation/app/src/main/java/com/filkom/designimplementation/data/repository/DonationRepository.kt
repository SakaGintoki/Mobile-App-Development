package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.data.donation.Donation
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DonationRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getDonationById(donationId: String): Donation? {
        return try {
            val document = firestore.collection("donations")
                .document(donationId)
                .get()
                .await()

            document.toObject(Donation::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateCurrentAmount(donationId: String, amount: Double) {
        try {
            firestore.collection("donations")
                .document(donationId)
                .update("currentAmount", FieldValue.increment(amount)) // Atomically increment
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getAllDonations(): List<Donation> {
        return try {
            val snapshot = firestore.collection("donations").get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Donation::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun incrementViewCount(donationId: String) {
        try {
            val docRef = firestore.collection("donations").document(donationId)
            docRef.update("viewCount", com.google.firebase.firestore.FieldValue.increment(1))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}