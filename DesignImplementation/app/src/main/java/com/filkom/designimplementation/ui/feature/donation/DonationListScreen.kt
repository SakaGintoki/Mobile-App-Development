package com.filkom.designimplementation.ui.feature.donation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.donation.Donation
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.data.UserDataViewModel
import com.filkom.designimplementation.viewmodel.feature.donation.DonationListUiState
import com.filkom.designimplementation.viewmodel.feature.donation.DonationListViewModel

@Composable
fun DonationListScreen(
    viewModel: DonationListViewModel = viewModel(),
    viewModelUser: UserDataViewModel = viewModel(),
    onBack: () -> Unit,
    onDonationClick: (String) -> Unit // Mengirim ID ke Detail Screen
) {
    val uiState by viewModel.uiState.collectAsState()
    val user by viewModelUser.userState.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            DonationTopBar(onBack = onBack, user?.name ?: "")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                SearchBarSection()
            }
            item {
                DonationBalanceCard(user?.balance ?: 0.0)
            }
            item {
                DonationCategories()
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Rekomendasi",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        "Lihat semua>",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color(0xFF9C27B0), // Warna Ungu sesuai gambar
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 5. List Donasi (State Handling)
            when (uiState) {
                is DonationListUiState.Loading -> {
                    item {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Pink)
                        }
                    }
                }
                is DonationListUiState.Error -> {
                    item { Text("Gagal memuat data", modifier = Modifier.padding(24.dp)) }
                }
                is DonationListUiState.Success -> {
                    val donations = (uiState as DonationListUiState.Success).donations
                    if (donations.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada rekomendasi saat ini.",
                                modifier = Modifier.padding(horizontal = 24.dp),
                                color = Color.Gray,
                                fontSize = 12.sp,
                                fontFamily = Poppins
                            )
                        }
                    } else {
                        items(donations) { donation ->
                            DonationListItem(
                                donation = donation,
                                onClick = { onDonationClick(donation.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationTopBar(
    onBack: () -> Unit,
    name: String
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Halo $name!!",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = Pink, // Warna Pink judul
                fontSize = 18.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Pink)
            }
        },
        actions = {
            // Profile Picture Circle
            Image(
                painter = painterResource(R.drawable.ic_launcher_background), // Ganti dengan gambar user
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun SearchBarSection() {
    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Cari", fontFamily = Poppins, color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Pink
            )
        )
    }
}

@Composable
fun DonationBalanceCard(
    balance: Double
) {
    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFD1DC), // Pink muda
                            Color(0xFFE086D3), // Pink agak tua
                            Color(0xFFCE93D8)  // Ungu muda
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Dollar Circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFb28123)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$", color = Color(0xFFefbf04), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Total Saldo", fontFamily = Poppins, fontSize = 11.sp, color = Color.White)
                    Text(formatRupiah(balance), fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(R.drawable.ic_saldo),
                        contentDescription = "Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(24.dp)
                    )
                    Text("Isi Saldo", fontSize = 10.sp, fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Color.White )
                }
            }
        }
    }
}

@Composable
fun DonationCategories() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryCircleItem("Pendidikan", R.drawable.ic_launcher_background) // Ganti resource gambar
        CategoryCircleItem("Kesehatan", R.drawable.ic_launcher_background)
        CategoryCircleItem("Kemanusiaan", R.drawable.ic_launcher_background)
        CategoryCircleItem("Lingkungan", R.drawable.ic_launcher_background)
    }
}

@Composable
fun CategoryCircleItem(title: String, imageRes: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)
        )
        Spacer(Modifier.height(8.dp))
        Text(title, fontSize = 10.sp, fontFamily = Poppins, color = Color.DarkGray)
    }
}

@Composable
fun DonationListItem(donation: Donation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Gambar Donasi
            AsyncImage(
                model = donation.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Nama Penyelenggara + Verified
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        donation.organizerName,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                    if (donation.isVerified) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Filled.CheckCircle, null, tint = Pink, modifier = Modifier.size(12.dp))
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Judul
                Text(
                    donation.title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(12.dp))

                // Progress Bar Pink
                LinearProgressIndicator(
                    progress = { if (donation.targetAmount > 0) (donation.currentAmount / donation.targetAmount).toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Pink,
                    trackColor = Color(0xFFFFD1DC).copy(alpha = 0.4f),
                )

                Spacer(Modifier.height(8.dp))

                // Terkumpul
                Row {
                    Text("Terkumpul ", fontSize = 10.sp, color = Color.Gray, fontFamily = Poppins)
                    Text(
                        formatRupiah(donation.currentAmount),
                        fontSize = 10.sp,
                        color = Pink,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}