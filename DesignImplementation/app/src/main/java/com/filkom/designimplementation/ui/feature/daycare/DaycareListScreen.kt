package com.filkom.designimplementation.ui.feature.daycare

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.model.data.daycare.Daycare
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.ui.theme.TextPrimary
import com.filkom.designimplementation.viewmodel.data.UserDataViewModel
import com.filkom.designimplementation.viewmodel.feature.daycare.DaycareViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaycareListScreen(
    viewModel: DaycareViewModel,
    viewModelUser: UserDataViewModel = viewModel(),
    onBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val daycares by viewModel.daycares.collectAsState()
    val user by viewModelUser.userState.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchDaycares()

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.startLocationUpdates(context)
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color(0xFF673AB7), fontWeight = FontWeight.Bold)) { // Ungu Tua
                                append("Halo, ")
                            }
                            withStyle(style = SpanStyle(color = Pink, fontWeight = FontWeight.Bold)) {
                                append("${user?.name}!")
                            }
                        },
                        fontSize = 16.sp,
                        fontFamily = Poppins,
                        lineHeight = 20.sp
                    )                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Pink)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 4.dp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                            viewModel.searchDaycares(newValue)
                        },
                        placeholder = {
                            Text(
                                "Cari lokasi atau nama daycare...",
                                fontFamily = Poppins,
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = Color.Gray)
                        },
                        // Hilangkan underline/background bawaan TextField
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
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            var selectedFilter by remember { mutableStateOf("Semua") }
            val filters = listOf("Semua", "Terdekat", "Termurah", "Rating 4+")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = filter == selectedFilter

                    // Ganti FilterChipItem dengan versi interaktif
                    FilterChipItem(
                        text = filter,
                        isSelected = isSelected,
                        onClick = { // <--- Tambahkan parameter onClick
                            selectedFilter = filter
                            viewModel.applyFilter(filter)
                        }
                    )
                }
            }

            // 3. List Items
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(daycares) { daycare ->
                    DaycareCard(daycare, onClick = { onItemClick(daycare.id) })
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Pink else Color.White)
            .border(1.dp, if (isSelected) Pink else Color(0xFFE0E0E0), RoundedCornerShape(50))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() } // Panggil onClick disini
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun DaycareCard(daycare: Daycare, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Shadow lembut
    ) {
        Column {
            // --- IMAGE SECTION ---
            Box(modifier = Modifier.height(180.dp)) {
                AsyncImage(
                    model = daycare.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Rating Badge (Kiri Atas)
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${daycare.rating}",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f)) // Semi transparan
                        .clickable (
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ){ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // --- CONTENT SECTION ---
            Column(modifier = Modifier.padding(16.dp)) {
                // Nama & Lokasi
                Text(
                    text = daycare.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

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
                Spacer(Modifier.height(12.dp))

                HorizontalDivider(color = Color(0xFFF5F5F5))

                Spacer(Modifier.height(12.dp))

                // Fasilitas Preview (Icon + Teks)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tampilkan 2-3 fasilitas pertama saja agar tidak penuh
                    val displayFacilities = daycare.facilities.take(3)
                    displayFacilities.forEach { facility ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Icon(Icons.Outlined.CheckCircle, null, tint = Pink, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(facility, fontSize = 11.sp, fontFamily = Poppins, color = Color.Gray)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Harga (Kanan Bawah)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Mulai dari", fontSize = 10.sp, color = Color.Gray, fontFamily = Poppins)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = formatRupiah(daycare.price),
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Bold,
                                color = Pink,
                                fontSize = 16.sp
                            )
                            Text(
                                text = " /${daycare.priceUnit}",
                                fontFamily = Poppins,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                    }

                    // Tombol Kecil "Lihat"
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCE4EC)), // Pink sangat muda
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Lihat", fontSize = 12.sp, color = Pink, fontFamily = Poppins, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}