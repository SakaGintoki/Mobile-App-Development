package com.filkom.designimplementation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.ui.theme.Pink // Pastikan warna Pink diimport
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.ui.theme.TextClr
import com.filkom.designimplementation.ui.theme.primaryGradient

@Composable
fun MainBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onOpenLittleAI: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp), // Tinggi total dilebihkan sedikit
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. LAYER BELAKANG: Bar Putih
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Tinggi asli bar putih
                .shadow(16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Kiri: Home & History
                BottomBarItem(
                    icon = if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    label = "Home",
                    isSelected = currentRoute == "home",
                    onClick = { onNavigate("home") }
                )
                BottomBarItem(
                    icon = if (currentRoute == "history") Icons.Filled.History else Icons.Outlined.History,
                    label = "Histori",
                    isSelected = currentRoute == "history",
                    onClick = { onNavigate("history") }
                )

                // Spacer Tengah (Tempat Tombol Besar)
                Spacer(modifier = Modifier.width(56.dp))

                // Kanan: Keranjang & Profile
                BottomBarItem(
                    icon = if (currentRoute == "cart") Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                    label = "Keranjang",
                    isSelected = currentRoute == "cart",
                    onClick = { onNavigate("cart") }
                )
                BottomBarItem(
                    icon = if (currentRoute == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                    label = "Profile",
                    isSelected = currentRoute == "profile",
                    onClick = { onNavigate("profile") }
                )
            }
        }

        // 2. LAYER DEPAN: Tombol Besar (Little AI)
        // Kita taruh di luar Surface putih agar tidak terpotong
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter) // Tempel ke atas container transparan
                .offset(y = 4.dp) // Atur posisi naik/turunnya disini
                .size(72.dp) // Ukuran tombol besar
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(brush = Brush.linearGradient(colors = primaryGradient)) // Warna Pink
                .clickable { onOpenLittleAI() },
            contentAlignment = Alignment.Center
        ) {
            // Icon di dalam tombol besar
            Icon(
                painter = painterResource(R.drawable.ic_littlesteps_logo_white_notext), // Pastikan icon ada
                contentDescription = "Little AI",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun BottomBarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = if (isSelected) Pink else Color(0xFF9E9E9E)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Hilangkan ripple effect agar clean
            ) { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
    }
}