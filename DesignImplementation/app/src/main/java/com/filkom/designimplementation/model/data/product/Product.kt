package com.filkom.designimplementation.model.data.product

import com.google.firebase.firestore.Exclude
import kotlin.math.roundToInt

data class CategoryData(
    val id: String = "",
    val name: String = "",
    val slug: String = ""
)

data class Product(
    val id: String = "",
    val name: String = "",
    val subtitle: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val description: String = "",
    val storeName: String = "",
    val storeLocation: String = "",
    val storeStatus: String = "",
    val isNew: Boolean = false,
    val category: CategoryData = CategoryData(),
    val imageUrls: List<String> = emptyList(),
    val sold: Int = 0,
) {
    @get:Exclude
    val mainImage: String
        get() = if (imageUrls.isNotEmpty()) imageUrls[0] else ""
    @get:Exclude
    val discount: Int
        get() {
            if (originalPrice <= price || originalPrice == 0.0) return 0
            return (((originalPrice - price) / originalPrice) * 100).roundToInt()
        }
}
