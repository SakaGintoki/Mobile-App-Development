package com.filkom.designimplementation.model.data.ai

data class ChatMessage(
    val id: Long,
    val fromUser: Boolean,
    val text: String
)