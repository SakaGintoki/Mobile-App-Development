package com.filkom.designimplementation.viewmodel.feature.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.CartRepository
import com.filkom.designimplementation.data.repository.ProductRepository
import com.filkom.designimplementation.model.data.product.CartItem
import com.filkom.designimplementation.model.data.product.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val cartRepository = CartRepository()

    private val _productState = MutableStateFlow<Product?>(null)
    val productState: StateFlow<Product?> = _productState.asStateFlow()

    fun loadProductById(id: String) {
        viewModelScope.launch {
            _productState.value = null

            repository.getProductDetailFlow(id).collect { productFromDb ->
                _productState.value = productFromDb
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val cartItem = CartItem(
                productId = product.id,
                name = product.name,
                imageUrl = product.mainImage,
                price = product.price,
                originalPrice = product.originalPrice,
                rating = product.rating,
                quantity = 1,
                isSelected = true
            )
            cartRepository.addToCart(cartItem)
        }
    }
}