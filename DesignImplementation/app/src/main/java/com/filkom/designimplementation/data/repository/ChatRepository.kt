package com.filkom.designimplementation.data.repository

import com.filkom.designimplementation.model.core.ai.AiService
import com.filkom.designimplementation.model.core.ai.ChatMsg
import com.filkom.designimplementation.model.data.ai.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepository(private val ai: AiService) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getChatCollection() = auth.currentUser?.let { user ->
        firestore.collection("users").document(user.uid).collection("ai_messages")
    }

    fun getMessagesFlow(): Flow<List<ChatMessage>> = callbackFlow {
        val collection = getChatCollection()
        if (collection == null) {
            close()
            return@callbackFlow
        }

        val listener = collection.orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveMessageToFirestore(message: ChatMessage) {
        try {
            getChatCollection()?.add(message)?.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun askAi(currentHistory: List<ChatMessage>, newQuestion: String): String {
        val historyForAi: List<ChatMsg> = currentHistory.map { m ->
            ChatMsg(
                role = if (m.fromUser) "user" else "assistant",
                content = m.text
            )
        }

        val fullContext = historyForAi + ChatMsg("user", newQuestion)

        return ai.generateReply(SYSTEM_PROMPT, fullContext)
    }

    companion object {
        private val SYSTEM_PROMPT = """
            Kamu adalah asisten ramah bernama Little AI untuk orang tua dan bayi.
            Balas ringkas, spesifik, dan actionable; hindari pengulangan.
            Gunakan Bahasa Indonesia yang jelas.
        """.trimIndent()
    }

    suspend fun ensureWelcomeMessage() {
        val collection = getChatCollection() ?: return

        try {
            val snapshot = collection.get().await()

            if (snapshot.isEmpty) {
                val welcomeMsg = ChatMessage(
                    text = "Halo! Aku Little AI. Ada yang bisa kubantu terkait si kecil? 💗",
                    fromUser = false, // Ini dari Bot
                    timestamp = System.currentTimeMillis()
                )
                collection.add(welcomeMsg).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}