package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.core.ai.AiService
import com.filkom.designimplementation.model.core.ai.ChatMsg
import com.filkom.designimplementation.model.core.ai.RealAiService
import com.filkom.designimplementation.model.data.ai.ChatMessage
import com.filkom.designimplementation.model.data.consultation.ChatMessageDoctor
import com.filkom.designimplementation.model.data.consultation.Doctor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ConsultationRepository (private val aiService: AiService = RealAiService()){
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("doctors")

    fun getAllDoctorsFlow(): Flow<List<Doctor>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull {
                it.toObject(Doctor::class.java)?.copy(id = it.id)
            } ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

//    fun getAllDoctorsFlow(): Flow<List<Doctor>> = callbackFlow {
//        val listener = collection.addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                close(error)
//                return@addSnapshotListener
//            }
//
//            val items = snapshot?.documents?.mapNotNull { doc ->
//                try {
//                    doc.toObject(Doctor::class.java)?.copy(id = doc.id)
//                } catch (e: Exception) {
//                    android.util.Log.e("CONSULTATION_ERROR", "Data rusak di ID: ${doc.id}. Error: ${e.message}")
//                    null // Skip data ini, lanjut ke data berikutnya
//                }
//            } ?: emptyList()
//
//            trySend(items)
//        }
//        awaitClose { listener.remove() }
//    }

    suspend fun getAllDoctors(): List<Doctor> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Doctor::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDoctorById(doctorId: String): Doctor? {
        return try {
            val document = collection.document(doctorId).get().await()
            document.toObject(Doctor::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun incrementPatientCount(doctorId: String) {
        try {
            collection.document(doctorId)
                .update("patientCount", FieldValue.increment(1))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun submitRating(doctorId: String, newRating: Int) {
        val docRef = collection.document(doctorId)
        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                val currentRating = snapshot.getDouble("rating") ?: 0.0
                val currentReviewCount = snapshot.getLong("reviewCount") ?: 0

                val newReviewCount = currentReviewCount + 1
                val newAverageRating = ((currentRating * currentReviewCount) + newRating) / newReviewCount

                transaction.update(docRef, "rating", newAverageRating)
                transaction.update(docRef, "reviewCount", newReviewCount)
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getBookedTimes(doctorId: String, date: String): List<String> {
        return try {
            val snapshot = firestore.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .whereEqualTo("date", date)
                .get()
                .await()

            snapshot.documents.mapNotNull { it.getString("time") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveBookingSlot(doctorId: String, date: String, time: String) {
        val bookingData = hashMapOf(
            "doctorId" to doctorId,
            "date" to date,
            "time" to time,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        try {
            val uniqueId = "${doctorId}_${date}_${time}"
            firestore.collection("appointments").document(uniqueId).set(bookingData).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun hasBookingHistory(doctorId: String, userId: String): Boolean {
        return try {
            val snapshot = firestore.collection("appointments")
                .whereEqualTo("doctorId", doctorId)
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    private fun getChatRoomId(userId: String, doctorId: String) = "${userId}_${doctorId}"

    fun getChatMessagesFlow(userId: String, doctorId: String): Flow<List<ChatMessageDoctor>> = callbackFlow {
        val roomId = getChatRoomId(userId, doctorId)
        val collection = firestore.collection("consultation_chats").document(roomId).collection("messages")

        val listener = collection.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessageDoctor::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(userId: String, doctorId: String, text: String) {
        val roomId = getChatRoomId(userId, doctorId)
        val message = ChatMessageDoctor(
            text = text,
            fromUser = true,
            timestamp = System.currentTimeMillis()
        )
        firestore.collection("consultation_chats").document(roomId)
            .collection("messages").add(message).await()
    }

    suspend fun autoReplyAsDoctor(userId: String, doctorId: String, doctorName: String, doctorSpecialization : String, userMessage: String) {
        try {
            val systemPrompt = """
                Kamu adalah seorang dokter profesional dengan spesialisasi $doctorSpecialization bernama $doctorName.
                Jawablah pertanyaan pasien sesuai dengan bidang keahlianmu ($doctorSpecialization).
                Jawab dengan ramah, singkat, dan empatik.
                Gunakan bahasa Indonesia yang formal namun mudah dimengerti.
                Jangan memberikan diagnosis medis pasti, tapi berikan saran umum atau rekomendasi pemeriksaan lebih lanjut.
                Jika ditanya resep obat, sarankan untuk konsultasi tatap muka.
                Maksimal jawaban 3 kalimat.
            """.trimIndent()

            val history = listOf(
                ChatMsg(role = "user", content = userMessage)
            )

            val aiResponse = aiService.generateReply(systemPrompt, history)

            val roomId = "${userId}_${doctorId}"
            val message = ChatMessage(
                text = aiResponse,
                fromUser = false,
                timestamp = System.currentTimeMillis()
            )

            firestore.collection("consultation_chats").document(roomId)
                .collection("messages").add(message).await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}