package com.filkom.designimplementation.ui.feature.esitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.sitter.Sitter
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.data.UserDataViewModel
import com.filkom.designimplementation.viewmodel.feature.esitter.ESitterViewModel
import kotlinx.coroutines.delay

@Composable
fun ESitterListScreen(
    viewModel: ESitterViewModel = viewModel(),
    viewModelUser: UserDataViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSitterClick: (Sitter) -> Unit = {}
) {
    val sitters by viewModel.sitters.collectAsState()
    val user by viewModelUser.userState.collectAsState()

    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "1 Hari", "3 Hari", "5 Hari")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .statusBarsPadding()
    ) {
        // 1. Custom Top Bar (Sesuai Request Gambar)
        ESitterTopBar(onBack = onBack, user?.name ?: "")

        // 2. Content List
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ESitterBannerSection()
            }

            item {
                MenuButtonsSection()
            }

            item {
                SitterFilterTabs(
                    filters = filters,
                    selected = selectedFilter,
                    onSelect = { filterName ->
                        // 1. Update warna tombol
                        selectedFilter = filterName

                        // 2. PANGGIL FUNGSI FILTER DI VIEWMODEL
                        viewModel.applyFilter(filterName)                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Lihat Semua >",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Pink,
                        modifier = Modifier
                            .clickable (
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ){ /* Handle lihat semua */ }
                    )
                }
            }

            // --- Sitter List Items ---
            if (sitters.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
//                        CircularProgressIndicator(color = Pink)
                        Text(
                            text = "Belum ada ART saat ini.",
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontFamily = Poppins
                        )
                    }
                }
            } else {
                items(sitters) { sitter ->
                    // Menggunakan padding horizontal pada item agar tidak menempel ke pinggir
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SitterCardItem(
                            sitter = sitter,
                            onClick = { onSitterClick(sitter) }
                        )
                    }
                }
            }

            // Spacer Bawah
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ================= COMPONENTS =================

@Composable
fun ESitterTopBar(
    onBack: () -> Unit,
    name : String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Kiri: Back Icon + Title Pink
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Pink,
                modifier = Modifier
                    .size(24.dp)
                    .clickable (
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onBack() }
            )
            Spacer(Modifier.width(16.dp))
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF673AB7), fontWeight = FontWeight.Bold)) {
                        append("Rumah ")
                    }
                    withStyle(style = SpanStyle(color = Pink, fontWeight = FontWeight.Bold)) {
                        append("$name!")
                    }
                },
                fontSize = 16.sp,
                fontFamily = Poppins,
                lineHeight = 20.sp
            )
        }

        // Kanan: Help Icon Pink
        Icon(
            imageVector = Icons.Default.HelpOutline, // Pastikan import ini ada
            contentDescription = "Help",
            tint = Pink,
            modifier = Modifier.size(24.dp)
        )
    }
}

// --- 2. Banner Section Khusus (Sesuai Request Gambar) ---
@Composable
fun ESitterBannerSection() {

    val banners = listOf(
        R.drawable.banner_home,
        R.drawable.banner_home,
        R.drawable.banner_home
    )

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
//                Image(
//                    painter = painterResource(banners[page]),
//                    contentDescription = "Banner $page",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
                AsyncImage(
                    model = banners[page],
                    contentDescription = "Banner Promosi",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.ic_launcher_background)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { iteration ->
                val isSelected = pagerState.currentPage == iteration

                val color = if (isSelected) Pink else Color.LightGray

                val width = if (isSelected) 20.dp else 8.dp

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun MenuButtonsSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tombol Pesanan (Pink Solid)
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Pink),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f).height(48.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = Color.White)
                Text("Pesanan", fontWeight = FontWeight.Bold, fontFamily = Poppins)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        // Tombol Cek Kupon (Abu-abu muda)
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F4F8)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f).height(48.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Pink)
                Text("Cek Kupon", fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = Poppins)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun SitterFilterTabs(filters: List<String>, selected: String, onSelect: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selected == filter

            // Menggunakan Box dengan background dan border untuk style tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Pink else Color.White)
                    .border(1.dp, if (isSelected) Pink else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filter,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Pink,
                )
            }
        }
    }
}

// --- 5. Sitter Card Item (Gaya Konsisten dengan Card Shop) ---
@Composable
fun SitterCardItem(sitter: Sitter, onClick: () -> Unit) {
    // Card Style konsisten dengan ProductCard (White, Rounded 12dp, Elevation 2dp)
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
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image (Square rounded)
            AsyncImage(
                model = sitter.imageUrl,
                contentDescription = sitter.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray.copy(alpha = 0.1f)),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            )

            Spacer(Modifier.width(12.dp))

            // Info Section
            Column(modifier = Modifier.weight(1f)) {
                // Nama
                Text(
                    text = sitter.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Pink, // Nama warna pink sesuai gambar ref
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Jadwal (Hardcoded dulu sesuai gambar referensi)
                Text(
                    text = "Senin, Selasa, Rabu",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color(0xFF7B1FA2) // Warna ungu
                )

                Spacer(Modifier.height(8.dp))

                // Rating & Reviews
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${"%.1f".format(sitter.rating)} (${sitter.reviewCount} Ulasan)",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            // Price Tag (Di kanan bawah)
            Box(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .background(Pink, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = formatRupiah(sitter.price),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}