package com.filkom.designimplementation.core.ai

data class ChatMsg(val role: String, val content: String)

interface AiService {
    suspend fun generateReply(systemPrompt: String, history: List<ChatMsg>): String
}
