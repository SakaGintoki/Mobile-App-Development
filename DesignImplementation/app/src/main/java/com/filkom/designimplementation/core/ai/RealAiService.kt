package com.filkom.designimplementation.core.ai

class RealAiService(
    private val real: AiService = OpenAiService()
) : AiService {

    override suspend fun generateReply(systemPrompt: String, history: List<ChatMsg>): String {
        // hitung pesan user
        val userCount = history.count { it.role == "user" }

        return if (userCount == 0) {
            // ini kondisi pertama kali / belum ada user chat
            "Oke, aku Little-AI. Ada yang bisa kubantu?"
        } else {
            // kalau user sudah kirim pesan, pakai OpenAI beneran
            real.generateReply(systemPrompt, history)
        }
    }
}
