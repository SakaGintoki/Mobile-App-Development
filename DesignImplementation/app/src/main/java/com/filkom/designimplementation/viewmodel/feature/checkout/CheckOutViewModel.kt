package com.filkom.designimplementation.viewmodel.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.filkom.designimplementation.data.repository.CartRepository
import com.filkom.designimplementation.data.repository.HistoryRepository
import com.filkom.designimplementation.data.repository.UserRepository
import com.filkom.designimplementation.model.data.product.CartItem
import com.filkom.designimplementation.model.data.history.HistoryTransaction
import com.filkom.designimplementation.model.data.product.Product
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutViewModel : ViewModel() {
    private val cartRepository = CartRepository()
    private val userRepository = UserRepository()
    private val historyRepository = HistoryRepository()
    private val auth = FirebaseAuth.getInstance()

    // State Data
    private val _checkoutItems = MutableStateFlow<List<CartItem>>(emptyList())
    val checkoutItems: StateFlow<List<CartItem>> = _checkoutItems.asStateFlow()

    private var isDirectBuy = false

    // State UI & Payment
    var selectedPaymentMethod by androidx.compose.runtime.mutableStateOf<String?>(null)
    var paymentType by androidx.compose.runtime.mutableStateOf("external")
    var transactionState by androidx.compose.runtime.mutableStateOf<String?>(null)
    var errorMessage by androidx.compose.runtime.mutableStateOf("")
    val adminFee = 7000.0

    fun prepareCartCheckout() {
        isDirectBuy = false
        viewModelScope.launch {
            // Ambil item yang dicentang dari Database Keranjang
            cartRepository.getCartItemsFlow().collect { items ->
                // Hanya update jika belum ada item (untuk menghindari refresh loop)
                if (_checkoutItems.value.isEmpty()) {
                    _checkoutItems.value = items.filter { it.isSelected }
                }
            }
        }
    }

    // --- MODE 2: BELI LANGSUNG (BELI SEKARANG) ---
    fun prepareDirectCheckout(product: Product) {
        isDirectBuy = true
        val tempItem = CartItem(
            id = "temp_direct",
            productId = product.id,
            name = product.name,
            imageUrl = product.mainImage,
            price = product.price,
            quantity = 1,
            isSelected = true
        )
        _checkoutItems.value = listOf(tempItem)
    }

    // Hitung Total
    fun getSubtotal(): Double = _checkoutItems.value.sumOf { it.price * it.quantity }
    fun getTotalPayment(): Double = getSubtotal() + adminFee

    // Proses Pembayaran Toko
    fun processPayment() {
        val userId = auth.currentUser?.uid ?: return
        val total = getTotalPayment()

        viewModelScope.launch {
            transactionState = "loading"
            val success = if (paymentType == "internal") {
                val points = (total * 0.01).toInt()
                userRepository.processTransaction(userId, total, points)
            } else {
                true
            }

            if (success) {
                createHistoryRecords(userId)
                _checkoutItems.value.forEach { item ->
                    com.filkom.designimplementation.data.repository.ProductRepository()
                        .incrementSold(item.productId, item.quantity)
                }

                if (!isDirectBuy) {
                    val cartIds = _checkoutItems.value.map { it.id }
                    cartRepository.deleteItems(cartIds)
                }

                transactionState = "success"
            } else {
                errorMessage = "Saldo tidak cukup atau gagal."
                transactionState = "failed"
            }
        }
    }



    private suspend fun createHistoryRecords(userId: String) {
        val items = _checkoutItems.value
        val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())

        items.forEach { item ->
            val transaction = HistoryTransaction(
                userId = userId,
                productId = item.productId,
                title = "${item.name} (${item.quantity}x)",
                date = date,
                total = (item.price * item.quantity),
                status = "Berhasil",
                imageUrl = item.imageUrl,
                category = "Belanja"
            )
            historyRepository.createTransaction(transaction)
        }
    }
}