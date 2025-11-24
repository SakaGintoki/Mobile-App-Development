package com.filkom.designimplementation.viewmodel.feature.shop

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.filkom.designimplementation.model.data.auth.User
import com.filkom.designimplementation.model.data.product.Product
import com.filkom.designimplementation.model.data.src.FirestoreClient
import com.filkom.designimplementation.data.repository.ProductRepository // Import repo produk
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopViewModel : ViewModel() {

    private val firestoreClient = FirestoreClient() // Untuk ambil User
    private val productRepository = ProductRepository() // Untuk ambil Produk
    private val auth = FirebaseAuth.getInstance()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private var allProductsCache: List<Product> = emptyList()


    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            productRepository.getAllProductsFlow().collect { result ->
                allProductsCache = result
                selectCategory(_selectedCategory.value)
            }
        }
    }

    // --- FUNGSI FILTER ---
    fun selectCategory(category: String) {
        _selectedCategory.value = category
        if (allProductsCache.isNotEmpty()) {
            if (category == "Semua") {
                _products.value = allProductsCache
            } else {
                _products.value = allProductsCache.filter { it.category.name == category }
            }
        }
    }
}