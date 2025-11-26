package com.filkom.designimplementation.model.core.ai

class RealAiService(
    private val real: AiService = OpenAiService()
) : AiService {

    override suspend fun generateReply(systemPrompt: String, history: List<ChatMsg>): String {
        val userCount = history.count { it.role == "user" }

        return if (userCount == 0) {
            "Oke, aku Little-AI. Ada yang bisa kubantu?"
        } else {
            real.generateReply(systemPrompt, history)
        }
    }
}
