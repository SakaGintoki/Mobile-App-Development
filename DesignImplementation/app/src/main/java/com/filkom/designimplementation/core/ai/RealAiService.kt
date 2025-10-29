package com.filkom.designimplementation.core.ai

class RealAiService : AiService {
    override suspend fun generateReply(systemPrompt: String, history: List<ChatMsg>): String {
        // TODO: replace with your real backend call
        return "Oke, aku Little-AI. Ada yang bisa kubantu?"
    }
}
