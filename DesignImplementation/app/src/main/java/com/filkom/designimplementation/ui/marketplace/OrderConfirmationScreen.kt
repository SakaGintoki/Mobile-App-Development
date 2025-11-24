package com.filkom.designimplementation.ui.marketplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.typography.Poppins
// TAMBAHKAN import eksplisit untuk fungsi top-level yang dibutuhkan
import com.filkom.designimplementation.ui.marketplace.formatRupiah
import com.filkom.designimplementation.ui.marketplace.getProductDetail
// TAMBAHKAN IMPORT EKSPLISIT UNTUK DATA CLASS
import com.filkom.designimplementation.ui.marketplace.ProductDetail

@Composable
fun OrderConfirmationScreen(
    productId: Int,
    onBack: () -> Unit,
    onProceedToPayment: (Int, Int) -> Unit // Navigasi ke Payment: (productId, quantity)
) {
    // getProductDetail is now correctly accessible from MarketplaceScreen.kt
    val productDetail = getProductDetail(productId)

    if (productDetail == null) {
        Text("Produk tidak ditemukan.", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
        return
    }

    // State untuk kuantitas
    var quantity by remember { mutableStateOf(1) } // Menggunakan mutableStateOf(1) yang umum.

    // Hitung total harga (ini adalah implementasi mock sederhana)
    // FIX: Tambahkan logic untuk menangani harga mock yang berbentuk string
    val priceString = productDetail.price.filter { it.isDigit() || it == ',' || it == '.' }
    val cleanedPriceString = priceString.replace(".", "").replace(",", "")
    val basePrice = cleanedPriceString.toIntOrNull() ?: 0

    val totalPrice = basePrice * quantity

    Scaffold(
        topBar = { ConfirmationTopBar(onBack) },
        bottomBar = { ConfirmationBottomBar(totalPrice, onProceedToPayment = { onProceedToPayment(productId, quantity) }) },
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
                    text = "Konfirmasi Pesanan",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                // Ringkasan Item
                ItemSummaryCard(productDetail, quantity,
                    onQuantityChange = { newQty ->
                        if (newQty > 0) quantity = newQty
                    }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                // Ringkasan Harga
                PriceSummaryCard(basePrice, quantity, totalPrice)
            }

            item { Spacer(Modifier.height(16.dp)) }

            item {
                // Detail Pengiriman Placeholder
                DeliveryDetailsCard()
            }
        }
    }
}

@Composable
private fun ConfirmationTopBar(onBack: () -> Unit) {
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
private fun ItemSummaryCard(
    product: ProductDetail,
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Item Belanja",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Gambar Produk
                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFC1E3))
                )
                Spacer(Modifier.width(12.dp))

                // Nama & Harga
                Column(Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = product.price,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFFF987C5)
                    )
                }

                // Kontrol Kuantitas
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFDE5F2))
                ) {
                    IconButton(onClick = { onQuantityChange(quantity - 1) }, enabled = quantity > 1) {
                        Icon(Icons.Filled.Remove, contentDescription = "Kurangi", tint = Color(0xFFF987C5))
                    }
                    Text(
                        text = "$quantity",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF222222),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color(0xFFF987C5))
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceSummaryCard(basePrice: Int, quantity: Int, totalPrice: Int) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Ringkasan Pembayaran",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Subtotal
            PriceRow(label = "Harga (${quantity}x)", value = formatRupiah(basePrice * quantity)) // FIX: Hitung subtotal

            // Biaya Pengiriman (Mock)
            PriceRow(label = "Biaya Pengiriman", value = "Rp 15.000")

            Divider(color = Color(0xFFF1F1F1), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

            // Total Akhir
            PriceRow(label = "Total Pembayaran", value = formatRupiah(totalPrice + 15000), isTotal = true)
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isTotal) Color(0xFF222222) else Color(0xFF6A6A6B)
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isTotal) Color(0xFFF987C5) else Color(0xFF222222)
        )
    }
}

@Composable
private fun DeliveryDetailsCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Alamat Pengiriman",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Placeholder Alamat
            Text(
                text = "Antony (0812xxxxxx)\nJl. Bunga Melati No. 12, Kota Malang, 65141",
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color(0xFF6A6A6B)
            )
            TextButton(onClick = { /* TODO: Ganti Alamat */ }) {
                Text("Ubah Alamat", fontFamily = Poppins, color = Color(0xFFF987C5))
            }
        }
    }
}

@Composable
private fun ConfirmationBottomBar(totalPrice: Int, onProceedToPayment: () -> Unit) {
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
                    text = "Total Harga",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color(0xFF6A6A6B)
                )
                Text(
                    text = formatRupiah(totalPrice + 15000), // Termasuk biaya kirim mock
                    fontFamily = Poppins,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color(0xFFF987C5)
                )
            }

            // Tombol Lanjut ke Pembayaran
            Button(
                onClick = onProceedToPayment,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5)),
                modifier = Modifier.width(180.dp)
            ) {
                Text("Lanjut Pembayaran", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}