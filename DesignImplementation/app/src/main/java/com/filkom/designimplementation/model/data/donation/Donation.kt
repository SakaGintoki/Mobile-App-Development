package com.filkom.designimplementation.model.data.donation

data class Donation(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val viewCount: Int = 0,
    val currentAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val organizerName: String = "",
    val isVerified: Boolean = false,
    val description: String = "",
    val category: String = "Lainnya"
)