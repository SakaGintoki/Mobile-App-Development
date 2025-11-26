package com.filkom.designimplementation.ui.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.checkout.CheckoutType
import com.filkom.designimplementation.viewmodel.feature.checkout.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = viewModel(),
    onBack: () -> Unit = {},
    onToPaymentMethod: () -> Unit = {}
) {
    val checkoutItems by viewModel.checkoutItems.collectAsState()
    val subtotal = checkoutItems.sumOf { it.price * it.quantity }
    val adminFee = viewModel.adminFee
    val totalPayment = subtotal + adminFee

    // Ambil Tipe Checkout untuk Logika UI
    val type = viewModel.checkoutType

    // Logic: Apakah butuh alamat?
    // Butuh: Belanja Barang (CART/DIRECT) & Panggil Sitter ke Rumah (ESITTER)
    // Tidak Butuh: Donasi, Konsultasi Online, Daycare (Datang ke lokasi)
    val showAddressSection = type == CheckoutType.CART ||
            type == CheckoutType.DIRECT_BUY ||
            type == CheckoutType.ESITTER

    // Logic: Label Judul
    val transactionLabel = when(type) {
        CheckoutType.DONATION -> "Total Donasi"
        CheckoutType.ESITTER -> "Total Jasa"
        CheckoutType.CONSULTATION -> "Total Konsultasi"
        CheckoutType.DAYCARE -> "Total Booking"
        else -> "Total Belanja"
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CheckOut", fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Pink) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Pink)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Button(
                    onClick = onToPaymentMethod,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Pilih Metode Pembayaran", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Lokasi Pengiriman (Kondisional)
            if (showAddressSection) {
                item {
                    val locationTitle = if(type == CheckoutType.ESITTER) "Lokasi Layanan" else "Lokasi Pengiriman"

                    Text(locationTitle, fontFamily = Poppins, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                    Spacer(Modifier.height(8.dp))
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.Black)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Alamat Utama", fontFamily = Poppins, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Jalan Kawi Atas No. 27, Kecamatan Klojen, Kota Malang", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                            }
                            IconButton(onClick = {}) { Icon(Icons.Default.Add, null) }
                        }
                    }
                }
            } else {
                // Spacer agar tidak terlalu mepet atas jika alamat hilang
                item { Spacer(Modifier.height(16.dp)) }
            }

            // 2. Voucher (Untuk Donasi mungkin tidak perlu voucher, tapi dibiarkan dulu)
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)), shape = RoundedCornerShape(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Yuk, tukarkan voucher kamu", fontFamily = Poppins, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(">", color = Pink)
                    }
                }
            }

            // 3. List Item
            items(checkoutItems) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.name, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                            // Tampilkan Quantity hanya untuk Barang (Cart/Direct)
                            if (type == CheckoutType.CART || type == CheckoutType.DIRECT_BUY) {
                                Text("${item.quantity} barang", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                            }

                            Spacer(Modifier.height(4.dp))
                            Text(formatRupiah(item.price), fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                        }
                    }
                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal :", fontFamily = Poppins, fontSize = 12.sp)
                        Text(formatRupiah(item.price * item.quantity), fontFamily = Poppins, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Pink)
                    }
                }
            }

            // 4. Ringkasan Pembayaran
            item {
                Text("Ringkasan Pembayaran", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                // Label Subtotal dinamis
                val subtotalLabel = if (type == CheckoutType.DONATION) "Nominal Donasi" else "Total Harga"
                SummaryRow(subtotalLabel, subtotal)

                SummaryRow("Biaya Admin", adminFee)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(transactionLabel, fontFamily = Poppins, fontWeight = FontWeight.Bold)
                    Text(formatRupiah(totalPayment), fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Pink)
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
        Text(formatRupiah(amount), fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
    }
}