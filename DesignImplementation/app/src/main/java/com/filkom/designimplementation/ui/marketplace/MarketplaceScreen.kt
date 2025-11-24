package com.filkom.designimplementation.ui.marketplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.typography.Poppins // Assuming this is defined
import java.text.NumberFormat // Import yang diperlukan
import java.util.Locale // Import yang diperlukan

// =======================================================
// MOCK DATA CLASSES (Pusat Data)
// =======================================================
data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val imageRes: Int,
    val rating: Double
)

data class Seller(val name: String, val rating: Double, val imageRes: Int)
data class Review(val user: String, val rating: Int, val comment: String, val date: String)

data class ProductDetail(
    val id: Int,
    val name: String,
    val price: String,
    val imageRes: Int,
    val rating: Double,
    val seller: Seller,
    val description: String,
    val fullReviews: List<Review>
)

// Mock list of baby/kids products
val mockProducts = listOf(
    Product(1, "Popok Bayi Premium (Jumbo)", "Rp 150.000", R.drawable.placeholder_pampers, 4.8),
    Product(2, "Mainan Balok Susun Edukasi", "Rp 75.000", R.drawable.placeholder_toys, 4.5),
    Product(3, "Baju Tidur Katun Lembut", "Rp 55.000", R.drawable.placeholder_clothes, 4.9),
    Product(4, "Botol Susu Anti-Kolik", "Rp 60.000", R.drawable.placeholder_bottle, 4.7),
    Product(5, "Baby Wipes Isi Ulang (4 Pack)", "Rp 45.000", R.drawable.placeholder_wipes, 4.6),
    Product(6, "Susu Formula Tahap 1", "Rp 95.000", R.drawable.placeholder_milk, 4.9),
)

// FUNGSI EXTERNAL UNTUK DESKRIPSI (STRING DIPECANG UNTUK MENGHINDARI BATASAN KOMPILASI)
private fun getLongDescription(): String {
    return "Popok Bayi Premium ini dirancang dengan teknologi ultra-serap yang menjaga kulit si kecil tetap kering hingga 12 jam. " +
            "Bahan sangat lembut, bersertifikasi organik, dan hypoallergenic, cocok untuk kulit sensitif. " +
            "Tersedia dalam ukuran M-XXL."
}

// FUNGSI EXTERNAL UNTUK ULASAN (KOMENTAR JUGA DIPECANG)
private fun getMockReviews(): List<Review> {
    return listOf(
        Review("MamaBunda123", 5, "Sangat puas! Popoknya benar-benar anti bocor dan lembut.", "2 minggu lalu"),
        Review("AyahHebat", 4, "Pengiriman cepat, produk sesuai deskripsi. " + "Hanya saja sedikit mahal.", "1 bulan lalu")
    )
}


// FUNGSI UTAMA UNTUK MENGAMBIL DETAIL PRODUK
fun getProductDetail(productId: Int): ProductDetail? {
    val baseProduct = mockProducts.find { it.id == productId } ?: return null

    // Ambil data dari fungsi terpisah (yang lebih aman dari batasan kompilasi statis)
    val sellerData = Seller("Toko Si Kecil", 4.9, R.drawable.placeholder_seller_avatar)

    return ProductDetail(
        id = baseProduct.id,
        name = baseProduct.name,
        price = baseProduct.price,
        imageRes = baseProduct.imageRes,
        rating = baseProduct.rating,
        seller = sellerData,
        description = getLongDescription(), // <-- DIPANGGIL DARI FUNGSI TERPISAH
        fullReviews = getMockReviews()      // <-- DIPANGGIL DARI FUNGSI TERPISAH
    )
}

// FUNGSI UNTUK MEMFORMAT MATA UANG RUPIAH
fun formatRupiah(number: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return formatter.format(number)
}
// =======================================================

@Composable
fun MarketplaceScreen(
    onBack: () -> Unit,
    onProductClick: (Int) -> Unit = {} // <-- Modified to pass only Int (Product ID)
) {
    // 1. STATE MANAGEMENT UNTUK SEARCH BAR
    var searchQuery by remember { mutableStateOf("") }

    // 2. LOGIKA FILTER
    val filteredProducts = remember(mockProducts, searchQuery) {
        if (searchQuery.isBlank()) {
            mockProducts
        } else {
            mockProducts.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = { MarketplaceTopBar(onBack) },
        containerColor = Color(0xFFF8F8F8) // Matches HomeScreen background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // SEARCH BAR BERGAYA OUTLINED TEXT FIELD
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari Popok, Mainan, atau Susu...", fontFamily = Poppins) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Cari") },
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFF987C5),
                    unfocusedBorderColor = Color(0xFFF1F1F1),
                    focusedLabelColor = Color(0xFFF987C5)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // PESAN JIKA TIDAK DITEMUKAN
            if (filteredProducts.isEmpty() && searchQuery.isNotBlank()) {
                Text(
                    text = "Tidak ada produk yang cocok dengan \"$searchQuery\"",
                    fontFamily = Poppins,
                    color = Color(0xFF6A6A6B),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // DAFTAR PRODUK (SEKARANG MENGGUNAKAN DAFTAR TERFILTER)
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredProducts) { product ->
                    // Pass the ID here
                    ProductCard(product = product, onClick = { onProductClick(product.id) })
                }
            }
        }
    }
}

@Composable
private fun MarketplaceTopBar(onBack: () -> Unit) { // <-- Receives the onBack function
    // Use Column to apply padding from the status bar on top of the Box's content
    Column(
        modifier = Modifier
            .fillMaxWidth()
            // PENTING: Terapkan padding status bar untuk memastikan area klik tombol kembali berfungsi
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(Color(0xFFFDE5F2)) // Placeholder for gradient/header
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp) // Maintain the height of the actual content area
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(end = 12.dp),
                    tint = Color(0xFF222222)
                )
                Text(
                    text = "Marketplace (Belanja)",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color(0xFF222222)
                )
            }
        }
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) { // <-- Modified onClick to Unit
    Surface(
        shape = RoundedCornerShape(16.dp), // Use 16.dp for product cards
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(Modifier.padding(12.dp)) {
            // Area Tampilan Gambar Produk
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFC1E3)), // Soft pink background color
                contentAlignment = Alignment.Center
            ) {
                // Menggunakan sumber daya gambar yang sebenarnya dari Product object.
                // Pastikan R.drawable.placeholder_... sudah tersedia di proyek Anda.
                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize() // Fill the box
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.price,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFF987C5) // Use the prominent pink from the AI button
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${product.rating}",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color(0xFF6A6A6B)
                    )
                }
            }
            AssistChip(
                onClick = { /* Aksi Tambah ke Keranjang */ },
                label = { Text("Beli", fontFamily = Poppins) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFFFDE5F2),
                    labelColor = Color(0xFFF987C5)
                ),
                border = null,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
    }
}