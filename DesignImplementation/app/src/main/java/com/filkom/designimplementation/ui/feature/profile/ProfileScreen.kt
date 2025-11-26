package com.filkom.designimplementation.ui.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.auth.User
import com.filkom.designimplementation.ui.theme.*
import com.filkom.designimplementation.viewmodel.feature.profile.ProfileUiState
import com.filkom.designimplementation.viewmodel.feature.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigate: (String) -> Unit = {},
    onOpenLittleAI: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onTermsAndConditions: () -> Unit = {},
    onSettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Gunakan Box sebagai Container Utama agar bisa menumpuk Header & Konten
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. HEADER IMAGE (Layer Belakang)
        // Tidak pakai padding status bar, agar tetap nempel di ujung atas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.header),
                contentDescription = "Header Gradient",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.85f)
            )
        }

        // 2. KONTEN UTAMA (Layer Depan)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                // PERBAIKAN: Tambahkan ini agar konten turun melewati Status Bar
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== Header Title =====
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Profil",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = TextClr
            )
            Spacer(Modifier.height(24.dp))

            when (uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(color = Pink, modifier = Modifier.padding(top = 50.dp))
                }
                is ProfileUiState.Error -> {
                    Text(
                        text = "Gagal memuat profil. Silakan login ulang.",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 50.dp)
                    )
                }
                is ProfileUiState.Success -> {
                    val user = (uiState as ProfileUiState.Success).user
                    ProfileContent(
                        user = user,
                        onEditProfile = onEditProfile,
                        onTerms = onTermsAndConditions,
                        onSettings = onSettings,
                        onLogoutClick = {
                            viewModel.logout()
                            onLogout()
                        }
                    )
                }
            }

            // Spacer agar konten paling bawah tidak tertutup Bottom Bar yang melayang
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    onEditProfile: () -> Unit,
    onTerms: () -> Unit,
    onSettings: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // ===== 1. HEADER INFO (Foto, Nama Besar, Email) =====
//        Image(
//            painter = painterResource(R.drawable.ic_launcher_background),
//            contentDescription = "Foto Profil",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(100.dp)
//                .clip(CircleShape)
//        )
        AsyncImage(
            model = user.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
            ,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_launcher_background),
            error = painterResource(R.drawable.ic_launcher_background)
        )
        Spacer(Modifier.height(16.dp))

        // Nama Besar (Judul)
        Text(
            text = user.name ?: "Tanpa Nama",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = TextClr
        )

        // Email Kecil (Subtitle)
        Text(
            text = user.email ?: "-",
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = Color(0xFF6A6A6B)
        )

        Spacer(Modifier.height(16.dp))

        // Tombol Edit
        Button(
            onClick = onEditProfile,
            colors = ButtonDefaults.buttonColors(containerColor = Pink),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Edit Profil", fontFamily = Poppins, fontSize = 12.sp)
        }

        Spacer(Modifier.height(32.dp))

        // ===== 2. SECTION DETAIL AKUN (List Lengkap) =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Detail Akun",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = TextClr
            )
            Spacer(Modifier.height(16.dp))

            // List Field Item
            ProfileFieldItem(label = "Nama Lengkap", value = user.name ?: "-")
            ProfileFieldItem(label = "Email", value = user.email ?: "-")
            ProfileFieldItem(label = "Username", value = user.username.ifEmpty { "-" })
            ProfileFieldItem(label = "No. Handphone", value = user.phone.ifEmpty { "-" })
        }

        Spacer(Modifier.height(24.dp))

        // ===== 3. TOMBOL OPSI (Syarat, Setting, Keluar) =====
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            ProfileOptionItem(
                icon = Icons.Outlined.Flag,
                text = "Syarat dan Ketentuan",
                onClick = onTerms
            )
            Spacer(Modifier.height(12.dp))
            ProfileOptionItem(
                icon = Icons.Outlined.Settings,
                text = "Pengaturan",
                onClick = onSettings
            )
            Spacer(Modifier.height(12.dp))

            ProfileOptionItem(
                icon = Icons.AutoMirrored.Outlined.ExitToApp,
                text = "Keluar",
                isDestructive = true,
                onClick = onLogoutClick
            )
        }
    }
}

// ================= KOMPONEN PENDUKUNG =================

@Composable
fun ProfileFieldItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = Color(0xFF8F8F93)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = TextClr,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    text: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val contentColor = if (isDestructive) Color(0xFFFF3B30) else TextClr
    val borderColor = if (isDestructive) Pink else Color(0xFFE5E5E5)

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = contentColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}