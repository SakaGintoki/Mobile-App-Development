package com.filkom.designimplementation.ui.feature.daycare

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.common.AppBookingDate
import com.filkom.designimplementation.model.data.daycare.Daycare
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.LightPinkBg
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.ui.theme.SoftGray
import com.filkom.designimplementation.ui.theme.TextPrimary
import com.filkom.designimplementation.ui.theme.TextSecondary
import com.filkom.designimplementation.utils.DateHelper
import com.filkom.designimplementation.viewmodel.feature.daycare.DaycareViewModel
import java.util.Locale


// --- Warna Custom untuk UI Modern ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaycareDetailScreen(
    daycareId: String,
    viewModel: DaycareViewModel,
    onBack: () -> Unit,
    onBookNow: (Daycare, String) -> Unit
) {
    val daycare = viewModel.selectedDaycare.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    var selectedDateIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            viewModel.startLocationUpdates(context)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchDaycares()

        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation) {
            viewModel.startLocationUpdates(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    val dates = remember { DateHelper.getNext7Days() }

    LaunchedEffect(daycareId) {
        viewModel.getDaycareDetail(daycareId)
    }

    if (isLoading || daycare == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Pink)
        }
    } else {
        Scaffold(
            containerColor = Color.White,
            bottomBar = {
                DaycareBottomBar(
                    price = daycare.price,
                    unit = daycare.priceUnit,
                    onBook = { onBookNow(daycare, dates[selectedDateIndex].fullDate) }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Immersive Header Image
                Box(modifier = Modifier.height(280.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = daycare.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent, Color.White),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Pink)
                    }
                }

                // 2. Main Content - overlap sedikit ke atas (-30.dp) untuk efek modern
                Column(
                    modifier = Modifier
                        .offset(y = (-30).dp)
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    // Badge Kategori/Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = LightPinkBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Daycare Premium",
                                fontSize = 10.sp,
                                color = Pink,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${daycare.rating} (${daycare.reviewCount} Ulasan)", fontSize = 12.sp, color = TextSecondary, fontFamily = Poppins)
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(daycare.name, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextPrimary)

                    Spacer(Modifier.height(4.dp))
//
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(Icons.Outlined.LocationOn, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
//                        Spacer(Modifier.width(4.dp))
//                        Text(daycare.location, color = TextSecondary, fontSize = 12.sp, fontFamily = Poppins)
//                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))

                        // --- PERBAIKAN FORMAT JARAK ---
                        val locationText = if (daycare.distanceInKm != null) {
                            val distance = daycare.distanceInKm
                            // Format: Menggunakan Locale Indonesia agar pemisah desimalnya "KOMA" (,)
                            // %.1f artinya ambil 1 angka di belakang koma
                            val formattedDistance = String.format(Locale("id", "ID"), "%.1f", distance)

                            "$formattedDistance km • ${daycare.location}"
                        } else {
                            daycare.location
                        }

                        Text(
                            text = locationText,
                            fontFamily = Poppins,
                            color = Color.Gray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- DATE SELECTOR (Modern) ---
                    SectionHeader("Pilih Jadwal")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(dates.size) { index ->
                            DaySelectorItem(
                                dateObj = dates[index],
                                isSelected = index == selectedDateIndex,
                                onClick = { selectedDateIndex = index }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- TIME SLOT ---
                    SectionHeader("Durasi Penitipan")
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Pink),
                        color = LightPinkBg.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.AccessTime, null, tint = Pink)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Jam Operasional", fontSize = 10.sp, color = TextSecondary, fontFamily = Poppins)
                                Text("10.00 - 14.00 WIB", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Poppins)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- TENTANG ---
                    SectionHeader("Tentang Daycare")
                    Text(
                        text = daycare.description,
                        fontFamily = Poppins,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                    )

                    Spacer(Modifier.height(24.dp))

                    // --- FASILITAS ---
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        SectionHeader("Fasilitas Utama")
                        Text("Lihat Semua", color = Pink, fontSize = 12.sp, fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        FacilityItem(Icons.Outlined.AcUnit, "AC")
                        FacilityItem(Icons.Outlined.Bed, "Kasur")
                        FacilityItem(Icons.Outlined.Restaurant, "Makan")
                        FacilityItem(Icons.Outlined.Videocam, "CCTV")
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- INFO TAMBAHAN ---
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoBox(title = "Usia Anak", value = daycare.ageRange, icon = Icons.Outlined.ChildCare, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(12.dp))
                        InfoBox(title = "Kapasitas", value = "20 Anak", icon = Icons.Outlined.Groups, modifier = Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- ACTIVITY CARDS (Gradient) ---
                    SectionHeader("Aktivitas Anak")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        GradientActivityCard(
                            title = "Bermain & Gembira",
                            colors = listOf(Color(0xFFFFB74D), Color(0xFFFFA726)), // Orange Gradient
                            icon = Icons.Filled.Toys,
                            modifier = Modifier.weight(1f)
                        )
                        GradientActivityCard(
                            title = "Tumbuh Kembang",
                            colors = listOf(Color(0xFFEF5350), Color(0xFFE53935)), // Red Gradient
                            icon = Icons.Filled.AutoGraph,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

// ================= COMPONENTS =================

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun DaySelectorItem(dateObj: AppBookingDate, isSelected: Boolean, onClick: () -> Unit) {
    // Menggunakan Card dengan Shadow halus
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Pink else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFF0F0F0)) else null,
        modifier = Modifier.size(width = 65.dp, height = 85.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateObj.day,
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = dateObj.date,
                fontFamily = Poppins,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}

@Composable
fun FacilityItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(SoftGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Pink, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium, fontFamily = Poppins)
    }
}

@Composable
fun InfoBox(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SoftGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontSize = 10.sp, color = TextSecondary, fontFamily = Poppins)
                Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Poppins)
            }
        }
    }
}

@Composable
fun GradientActivityCard(title: String, colors: List<Color>, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(110.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors)) // Efek Gradient
        ) {
            // Pattern Circle transparan di belakang
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = Color.White.copy(alpha = 0.1f), center = center, radius = size.minDimension)
            }

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 16.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(28.dp).align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun DaycareBottomBar(price: Double, unit: String, onBook: () -> Unit) {
    Surface(
        shadowElevation = 24.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Mulai dari", fontSize = 12.sp, color = TextSecondary, fontFamily = Poppins)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(formatRupiah(price), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Pink, fontFamily = Poppins)
                    Text("/$unit", fontSize = 12.sp, color = TextSecondary, fontFamily = Poppins, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            Button(
                onClick = onBook,
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(160.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp) // Tombol melayang
            ) {
                Text("Pesan", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}