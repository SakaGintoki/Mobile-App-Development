package com.filkom.designimplementation.model.data.auth

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val usernumber: Long = 0,
    val username: String = "",
    val phone: String = "",
    val imageUrl: String = "",
    val role: String = "user",
    val balance: Double = 0.0,
    val points: Int = 0
)