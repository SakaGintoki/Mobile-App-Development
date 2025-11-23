package com.filkom.designimplementation.viewmodel.feature.shop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.ProductRepository
import com.filkom.designimplementation.model.data.product.CategoryData // Pastikan import ini
import com.filkom.designimplementation.model.data.product.Product
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    // State UI (Input Form)
    var name by mutableStateOf("")
    var subtitle by mutableStateOf("")

    // Harga Input String agar tidak error saat ngetik
    var price by mutableStateOf("")
    var originalPrice by mutableStateOf("")

    var description by mutableStateOf("")

    // Kategori Input String (User ngetik "Obat")
    var category by mutableStateOf("Semua")

    var storeName by mutableStateOf("Toko Admin")
    var storeLocation by mutableStateOf("Jakarta")

    // 3 Slot Gambar
    var image1 by mutableStateOf("")
    var image2 by mutableStateOf("")
    var image3 by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun saveProduct() {
        if (name.isBlank()) { errorMessage = "Nama produk wajib diisi"; return }
        if (price.isBlank()) { errorMessage = "Harga wajib diisi"; return }
        if (image1.isBlank()) { errorMessage = "Minimal isi 1 foto utama"; return }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val listImages = listOf(image1, image2, image3).filter { it.isNotBlank() }
            val finalPrice = price.toDoubleOrNull() ?: 0.0
            val finalOriginalPrice = originalPrice.toDoubleOrNull() ?: 0.0

            val categoryObj = CategoryData(
                id = category.lowercase().replace(" ", "_"), // misal: "Obat Sirup" -> "obat_sirup"
                name = category,
                slug = category
            )

            val newProduct = Product(
                name = name,
                subtitle = subtitle,
                price = finalPrice,
                originalPrice = finalOriginalPrice,
                description = description,
                imageUrls = listImages,
                category = categoryObj,
                storeName = storeName,
                storeLocation = storeLocation,
                storeStatus = "Online",
                rating = 5.0,
                sold = 0,
                reviewCount = 0,
                isNew = true
            )

            val result = repository.addProduct(newProduct)
            isLoading = false

            if (result) isSuccess = true
            else errorMessage = "Gagal menyimpan ke database"
        }
    }
}