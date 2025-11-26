package com.filkom.designimplementation.ui.feature.donation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.text.style.TextAlign
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
    donationId: String,
    checkoutViewModel: CheckoutViewModel,
    viewModel: DonationDetailViewModel = viewModel(),
    onBack: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    LaunchedEffect(donationId) {
        viewModel.getDonationDetail(donationId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        when (uiState) {
            is DonationDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Pink)
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
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Button(
                    onClick = { showNominalSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Pink),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Donasi Sekarang", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1. HEADER IMAGE (Full width)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = donation.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient Overlay untuk text readability di atas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent)))
                )

                // Top Bar Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Pink)
                    }
                }
            }

            // 2. CONTENT CONTAINER (Overlap effect)
            Column(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(24.dp)
            ) {
                // Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFE3F2FD), shape = RoundedCornerShape(6.dp)) {
                        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFF2196F3), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(donation.location, fontSize = 10.sp, color = Color(0xFF2196F3), fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(color = Color(0xFFFCE4EC), shape = RoundedCornerShape(6.dp)) {
                        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Visibility, null, tint = Pink, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${donation.viewCount} Dilihat", fontSize = 10.sp, color = Pink, fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = donation.title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    color = Color(0xFF212121)
                )

                Spacer(Modifier.height(20.dp))

                // Progress Section
                val progress = if (donation.targetAmount > 0) (donation.currentAmount / donation.targetAmount).toFloat() else 0f
                val percentage = (progress * 100).toInt()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Terkumpul", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                    Text("Target", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                }
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatRupiah(donation.currentAmount), fontFamily = Poppins, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Pink)
                    Text(formatRupiah(donation.targetAmount), fontFamily = Poppins, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                }

                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Pink,
                    trackColor = Color(0xFFFFEBEE),
                )
                Text(
                    text = "$percentage% tercapai",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )

                HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color(0xFFF5F5F5))

                // Organizer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(donation.organizerName, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (donation.isVerified) {
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF2196F3), modifier = Modifier.size(14.dp))
                            }
                        }
                        Text("Penggalang Dana", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color(0xFFF5F5F5))

                // Donatur (Social Proof)
                Text("Donatur Terbaru", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    items(5) { // Dummy 5 donatur
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+99", fontSize = 10.sp, fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color(0xFFF5F5F5))

                // Description
                Text("Cerita Penggalangan Dana", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = donation.description,
                    fontFamily = Poppins,
                    fontSize = 13.sp,
                    color = Color(0xFF616161),
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Justify
                )
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

@Composable
fun DonationAmountInput(
    onNominalSelected: (Double) -> Unit
) {
    var nominalInput by remember { mutableStateOf("") }
    val quickAmounts = listOf(10000, 20000, 50000, 100000, 200000, 500000)

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp)
    ) {
        Text("Pilih Nominal Donasi", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(20.dp))

        // Grid Pilihan Cepat
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(120.dp)
        ) {
            items(quickAmounts) { amount ->
                val isSelected = nominalInput == amount.toString()
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            if (isSelected) Pink else Color(0xFFEEEEEE),
                            RoundedCornerShape(12.dp)
                        )
                        .background(
                            if (isSelected) Pink.copy(alpha = 0.1f) else Color.White,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){ nominalInput = amount.toString() }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatRupiah(amount.toDouble()).replace(",00", ""), // Format pendek
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Pink else Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Atau Masukkan Nominal Lain", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nominalInput,
            onValueChange = { if (it.all { char -> char.isDigit() }) nominalInput = it },
            prefix = { Text("Rp ", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFEEEEEE),
                focusedBorderColor = Pink,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val finalAmount = nominalInput.toDoubleOrNull() ?: 0.0
                if (finalAmount > 0) {
                    onNominalSelected(finalAmount)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Pink),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Lanjut Pembayaran", fontFamily = Poppins, fontWeight = FontWeight.Bold)
        }
    }
}