package com.filkom.designimplementation.data

data class ChatMessage(
    val id: Long,
    val fromUser: Boolean,
    val text: String
)