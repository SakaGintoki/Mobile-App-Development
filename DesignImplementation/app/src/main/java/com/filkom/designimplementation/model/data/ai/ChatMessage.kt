package com.filkom.designimplementation.model.data.ai

import com.google.firebase.firestore.PropertyName

data class ChatMessage(
    val id: String = "",
    val text: String = "",

    @get:PropertyName("fromUser")
    val fromUser: Boolean = false,

    val timestamp: Long = System.currentTimeMillis()
)