package com.filkom.designimplementation.ui.feature.shop

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.shop.ProductDetailViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = viewModel(),
    onBack: () -> Unit = {},
    onAddToCart: () -> Unit = {},
    onBuyNow: () -> Unit = {}
) {
    val product = viewModel.productState.collectAsState().value
    val context = androidx.compose.ui.platform.LocalContext.current
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Pink)
        }
        return
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Pink
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            DetailBottomBar(
                onAddToCart = {
                    if (product != null){
                        viewModel.addToCart(product)
                        Toast.makeText(context, "Berhasil masuk keranjang", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                , onBuyNow = onBuyNow)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1. Image Carousel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Tinggi disesuaikan agar gambar jelas
                    .background(Color(0xFFFFF5EE)),
                contentAlignment = Alignment.BottomCenter
            ) {
                val pagerState = rememberPagerState(pageCount = { product.imageUrls.size })

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = product.imageUrls[page], // Mengambil dari list index ke-page
                            contentDescription = null,
                            contentScale = ContentScale.Crop, // Saran: Gunakan Crop agar full, atau Fit agar utuh
                            modifier = Modifier.fillMaxSize(), // Ubah size jadi fillMaxSize agar memenuhi Box
                            placeholder = painterResource(R.drawable.ic_launcher_background),
                            error = painterResource(R.drawable.ic_launcher_background)
                        )
                    }
                }

                // Dots Indicator (Hanya muncul jika gambar > 1)
                if (product.imageUrls.size > 1) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .wrapContentWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagerState.pageCount) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Pink else Color.LightGray
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                // 2. Title & Price
                Text(
                    text = product.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Text(
                    text = product.subtitle,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(12.dp))

                // Price Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatRupiah(product.price),
                        fontFamily = Poppins,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Pink
                    )

                    if (product.discount > 0) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = formatRupiah(product.originalPrice),
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${product.discount}% Off",
                                fontFamily = Poppins,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Rating & Sold
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${product.rating} (${product.reviewCount} Ulasan)",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("|", color = Color.LightGray)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "${product.sold} Terjual",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

                // 3. Description
                Text(
                    text = "Deskripsi Produk",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = product.description,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 22.sp
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

                // 4. Store Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Store Icon
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Store, null, tint = Color.Gray)
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.storeName,
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Filled.CheckCircle, null, tint = Pink, modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = product.storeLocation,
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    OutlinedButton(
                        onClick = { /* Visit Store */ },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Pink),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Kunjungi", fontSize = 12.sp, color = Pink)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailBottomBar(onAddToCart: () -> Unit, onBuyNow: () -> Unit) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Chat
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Color.Gray)
            }

            // Add to Cart
            OutlinedButton(
                onClick = onAddToCart,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Pink),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("Keranjang", fontWeight = FontWeight.Bold, color = Pink)
            }

            // Buy Now
            Button(
                onClick = onBuyNow,
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("Beli Sekarang", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}