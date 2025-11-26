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
    private var allProductsList: List<Product> = emptyList()
    private var currentCategoryFilter: String = "Semua"

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            // Ambil Data Realtime
            productRepository.getAllProductsFlow().collect { products ->
                allProductsList = products // Simpan ke backup

                // Terapkan filter/search ulang jika ada update data
                selectCategory(currentCategoryFilter)
            }
        }
    }

    fun selectCategory(category: String) {
        currentCategoryFilter = category
        _selectedCategory.value = category

        if (category == "Semua") {
            _products.value = allProductsList
        } else {
            _products.value = allProductsList.filter {
                // Asumsi field 'category.name' di Firestore
                it.category.name == category
            }
        }
    }

    // 2. FUNGSI SEARCH BARU
    fun searchProducts(query: String) {
        if (query.isBlank()) {
            // Jika kosong, kembalikan ke filter kategori aktif
            selectCategory(currentCategoryFilter)
            return
        }

        val lowerQuery = query.lowercase()

        // Filter dari SEMUA produk
        val searchResults = allProductsList.filter { product ->
            product.name.lowercase().contains(lowerQuery) ||
                    product.subtitle.lowercase().contains(lowerQuery)
        }

        _products.value = searchResults
    }
}
