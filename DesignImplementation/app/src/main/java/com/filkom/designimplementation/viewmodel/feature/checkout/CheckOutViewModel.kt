package com.filkom.designimplementation.viewmodel.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.filkom.designimplementation.data.repository.CartRepository
import com.filkom.designimplementation.data.repository.ConsultationRepository
import com.filkom.designimplementation.data.repository.DaycareRepository
import com.filkom.designimplementation.data.repository.DonationRepository
import com.filkom.designimplementation.data.repository.HistoryRepository
import com.filkom.designimplementation.data.repository.ProductRepository
import com.filkom.designimplementation.data.repository.SitterRepository
import com.filkom.designimplementation.data.repository.UserRepository
import com.filkom.designimplementation.model.data.consultation.Doctor
import com.filkom.designimplementation.model.data.daycare.Daycare
import com.filkom.designimplementation.model.data.donation.Donation
import com.filkom.designimplementation.model.data.product.CartItem
import com.filkom.designimplementation.model.data.history.HistoryTransaction
import com.filkom.designimplementation.model.data.product.Product
import com.filkom.designimplementation.model.data.sitter.Sitter
import com.filkom.designimplementation.utils.IdGenerator.generateUniqueIdHistory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class CheckoutType {
    CART,
    DIRECT_BUY,
    ESITTER,
    DONATION,
    CONSULTATION,
    DAYCARE
}

class CheckoutViewModel : ViewModel() {
    private val cartRepository = CartRepository()
    private val userRepository = UserRepository()
    private val historyRepository = HistoryRepository()
    private val sitterRepository = SitterRepository()
    private val productRepository = ProductRepository()
    private val donationRepository = DonationRepository()
    private val consultationRepository = ConsultationRepository()

    private val daycareRepository = DaycareRepository()

    private val auth = FirebaseAuth.getInstance()

    private val _checkoutItems = MutableStateFlow<List<CartItem>>(emptyList())
    val checkoutItems: StateFlow<List<CartItem>> = _checkoutItems.asStateFlow()

    var checkoutType = CheckoutType.CART

    var selectedPaymentMethod by mutableStateOf<String?>(null)
    var paymentType by mutableStateOf("external")
    var transactionState by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf("")

    val adminFee = 7000.0

    private var bookingDateString = ""
    private var bookingTimeString = ""

    // --- FUNGSI RESET STATE (PENTING) ---
    fun resetState() {
        transactionState = null
        errorMessage = ""
        selectedPaymentMethod = null
        bookingDateString = ""
        bookingTimeString = ""
        // checkoutItems dibiarkan, karena mungkin mau dilihat sebelum bayar
    }

    fun prepareCartCheckout() {
        resetState() // Reset sebelum mulai
        checkoutType = CheckoutType.CART
        viewModelScope.launch {
            cartRepository.getCartItemsFlow().collect { items ->
                if (_checkoutItems.value.isEmpty()) {
                    _checkoutItems.value = items.filter { it.isSelected }
                }
            }
        }
    }

    fun prepareDirectCheckout(product: Product) {
        resetState() // Reset sebelum mulai
        checkoutType = CheckoutType.DIRECT_BUY
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

    fun prepareSitterCheckout(sitter: Sitter, date: String, time: String) {
        resetState() // Reset sebelum mulai
        checkoutType = CheckoutType.ESITTER
        bookingDateString = date
        bookingTimeString = time

        val tempItem = CartItem(
            id = "temp_sitter_${System.currentTimeMillis()}",
            productId = sitter.id,
            name = "${sitter.name} ($date - $time)",
            imageUrl = sitter.imageUrl,
            price = sitter.price,
            quantity = 1,
            isSelected = true
        )
        _checkoutItems.value = listOf(tempItem)
    }

    fun prepareDonationCheckout(donation: Donation, nominal: Double) {
        resetState() // Reset sebelum mulai
        checkoutType = CheckoutType.DONATION

        val tempItem = CartItem(
            id = "temp_donation_${System.currentTimeMillis()}",
            productId = donation.id,
            name = donation.title,
            imageUrl = donation.imageUrl,
            price = nominal,
            quantity = 1,
            isSelected = true
        )
        _checkoutItems.value = listOf(tempItem)
    }

    fun prepareConsultationCheckout(doctor: Doctor, date: String, time: String) {
        resetState() // Reset sebelum mulai
        checkoutType = CheckoutType.CONSULTATION
        bookingDateString = date
        bookingTimeString = time

        val tempItem = CartItem(
            id = "temp_consult_${System.currentTimeMillis()}",
            productId = doctor.id,
            name = "Konsultasi: ${doctor.name} ($date - $time)",
            imageUrl = doctor.imageUrl,
            price = doctor.price,
            quantity = 1,
            isSelected = true
        )
        _checkoutItems.value = listOf(tempItem)
    }

    fun prepareDaycareCheckout(daycare: Daycare, startDate: String) {
        resetState() // Reset sebelum mulai
        checkoutType = CheckoutType.DAYCARE

        val tempItem = CartItem(
            id = "temp_daycare_${System.currentTimeMillis()}",
            productId = daycare.id,
            name = "${daycare.name} (Mulai: $startDate)",
            imageUrl = daycare.imageUrl,
            price = daycare.price,
            quantity = 1,
            isSelected = true
        )
        _checkoutItems.value = listOf(tempItem)
    }

    fun getSubtotal(): Double = _checkoutItems.value.sumOf { it.price * it.quantity }
    fun getTotalPayment(): Double = getSubtotal() + adminFee

    fun processPayment() {
        val userId = auth.currentUser?.uid ?: return
        val total = getTotalPayment()
        val date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())

        viewModelScope.launch {
            transactionState = "loading"

            val success = if (paymentType == "internal") {
                val points = (total * 0.02).toInt()
                userRepository.processTransaction(userId, total, points)
            } else {
                val points = (total * 0.01).toInt()
                userRepository.processTransaction(userId, 0.0, points)
            }

            if (success) {
                _checkoutItems.value.forEach { item ->

                    val categoryString = when (checkoutType) {
                        CheckoutType.ESITTER -> "E-Sitter"
                        CheckoutType.DONATION -> "Donasi"
                        CheckoutType.CONSULTATION -> "Konsultasi"
                        CheckoutType.DAYCARE -> "Daycare"
                        else -> "Belanja"
                    }
                    val itemTotal = item.price * item.quantity

                    val transaction = HistoryTransaction(
                        userId = userId,
                        productId = item.productId,
                        historyId = generateUniqueIdHistory(),
                        title = item.name,
                        date = date,
                        total = if (_checkoutItems.value.size == 1) total else itemTotal,
                        status = "Berhasil",
                        imageUrl = item.imageUrl,
                        category = categoryString,
                        reviewed = false
                    )
                    historyRepository.createTransaction(transaction)

                    when (checkoutType) {
                        CheckoutType.ESITTER -> {
                            sitterRepository.incrementCompletedJobs(item.productId)
                            if (bookingDateString.isNotEmpty() && bookingTimeString.isNotEmpty()) {
                                sitterRepository.saveBookingSlot(
                                    item.productId,
                                    bookingDateString,
                                    bookingTimeString
                                )
                            }
                        }

                        CheckoutType.CONSULTATION -> {
                            consultationRepository.incrementPatientCount(item.productId)
                            if (bookingDateString.isNotEmpty() && bookingTimeString.isNotEmpty()) {
                                consultationRepository.saveBookingSlot(
                                    item.productId,
                                    bookingDateString,
                                    bookingTimeString
                                )
                            }
                        }

                        CheckoutType.DONATION -> {
                            donationRepository.updateCurrentAmount(item.productId, item.price)
                        }

                        CheckoutType.DAYCARE -> {
                            daycareRepository.incrementBookingCount(item.productId)
                        }

                        else -> { // Cart / Direct Buy
                            productRepository.incrementSold(item.productId, item.quantity)
                        }
                    }
                }

                if (checkoutType == CheckoutType.CART) {
                    val cartIds = _checkoutItems.value.map { it.id }
                    cartRepository.deleteItems(cartIds)
                }

                transactionState = "success"
            } else {
                errorMessage = "Saldo tidak mencukupi."
                transactionState = "failed"
            }
        }
    }
}