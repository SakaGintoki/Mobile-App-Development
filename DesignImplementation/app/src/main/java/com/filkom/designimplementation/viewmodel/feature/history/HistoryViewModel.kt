package com.filkom.designimplementation.viewmodel.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.HistoryRepository
import com.filkom.designimplementation.data.repository.ProductRepository // 1. Tambahkan Import ini
import com.filkom.designimplementation.model.data.history.HistoryTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val repository = HistoryRepository()

    // 2. Tambahkan Repository Produk (Untuk update rating)
    private val productRepository = ProductRepository()

    private val _historyItems = MutableStateFlow<List<HistoryTransaction>>(emptyList())
    val historyItems: StateFlow<List<HistoryTransaction>> = _historyItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getUserHistoryFlow().collect { items ->
                _historyItems.value = items
                _isLoading.value = false
            }
        }
    }
    fun submitReview(transactionId: String, productId: String, rating: Int) {
        viewModelScope.launch {
            productRepository.submitRating(productId, rating)
            repository.setTransactionReviewed(transactionId)
        }
    }
}