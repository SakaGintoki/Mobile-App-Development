package com.filkom.designimplementation.model.data.ai


import com.filkom.designimplementation.model.core.ai.AiService
import com.filkom.designimplementation.model.core.ai.ChatMsg

class ChatRepository(private val ai: AiService) {

    suspend fun ask(messages: List<ChatMessage>): String {
        val history: List<ChatMsg> = messages.map { m ->
            ChatMsg(
                role = if (m.fromUser) "user" else "assistant",
                content = m.text
            )
        }
        return ai.generateReply(SYSTEM_PROMPT, history)
    }

    companion object {
        private val SYSTEM_PROMPT = """
            Kamu adalah asisten ramah bernama Little AI untuk orang tua dan bayi.
            Balas ringkas, spesifik, dan actionable; hindari pengulangan.
            Gunakan Bahasa Indonesia yang jelas. Untuk topik kesehatan bayi,
            beri langkah praktis dan tanda bahaya, tanpa mendiagnosis.
        """.trimIndent()
    }
}
