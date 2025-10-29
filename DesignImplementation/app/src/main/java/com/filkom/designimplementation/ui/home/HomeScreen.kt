package com.filkom.designimplementation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.typography.Poppins

@Composable
fun HomeScreen(
    onOpenLittleAI: () -> Unit = {},
    onNavigate: (String) -> Unit = {} // "home","history","cart","profile"
) {
    Scaffold(
        bottomBar = {
            HomeBottomBar(
                onHome = { onNavigate("home") },
                onHistory = { onNavigate("history") }, // TODO: implement destination
                onLittleAI = onOpenLittleAI,
                onCart = { onNavigate("cart") },       // TODO: implement destination
                onProfile = { onNavigate("profile") }  // TODO: implement destination
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ===== Header (reuse header.png) =====
            Image(
                painter = painterResource(R.drawable.header),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            // ===== Greeting + saldo card (placeholder) =====
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Hi, Antony!",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF222222),
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFDE5F2),
                    tonalElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // TODO: ganti avatar dengan foto pengguna / drawable sendiri
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC1E3))
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Total Saldo", fontFamily = Poppins, fontSize = 12.sp, color = Color(0xFF6A6A6B))
                            Text("Rp 1.000.000,00", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                        }
                        AssistChip(onClick = { /* TODO: isi saldo */ }, label = { Text("Isi Saldo") })
                        Spacer(Modifier.width(8.dp))
                        AssistChip(onClick = { /* TODO: tukar poin */ }, label = { Text("Tukar") })
                    }
                }
            }

            // ===== Little-AI (fokus utama) =====
            Text(
                text = "Little-AI",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color(0xFF222222),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clickable { onOpenLittleAI() }
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // GUNAKAN DRAWABLE LITTLE AI
                    Image(
                        painter = painterResource(R.drawable.littleai),
                        contentDescription = "Little-AI",
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFFDE5F2), CircleShape)
                            .padding(6.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Tanyakan apa saja tentang si kecil", fontFamily = Poppins, fontWeight = FontWeight.Medium)
                        Text(
                            "Contoh: pola tidur, jadwal imunisasi, ide MPASI…",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = Color(0xFF6A6A6B)
                        )
                    }
                    FilledTonalButton(
                        onClick = onOpenLittleAI,
                        shape = RoundedCornerShape(30),
                    ) { Text("Mulai", fontFamily = Poppins) }
                }
            }

            // ===== Kategori (placeholder) =====
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Kategori",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF222222),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CategoryItem("E-Sitter")   // TODO: set icon drawable
                CategoryItem("Konsultasi") // TODO: set icon drawable
                CategoryItem("Belanja")    // TODO: set icon drawable
                CategoryItem("Daycare")    // TODO: set icon drawable
            }

            // ===== Banner & rekomendasi (placeholder) =====
            Spacer(Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                // TODO: masukkan gambar banner rekomendasi
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Banner rekomendasi (TODO)", fontFamily = Poppins, color = Color(0xFF8F8F93))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CategoryItem(title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // TODO: ganti dengan icon kategori (drawable) sesuai desain
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF1F1F1)),
            contentAlignment = Alignment.Center
        ) { Text("🔖", fontSize = 18.sp) } // placeholder emoji
        Spacer(Modifier.height(6.dp))
        Text(title, fontFamily = Poppins, fontSize = 12.sp, color = Color(0xFF6A6A6B))
    }
}

@Composable
private fun HomeBottomBar(
    onHome: () -> Unit,
    onHistory: () -> Unit,
    onLittleAI: () -> Unit,
    onCart: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = true, onClick = onHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) }, // TODO: ganti drawable ic_home
            label = { Text("Home", fontFamily = Poppins) }
        )
        NavigationBarItem(
            selected = false, onClick = onHistory,
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }, // TODO: ganti drawable ic_history
            label = { Text("Histori", fontFamily = Poppins) }
        )
        // Tombol tengah: Little-AI (DRAWABLE)
        NavigationBarItem(
            selected = false, onClick = onLittleAI,
            icon = {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF987C5)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_littlesteps_logo),
                        contentDescription = "Little-AI",
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            label = { Text("Little-AI", fontFamily = Poppins) }
        )
        NavigationBarItem(
            selected = false, onClick = onCart,
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }, // TODO: ganti drawable ic_cart
            label = { Text("Keranjang", fontFamily = Poppins) }
        )
        NavigationBarItem(
            selected = false, onClick = onProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) }, // TODO: ganti drawable ic_profile
            label = { Text("Profile", fontFamily = Poppins) }
        )
    }
}
