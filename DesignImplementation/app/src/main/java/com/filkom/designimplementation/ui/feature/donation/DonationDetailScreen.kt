package com.filkom.designimplementation.ui.feature.donation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.LocationOn
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.donation.Donation
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.checkout.CheckoutViewModel
import com.filkom.designimplementation.viewmodel.feature.donation.DonationDetailUiState
import com.filkom.designimplementation.viewmodel.feature.donation.DonationDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun DonationDetailScreen(
    donationId: String, // Menerima ID String, bukan Objek
    checkoutViewModel: CheckoutViewModel,
    viewModel: DonationDetailViewModel = viewModel(), // ViewModel baru
    onBack: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    // 1. Fetch Data saat pertama kali dibuka
    LaunchedEffect(donationId) {
        viewModel.getDonationDetail(donationId)
    }

    val uiState by viewModel.uiState.collectAsState()

    // 2. Handle UI State (Loading / Error / Success)
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        when (uiState) {
            is DonationDetailUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Pink
                )
            }
            is DonationDetailUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Gagal memuat data", fontFamily = Poppins)
                    Button(onClick = { viewModel.getDonationDetail(donationId) }, colors = ButtonDefaults.buttonColors(containerColor = Pink)) {
                        Text("Coba Lagi")
                    }
                }
            }
            is DonationDetailUiState.Success -> {
                val donationData = (uiState as DonationDetailUiState.Success).donation

                // Panggil Content Utama jika data sukses
                DonationContent(
                    donation = donationData,
                    checkoutViewModel = checkoutViewModel,
                    onBack = onBack,
                    onNavigateToPayment = onNavigateToPayment
                )
            }
        }
    }
}

// Pisahkan UI Konten agar kode lebih rapi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationContent(
    donation: Donation,
    checkoutViewModel: CheckoutViewModel,
    onBack: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showNominalSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Button(
                onClick = { showNominalSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Donasi", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // --- HEADER GRADIENT ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFE086D3), Color(0xFFE4A4D8))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "Halo!",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
            }

            // --- CONTENT SCROLLABLE ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Image Donasi
                AsyncImage(
                    model = donation.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Judul
                Text(
                    text = donation.title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Chips (Lokasi & View)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(donation.location, fontSize = 12.sp, fontFamily = Poppins) },
                        icon = { Icon(Icons.Outlined.LocationOn, null, modifier = Modifier.size(14.dp)) },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.LightGray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SuggestionChip(
                        onClick = {},
                        label = { Text("${donation.viewCount}", fontSize = 12.sp, fontFamily = Poppins) },
                        icon = { Icon(Icons.Filled.Visibility, null, modifier = Modifier.size(14.dp)) },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.LightGray)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { if(donation.targetAmount > 0) (donation.currentAmount / donation.targetAmount).toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Pink,
                    trackColor = Color(0xFFFFD1DC).copy(alpha = 0.3f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Terkumpul ${formatRupiah(donation.currentAmount)}",
                    fontFamily = Poppins,
                    color = Pink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Organizer Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = donation.organizerName,
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                                if (donation.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Filled.CheckCircle, null, tint = Pink, modifier = Modifier.size(14.dp))
                                }
                            }
                            Text(
                                text = "Verified Account",
                                fontFamily = Poppins,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text(
                    text = donation.description,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // --- BOTTOM SHEET NOMINAL ---
    if (showNominalSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNominalSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            DonationAmountInput(
                onNominalSelected = { nominal ->
                    checkoutViewModel.prepareDonationCheckout(donation, nominal)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        showNominalSheet = false
                        onNavigateToPayment()
                    }
                }
            )
        }
    }
}

// Komponen input nominal sama seperti jawaban sebelumnya
@Composable
fun DonationAmountInput(
    onNominalSelected: (Double) -> Unit
) {
    var nominalInput by remember { mutableStateOf("") }
    val quickAmounts = listOf(10000, 50000, 100000, 150000, 200000, 250000)

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 30.dp)
    ) {
        Text("Isi Nominal", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nominalInput,
            onValueChange = { if (it.all { char -> char.isDigit() }) nominalInput = it },
            prefix = { Text("Rp ", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Pink,
                focusedContainerColor = Color(0xFFF3F4F6),
                unfocusedContainerColor = Color(0xFFF3F4F6)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            trailingIcon = {
                Text("0", fontFamily = Poppins, color = Color.Gray, modifier = Modifier.padding(end = 12.dp))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(120.dp)
        ) {
            items(quickAmounts) { amount ->
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .clickable { nominalInput = amount.toString() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rp ${amount / 1000}.000",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val finalAmount = nominalInput.toDoubleOrNull() ?: 0.0
                    if (finalAmount > 0) {
                        onNominalSelected(finalAmount)
                    }
                },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih Metode Pembayaran",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}