package com.filkom.designimplementation.ui.feature.esitter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.common.AppBookingDate
import com.filkom.designimplementation.model.data.sitter.Sitter
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.ui.theme.TextPrimary
import com.filkom.designimplementation.utils.DateHelper
import com.filkom.designimplementation.viewmodel.feature.esitter.ESitterViewModel

// --- MAIN SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ESitterDetailScreen(
    viewModel: ESitterViewModel,
    onBack: () -> Unit,
    onBookNow: (Sitter, String, String) -> Unit
) {
    val sitter = viewModel.selectedSitter.collectAsState().value ?: return
    val scrollState = rememberScrollState()

    // Data Slot yang sudah dibooking dari Firebase
    val bookedSlots by viewModel.bookedSlots.collectAsState()

    // State Selection
    var selectedTime by remember { mutableStateOf<String?>(null) } // Default null biar user wajib pilih
    var selectedDateIndex by remember { mutableIntStateOf(0) }

    // Data Tanggal (7 Hari ke depan)
    val dates = remember { DateHelper.getNext7Days() }

    // Perhitungan Biaya
    val adminFee = 7000.0
    val totalPrice = sitter.price + adminFee

    // 1. Fetch Data Booking setiap tanggal berubah
    LaunchedEffect(selectedDateIndex) {
        val dateFull = dates[selectedDateIndex].fullDate
        viewModel.fetchBookedSlots(sitter.id, dateFull)

        // UX Improvement: Reset jam saat ganti tanggal
        selectedTime = null
    }

    // 2. Logic Generate Jam (Gabungan Waktu & Database)
    val timeSlots = remember(selectedDateIndex, bookedSlots) {
        val isToday = (selectedDateIndex == 0)
        // Ambil jam dasar (09.00 - 20.00)
        val rawSlots = DateHelper.generateTimeSlots(isToday)

        // Filter: Abu-abukan jika SUDAH LEWAT atau SUDAH DIBOOKING
        rawSlots.map { slot ->
            val isBooked = bookedSlots.contains(slot.time)
            slot.copy(isAvailable = slot.isAvailable && !isBooked)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        bottomBar = {
            ConsistentBookingBottomBar(
                sitterPrice = sitter.price,
                adminFee = adminFee,
                totalPrice = totalPrice,
                isButtonEnabled = selectedTime != null,
                onBookNow = {
                    selectedTime?.let { time ->
                        onBookNow(sitter, dates[selectedDateIndex].fullDate, time)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(scrollState)
        ) {
            // 1. Header (Image + Stats Card)
            ProfileHeaderSection(sitter = sitter, onBack = onBack)

            Spacer(Modifier.height(20.dp))

            // 2. Nama & Rating
            SectionNameRate(sitter = sitter)

            // 3. Pilih Tanggal
            SectionTitle("Pilih Tanggal")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dates.size) { index ->
                    DateSelectorItem(
                        dateObj = dates[index],
                        isSelected = index == selectedDateIndex,
                        onClick = { selectedDateIndex = index }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // 4. Pilih Jam
            SectionTitle("Pilih Jam")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(timeSlots) { slot ->
                    TimeSelectorItem(
                        time = slot.time,
                        isEnabled = slot.isAvailable, // Ini yang bikin abu-abu/disable
                        isSelected = selectedTime == slot.time,
                        onClick = {
                            if(slot.isAvailable) selectedTime = slot.time
                        }
                    )
                }
            }

            // Spacer Bawah
            Spacer(Modifier.height(40.dp))
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun ProfileHeaderSection(sitter: Sitter, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Gambar Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
        ) {
            AsyncImage(
                model = sitter.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            )
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f))
            )
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 8.dp, top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(80.dp)
                .offset(y = -(20.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Mengasuh", "${sitter.completedJobs}+")
                VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFEEEEEE))
                StatItem("Pengalaman", sitter.experience)
                VerticalDivider(modifier = Modifier.height(40.dp), color = Color(0xFFEEEEEE))
                StatItem("Lokasi", sitter.location)
            }
        }
    }
}

@Composable
fun SectionNameRate(sitter: Sitter) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = sitter.name,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = TextPrimary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            repeat(5) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${"%.1f".format(sitter.rating)} (${sitter.reviewCount} Ulasan)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextPrimary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontFamily = Poppins)
        Spacer(Modifier.height(4.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Poppins, color = Pink)
    }
}

@Composable
fun DateSelectorItem(dateObj: AppBookingDate, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .height(84.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Pink else Color.White)
            .border(1.dp, if (isSelected) Pink else Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dateObj.day,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Pink
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = dateObj.date,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isSelected) Color.White else Pink
            )
        }
    }
}

@Composable
fun TimeSelectorItem(
    time: String,
    isEnabled: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Logic Warna (Pink=Pilih, Abu Muda=Disable, Putih=Normal)
    val backgroundColor = when {
        isSelected -> Pink
        !isEnabled -> Color(0xFFF0F0F0)
        else -> Color.White
    }
    val textColor = when {
        isSelected -> Color.White
        !isEnabled -> Color.LightGray
        else -> Color.Gray
    }
    val borderColor = if (isSelected) Pink else Color(0xFFE0E0E0)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(1.dp, if(isEnabled) borderColor else Color.Transparent, RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = isEnabled
            ) { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(
            text = time,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

@Composable
fun ConsistentBookingBottomBar(
    sitterPrice: Double,
    adminFee: Double,
    totalPrice: Double,
    isButtonEnabled: Boolean, // Tambahan parameter biar tombol bisa disable kalau belum pilih jam
    onBookNow: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 20.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
            Text("Biaya Jasa", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Text("Berikut adalah rincian biaya pemesanan", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)

            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

            PriceRow("Biaya Jasa", sitterPrice)
            Spacer(Modifier.height(8.dp))
            PriceRow("Biaya Admin", adminFee)

            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Pembayaran", fontSize = 12.sp, fontFamily = Poppins, color = Color.Gray)
                    Text(formatRupiah(totalPrice), fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Poppins, color = Pink)
                }

                Button(
                    onClick = onBookNow,
                    enabled = isButtonEnabled, // Tombol mati kalau jam belum dipilih
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Pink,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(50.dp).width(160.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text("Buat Janji", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PriceRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontFamily = Poppins, fontSize = 14.sp, color = Color.Gray)
        Text(formatRupiah(amount), fontFamily = Poppins, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}