package com.filkom.designimplementation.model.data.history

data class HistoryTransaction(
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val historyId: String = "",
    val title: String = "",
    val date: String = "",
    val total: Double = 0.0,
    val status: String = "Berhasil",
    val imageUrl: String = "",
    val category: String = "Belanja",
    val reviewed: Boolean = false
)