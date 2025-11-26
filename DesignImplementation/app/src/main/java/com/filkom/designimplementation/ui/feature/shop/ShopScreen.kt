package com.filkom.designimplementation.ui.feature.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage // Pastikan pakai coil.compose
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.product.Product
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.data.UserDataViewModel
import com.filkom.designimplementation.viewmodel.feature.shop.ShopViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ShopScreen(
    viewModel: ShopViewModel = viewModel(),
    viewModelUser: UserDataViewModel = viewModel(),
    onNavigateToDetail: (String) -> Unit = {}, // ID Produk
    onNavigateToCart: () -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToAdd: () -> Unit = {}
) {
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // 1. AMBIL DATA USER DARI VIEWMODEL
    val user by viewModelUser.userState.collectAsState()
    // Kategori Tabs
    val categories = listOf("Semua", "Suplemen", "Kebutuhan", "Vitamin", "Obat", "Alat")
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .statusBarsPadding()
    ) {
        // 1. Top Bar Custom (Search & Icons)
        ShopTopBar(
            searchQuery = searchQuery,
            onSearchChange = { newQuery ->
                searchQuery = newQuery
                viewModel.searchProducts(newQuery) // Panggil VM
            },
            onCartClick = onNavigateToCart,
            onBack = onBack
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Header Section: Wallet
            item(span = { GridItemSpan(2) }) {

                WalletSection(
                    balance = user?.balance ?: 0.0,
                    points = user?.points ?: 0
                )
            }

            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Barang Populer",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(12.dp))
                    BannerSection()
                }
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(16.dp))
                CategoryTabs(
                    categories = categories,
                    selected = selectedCategory,
                    onSelect = { viewModel.selectCategory(it) }
                )
                Spacer(Modifier.height(16.dp))
            }

            // --- Product Grid ---
            items(products) { product ->
                ProductCardItem(
                    product = product,
                    onClick = {
                        onNavigateToDetail(product.id)
                    }
                )
            }

            // Spacer Bawah
            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

// ================= COMPONENTS =================

@Composable
fun ShopTopBar(
    searchQuery: String, // Terima Value
    onSearchChange: (String) -> Unit, // Terima Fungsi
    onCartClick: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Pink,
            modifier = Modifier.clickable { onBack() }
        )

        Spacer(Modifier.width(12.dp))

        // Search Bar (TextField)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text("Cari produk...", fontFamily = Poppins, color = Color.Gray, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = Color.Gray)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Pink
                ),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = Poppins,
                    fontSize = 14.sp
                ),
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Spacer(Modifier.width(16.dp))

        Icon(Icons.Outlined.Notifications, null, tint = Pink, modifier = Modifier.size(26.dp))
        Spacer(Modifier.width(12.dp))
        Icon(
            Icons.Outlined.ShoppingBag,
            null,
            tint = Pink,
            modifier = Modifier.size(26.dp).clickable { onCartClick() }
        )
    }
}

// 3. UPDATE WALLET SECTION MENERIMA PARAMETER
@Composable
fun WalletSection(
    balance: Double,
    points: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Tampilkan Saldo
        WalletItem(
            iconBg = Color(0xFFE8F5E9),
            iconColor = Color(0xFF2E7D32),
            icon = Icons.Default.AccountBalanceWallet,
            text = formatRupiah(balance)
        )
        // Tampilkan Poin
        WalletItem(
            iconBg = Color(0xFFE3F2FD),
            iconColor = Color(0xFF1565C0),
            icon = Icons.Default.MonetizationOn,
            text = "$points Poin"
        )
        // Voucher (Tetap Statis)
        WalletItem(
            iconBg = Color(0xFFF3E5F5),
            iconColor = Color(0xFF7B1FA2),
            icon = Icons.Default.ConfirmationNumber,
            text = "Cek Voucher"
        )
    }
}

@Composable
fun WalletItem(iconBg: Color, iconColor: Color, icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(6.dp))
        Text(text, fontFamily = Poppins, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
@Composable
fun BannerSection() {
    val banners = listOf(
        R.drawable.banner_home,
        R.drawable.banner_home,
        R.drawable.banner_home
    )

    // 2. STATE PAGER
    val pagerState = rememberPagerState(pageCount = { banners.size })

    // 3. LOGIKA AUTO SCROLL (Berjalan setiap 3 detik)
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Tunggu 3000ms (3 detik)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    // 4. UI
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // --- BAGIAN GAMBAR SLIDER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp) // Tinggi banner
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray) // Placeholder warna loading
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // Tampilkan Gambar sesuai halaman (page)
                AsyncImage(
                    model = banners[page],
                    contentDescription = "Banner Promosi",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.ic_launcher_background)
                )

                // Overlay Text (Opsional - Hapus jika gambar sudah ada teksnya)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.6f))))
                )
            }

            // Teks Promo di atas banner (Opsional)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
//                Text(
//                    text = "Promo Spesial", // Bisa dinamis sesuai data
//                    fontFamily = Poppins,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White,
//                    fontSize = 16.sp
//                )
                Button(
                    onClick = { /* Handle klik banner */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Lihat", fontSize = 10.sp, color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- BAGIAN INDIKATOR TITIK (DOTS) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Pink else Color.LightGray
                val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp // Efek memanjang saat aktif

                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .height(8.dp)
                        .width(width) // Lebar dinamis
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun CategoryTabs(categories: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val isSelected = selected == category
            Text(
                text = category,
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Pink else Color.Gray,
                modifier = Modifier
                    .clickable (
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(category) }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun ProductCardItem(product: Product, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Box {
                AsyncImage(
                    model = product.mainImage,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(Color.Gray.copy(alpha = 0.1f)),
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background)
                )
                if (product.isNew || product.discount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Pink, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(if(product.isNew) "Baru!" else "Sale", fontSize = 10.sp, color = Color.White, fontFamily = Poppins)
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(24.dp)
                ) {
                    Icon(Icons.Default.ThumbUp, null, tint = Pink, modifier = Modifier.padding(4.dp))
                }
            }

            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    text = product.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = formatRupiah(product.price),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF9C27B0)
                )

                if (product.discount > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF8BBD0), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("${product.discount}%", fontSize = 10.sp, color = Color(0xFF880E4F), fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = formatRupiah(product.originalPrice),
                            fontFamily = Poppins,
                            fontSize = 10.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(2.dp))
                    Text("${product.rating}", fontSize = 11.sp, color = Color.Gray, fontFamily = Poppins)
                    Spacer(Modifier.width(4.dp))
                    Box(Modifier.size(3.dp).background(Color.Gray, CircleShape))
                    Spacer(Modifier.width(4.dp))
                    Text("${product.sold} Terjual", fontSize = 11.sp, color = Color.Gray, fontFamily = Poppins)
                }
            }
        }
    }
}