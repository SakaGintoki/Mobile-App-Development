package com.filkom.designimplementation.ui.feature.consultation

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PregnantWoman
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.consultation.Doctor
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.ui.theme.TextPrimary
import com.filkom.designimplementation.viewmodel.data.UserDataViewModel
import com.filkom.designimplementation.viewmodel.feature.consultation.ConsultationViewModel

@Composable
fun ConsultationListScreen(
    viewModel: ConsultationViewModel,
    viewModelUser: UserDataViewModel = viewModel(),
    onBack: () -> Unit,
    onDoctorClick: (String) -> Unit,
    onChatClick: (String) -> Unit
) {
    val doctors by viewModel.doctors.collectAsState()
    val user by viewModelUser.userState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchDoctors()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ConsultationTopBar(onBack = onBack, user?.name ?: "")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // 1. Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { newQuery ->
                    searchQuery = newQuery
                    viewModel.searchDoctors(newQuery) // Panggil VM
                }
            )

            Spacer(Modifier.height(24.dp))

            // 2. Section Rekomendasi Dokter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rekomendasi dokter",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Konsultasi online dengan dokter terpercaya",
                        fontFamily = Poppins,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "Lihat semua",
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color(0xFF9C27B0), // Warna Ungu
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable (
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { }
                )
            }

            Spacer(Modifier.height(16.dp))

            // 3. List Dokter (Card)
            if (isLoading) {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Pink)
                }
            } else {
                if (doctors.isEmpty()) {
                    Text(
                        text = "Belum ada rekomendasi saat ini.",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = Poppins
                    )
                } else {
                    doctors.forEach { doctor ->
                        DoctorListItem(
                            doctor = doctor,
                            onClick = { onDoctorClick(doctor.id) },
                            onChatClick = {
                                val currentUserId = user?.id ?: ""

                                viewModel.checkActiveSession(doctor.id, currentUserId) { isActive ->
                                    if (isActive) {
                                        val encodedName = Uri.encode(doctor.name)
                                        val encodedImage = Uri.encode(doctor.imageUrl)
                                        val encodedSpecialization = Uri.encode(doctor.specialization)

                                        val route = "chat_room/${doctor.id}/$encodedName?doctorImage=$encodedImage&doctorSpecialization=$encodedSpecialization"
                                        onChatClick(route)
                                    } else {
                                        Toast.makeText(context, "Sesi konsultasi belum dimulai atau sudah berakhir.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            /*
            Text(
                text = "Cari Dokter yang sesuai",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
            Text(
                text = "Pilih kategori yang tersedia sesuai kondisimu",
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryCircle(Icons.Default.MedicalServices, "Umum")
                CategoryCircle(Icons.Default.ChildCare, "Anak")
                CategoryCircle(Icons.Default.PregnantWoman, "Kandungan")
                CategoryCircle(Icons.Default.GridView, "Lainnya")
            }
            */

            Spacer(Modifier.height(40.dp))
        }
    }
}

// --- COMPONENTS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationTopBar(
    onBack: () -> Unit,
    name : String
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
        title = {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF673AB7), fontWeight = FontWeight.Bold)) { // Ungu Tua
                        append("Selamat Pagi, ")
                    }
                    withStyle(style = SpanStyle(color = Pink, fontWeight = FontWeight.Bold)) {
                        append("$name!")
                    }
                },
                fontSize = 16.sp,
                fontFamily = Poppins,
                lineHeight = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Pink)
            }
        },
        actions = {
            IconButton(onClick = {}, modifier = Modifier.padding(end = 12.dp)) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = Pink)
            }
        }
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp, // Kasih bayangan dikit biar pop-up
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        // Gunakan TextField, bukan Row biasa
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text("Cari dokter atau spesialis...", color = Color.LightGray, fontSize = 14.sp, fontFamily = Poppins)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            // Styling agar terlihat clean (tanpa garis bawah)
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
                fontSize = 14.sp,
                color = TextPrimary
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DoctorListItem(
    doctor: Doctor,
    onClick: () -> Unit,
    onChatClick: () -> Unit // Parameter Callback Chat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = doctor.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 80.dp, height = 100.dp) // Ukuran portrait
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background)
            )

            Spacer(Modifier.width(16.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doctor.name,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = doctor.specialization,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                // Chips (Pengalaman & Rating)
                Row {
                    SmallChip(Icons.Default.Work, doctor.experience) // 7 Tahun
                    Spacer(Modifier.width(8.dp))
                    // Konversi rating 0-5 ke persen (misal 4.8 -> 96%)
                    val percentage = if(doctor.rating > 0) (doctor.rating / 5.0 * 100).toInt() else 0
                    SmallChip(Icons.Default.ThumbUp, "$percentage%") // 95%
                }

                Spacer(Modifier.height(12.dp))

                // Harga & Tombol Chat
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatRupiah(doctor.price),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = Pink,
                        fontSize = 14.sp
                    )

                    // Tombol Chat Kecil
                    Button(
                        onClick = onChatClick, // Panggil Callback
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE086D3)), // Pink agak ungu
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Chat", fontSize = 12.sp, fontFamily = Poppins, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SmallChip(icon: ImageVector, text: String) {
    Surface(
        color = Color(0xFFF5F5F5), // Abu sangat muda
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(4.dp))
            Text(text, fontSize = 10.sp, color = Color.Gray, fontFamily = Poppins)
        }
    }
}

@Composable
fun CategoryCircle(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEBF0)), // Pink sangat muda background
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Pink, // Icon warna pink
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = TextPrimary
        )
    }
}