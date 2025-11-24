package com.filkom.designimplementation.viewmodel.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.filkom.designimplementation.data.repository.ConsultationRepository
import com.filkom.designimplementation.data.repository.DaycareRepository
import com.filkom.designimplementation.data.repository.HistoryRepository
import com.filkom.designimplementation.data.repository.ProductRepository
import com.filkom.designimplementation.data.repository.SitterRepository
import com.filkom.designimplementation.model.data.history.HistoryTransaction
import com.filkom.designimplementation.viewmodel.feature.daycare.DaycareViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val repository = HistoryRepository()

    private val productRepository = ProductRepository()
    private val sitterRepository = SitterRepository()
    private val consultationRepository = ConsultationRepository()
    private val daycareRepository = DaycareRepository()

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
                val sortedItems = items.sortedByDescending { it.date }
                _historyItems.value = sortedItems
                _isLoading.value = false
            }
        }
    }
    fun submitReview(transactionId: String, itemId: String, rating: Int, category: String?) {
        viewModelScope.launch {
            when (category) {
                "Konsultasi" -> consultationRepository.submitRating(itemId, rating)
                "E-Sitter" -> sitterRepository.submitRating(itemId, rating)
                "Belanja" -> productRepository.submitRating(itemId, rating)
                "Daycare" -> daycareRepository.submitRating(itemId, rating)
            }
            repository.setTransactionReviewed(transactionId)
        }
    }
}