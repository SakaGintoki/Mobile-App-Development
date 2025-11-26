package com.filkom.designimplementation.model.data.consultation

data class ChatMessageDoctor(
    val id: String = "",
    val text: String = "",
    val fromUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)