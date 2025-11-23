package com.filkom.designimplementation.viewmodel.feature.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.ProductRepository // Pastikan path ini sesuai
import com.filkom.designimplementation.model.data.product.Product // Pastikan path ini sesuai dengan Model baru Anda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopViewModel : ViewModel() {

    private val repository = ProductRepository()

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
            repository.getAllProductsFlow().collect { result ->

                allProductsCache = result // Update cache
                selectCategory(_selectedCategory.value) // Refresh tampilan dengan filter saat ini
            }
        }
    }

    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
        if (allProductsCache.isNotEmpty()) {
            if (categoryName == "Semua") {
                _products.value = allProductsCache
            } else {
                _products.value = allProductsCache.filter {
                    it.category.name.equals(categoryName, ignoreCase = true) ||
                            it.category.slug.equals(categoryName, ignoreCase = true)
                }
            }
        }
    }
}