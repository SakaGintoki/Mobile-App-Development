package com.filkom.designimplementation.ui.marketplace

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.typography.Poppins
// TAMBAHKAN IMPORT EKSPLISIT UNTUK FUNGSIONALITAS
import com.filkom.designimplementation.ui.marketplace.getProductDetail
// TAMBAHKAN IMPORT EKSPLISIT UNTUK DATA CLASSES DARI FILE PUSAT DATA (MarketplaceScreen.kt)
import com.filkom.designimplementation.ui.marketplace.ProductDetail
import com.filkom.designimplementation.ui.marketplace.Seller
import com.filkom.designimplementation.ui.marketplace.Review


@Composable
fun ProductDetailScreen(
    productId: Int,
    onBack: () -> Unit,
    onCheckout: (Int) -> Unit = {} // Digunakan di BottomBar
) {
    // Fetch mock product details based on the ID (In a real app, this would use State/ViewModel)
    val product = getProductDetail(productId) // Menggunakan fungsi global

    if (product == null) {
        // Handle error or loading state
        Text("Product Not Found", modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
        return
    }

    Scaffold(
        topBar = { ProductDetailTopBar(product.name, onBack) },
        // Meneruskan productId ke onCheckout
        bottomBar = { ProductDetailBottomBar(product.price, onCheckout = { onCheckout(productId) }) },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { ProductImageSection(product) }
            item { ProductInfoSection(product) }
            item { SellerInfoCard(product.seller) }
            item { Spacer(Modifier.height(16.dp)) }
            item { ReviewHeader(product.rating, product.fullReviews.size) }
            items(product.fullReviews) { review ->
                ReviewCard(review)
            }
        }
    }
}

// ... (Composable pendukung ProductDetailTopBar, ProductImageSection, dll. tidak berubah)
@Composable
private fun ProductDetailTopBar(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .background(Color.White) // Use white for detail screen top bar
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(end = 12.dp),
                    tint = Color(0xFF222222)
                )
                Text(
                    text = title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProductImageSection(product: ProductDetail) {
    Image(
        painter = painterResource(product.imageRes),
        contentDescription = product.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color(0xFFFDE5F2)) // Soft pink background
    )
}

@Composable
private fun ProductInfoSection(product: ProductDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Product Name
        Text(
            text = product.name,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color(0xFF222222)
        )
        Spacer(Modifier.height(8.dp))

        // Price and Rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = product.price,
                fontFamily = Poppins,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = Color(0xFFF987C5) // Pink accent
            )
            Spacer(Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 18.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "${product.rating}",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF6A6A6B)
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Divider(color = Color(0xFFF1F1F1), thickness = 1.dp)
        Spacer(Modifier.height(16.dp))

        // Description Header
        Text(
            text = "Deskripsi Produk",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = product.description, // <-- FIX 1: This now resolves
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color(0xFF6A6A6B)
        )
    }
}

@Composable
private fun SellerInfoCard(seller: Seller) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { /* TODO: Navigate to seller profile */ }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Seller Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFC1E3)),
                contentAlignment = Alignment.Center
            ) {
                // In a real app, use Image with seller.imageRes
                Text("👤", fontSize = 24.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "Dijual oleh",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color(0xFF6A6A6B)
                )
                Text(
                    text = seller.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 14.sp)
                Text("${seller.rating}", fontFamily = Poppins, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun ReviewHeader(averageRating: Double, totalReviews: Int) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Ulasan Pembeli",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Rata-rata ${averageRating} dari ${totalReviews} ulasan",
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color(0xFF6A6A6B)
        )
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            // Review Header (User + Rating)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = review.user,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Row {
                    repeat(review.rating) {
                        Text("★", color = Color(0xFFF987C5)) // Pink stars
                    }
                    repeat(5 - review.rating) {
                        Text("☆", color = Color(0xFFF1F1F1)) // Grey stars
                    }
                }
            }
            Spacer(Modifier.height(4.dp))

            // Review Comment
            Text(
                text = review.comment,
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = Color(0xFF222222)
            )
            Spacer(Modifier.height(8.dp))

            // Review Date
            Text(
                text = review.date,
                fontFamily = Poppins,
                fontSize = 10.sp,
                color = Color(0xFF6A6A6B),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun ProductDetailBottomBar(price: String, onCheckout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        tonalElevation = 4.dp // Strong shadow for the bottom bar
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Price Display
            Column {
                Text(
                    text = "Total Harga",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color(0xFF6A6A6B)
                )
                Text(
                    text = price,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Color(0xFFF987C5)
                )
            }

            // Buy Button
            Button(
                onClick = onCheckout, // <-- Memicu navigasi checkout
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5)),
                modifier = Modifier.width(180.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Beli")
                    Spacer(Modifier.width(8.dp))
                    Text("Beli Sekarang", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}