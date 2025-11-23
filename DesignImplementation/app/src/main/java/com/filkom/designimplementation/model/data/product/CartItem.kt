package com.filkom.designimplementation.model.data.product

data class CartItem(
    val id: String = "",
    val productId: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val rating: Double = 0.0,
    val quantity: Int = 1,
    val isSelected: Boolean = true
)