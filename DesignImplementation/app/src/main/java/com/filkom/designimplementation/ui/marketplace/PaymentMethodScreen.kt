package com.filkom.designimplementation.ui.marketplace

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.typography.Poppins
// HAPUS import com.filkom.designimplementation.ui.marketplace.*
// TAMBAHKAN import eksplisit untuk fungsi top-level yang dibutuhkan
import com.filkom.designimplementation.ui.marketplace.formatRupiah
import com.filkom.designimplementation.ui.marketplace.getProductDetail

data class PaymentOption(val id: String, val name: String, val iconRes: Int? = null)

val mockPaymentOptions = listOf(
    PaymentOption("gopay", "GoPay"),
    PaymentOption("ovo", "OVO"),
    PaymentOption("bca", "Transfer Bank BCA"),
    PaymentOption("mandiri", "Transfer Bank Mandiri"),
    PaymentOption("cc", "Kartu Kredit/Debit"),
)

@Composable
fun PaymentMethodScreen(
    productId: Int,
    quantity: Int,
    onBack: () -> Unit,
    onPaymentConfirmed: (String) -> Unit // Navigasi ke success page dengan metode pembayaran terpilih
) {
    // State untuk metode pembayaran yang dipilih
    var selectedMethod by remember { mutableStateOf<PaymentOption?>(null) }

    // Asumsi harga total dari OrderConfirmationScreen
    // FIX: Parsing harga sekarang lebih aman karena menggunakan fungsi helper
    val priceString = getProductDetail(productId)?.price?.filter { it.isDigit() || it == ',' || it == '.' }
    val cleanedPriceString = priceString?.replace(".", "")?.replace(",", "")
    val basePrice = cleanedPriceString?.toIntOrNull() ?: 0

    val totalPrice = basePrice * quantity + 15000 // Total termasuk biaya kirim mock

    Scaffold(
        topBar = { PaymentTopBar(onBack) },
        bottomBar = { PaymentBottomBar(totalPrice, selectedMethod != null, onConfirm = { onPaymentConfirmed(selectedMethod?.id ?: "") }) },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Pilih Metode Pembayaran",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            mockPaymentOptions.forEach { option ->
                item {
                    PaymentOptionCard(
                        option = option,
                        isSelected = selectedMethod == option,
                        onClick = { selectedMethod = option }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PaymentTopBar(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(end = 12.dp),
                tint = Color(0xFF222222)
            )
        }
    }
}

@Composable
private fun PaymentOptionCard(
    option: PaymentOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFFFDE5F2) else Color.White,
        border = if (isSelected) BorderStroke(2.dp, Color(0xFFF987C5)) else null,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Placeholder Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFC1E3)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(option.name.first().toString(), fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = option.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFFF987C5)
                )
            }
        }
    }
}

@Composable
private fun PaymentBottomBar(totalPrice: Int, isButtonEnabled: Boolean, onConfirm: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Total Harga Display
            Column {
                Text(
                    text = "Total Bayar",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color(0xFF6A6A6B)
                )
                Text(
                    text = formatRupiah(totalPrice),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color(0xFFF987C5)
                )
            }

            // Tombol Konfirmasi Pembayaran
            Button(
                onClick = onConfirm,
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5)),
                modifier = Modifier.width(180.dp)
            ) {
                Text("Bayar Sekarang", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}