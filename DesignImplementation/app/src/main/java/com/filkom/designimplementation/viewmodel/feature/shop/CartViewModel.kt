package com.filkom.designimplementation.viewmodel.feature.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.CartRepository
import com.filkom.designimplementation.model.data.product.CartItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {

    private val repository = CartRepository()

    val cartItems: StateFlow<List<CartItem>> = repository.getCartItemsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateQuantity(id: String, newQty: Int) {
        viewModelScope.launch {
            repository.updateQuantity(id, newQty)
        }
    }

    fun toggleSelection(id: String) {
        // Cari item di list saat ini untuk tahu status sebelumnya
        val item = cartItems.value.find { it.id == id }
        if (item != null) {
            viewModelScope.launch {
                repository.toggleSelection(id, !item.isSelected)
            }
        }
    }

    fun toggleSelectAll(isSelected: Boolean) {
        viewModelScope.launch {
            repository.toggleSelectAll(isSelected)
        }
    }


    fun removeItem(id: String) {
        viewModelScope.launch {
            repository.removeItem(id)
        }
    }
}