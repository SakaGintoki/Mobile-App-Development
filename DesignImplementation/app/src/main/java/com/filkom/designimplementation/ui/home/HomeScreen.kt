package com.filkom.designimplementation.ui.home

import android.widget.Toast
import androidx.camera.core.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.ui.theme.*
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.getValue
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
        ) {
            // ===== Header (reuse header.png) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Image(
                    painter = painterResource(R.drawable.header),
                    contentDescription = "Header Gradient",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                        .offset(y = (-90).dp)
                )
                Image(
                    painter = painterResource(R.drawable.ic_littlesteps_logo_notext),
                    contentDescription = "Blur Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                        .blur(8.dp)
                        .alpha(.6f)
                      .offset(x = (-150).dp, y = (-25).dp)
                )
            }
        }
        // ===== Greeting + saldo card (placeholder) =====
        Column(
            Modifier
                .padding(16.dp)
                .offset(y = 100.dp)

        ) {
            Box (Modifier.fillMaxWidth() ,contentAlignment = Alignment.CenterEnd) {
                Row {
                    Icon(
                        imageVector = Icons.Filled.Mail,
                        contentDescription = "Mail",
                        modifier = Modifier
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.linearGradient(colorStops = primaryGradient),
                                    blendMode = BlendMode.SrcIn
                                )
                            }
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.linearGradient(colorStops = primaryGradient),
                                    blendMode = BlendMode.SrcIn
                                )
                            }
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Surface(
                color = Color.Transparent,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(colorStops = primaryGradient),
                        shape = RoundedCornerShape(16.dp)
                    )

            ) {
                Column (
                    Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row {
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFC1E3))

                        )
                        Spacer(Modifier.width(16.dp))
                        Column (Modifier.weight(1f).padding(top = 6.dp)) {
                            Text("Hi, Antony!", fontFamily = Poppins, fontSize = 14.sp, color = TextClr, fontWeight = FontWeight.SemiBold)
                            Text("1234567890", fontSize = 9.sp,fontFamily = Poppins, color = TextClr)
                        }
                        Column (Modifier.weight(1f).padding(top = 6.dp), horizontalAlignment = Alignment.End) {
                            Text(
                                "Poin",
                                textAlign = TextAlign.Center,
                                fontFamily = Poppins,
                                fontSize = 9.sp,
                                color = TextClr,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("460",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontFamily = Poppins,
                                color = TextClr,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))


                    Spacer(Modifier.height(16.dp))

                    Surface(
                        color = Color.Transparent,
                        tonalElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(colorStops = lightPurpleGradient),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Row {
                            Column(Modifier.weight(1f).padding(12.dp)) {
                                Text(
                                    "Total Saldo",
                                    fontFamily = Poppins,
                                    fontSize = 12.sp,
                                    color = TextClr
                                )
                                Text(
                                    "Rp 1.000.000,00",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Normal,
                                    color = TextClr
                                )
                            }
                            Row (Modifier.padding(top = 8.dp))
                            {
                                Button(
                                    onClick = {/* TODO: tukar poin */ },
                                    contentPadding = PaddingValues(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_saldo),
                                            contentDescription = "Image",
                                            modifier = Modifier
                                                .height(26.dp)
                                                .width(26.dp)
                                        )
                                        Text(
                                            "Isi Saldo",
                                            style = TextStyle(fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center,
                                            fontSize = 10.sp,
                                            color = TextClr
                                        )
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = {/* TODO: tukar poin */ },
                                    contentPadding = PaddingValues(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_tukar),
                                            contentDescription = "Image",
                                            modifier = Modifier
                                                .height(26.dp)
                                                .width(26.dp)
                                        )
                                        Text(
                                            "Tukar",
                                            style = TextStyle(fontWeight = FontWeight.Bold),
                                            textAlign = TextAlign.Center,
                                            fontSize = 10.sp,
                                            color = TextClr
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
//            // ===== Little-AI (fokus utama) =====
//            Text(
//                text = "Little-AI",
//                fontFamily = Poppins,
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 18.sp,
//                color = Color(0xFF222222),
//                modifier = Modifier.padding(horizontal = 16.dp)
//            )
//            Spacer(Modifier.height(8.dp))
//            Surface(
//                shape = RoundedCornerShape(20.dp),
//                color = Color.White,
//                tonalElevation = 2.dp,
//                modifier = Modifier
//                    .padding(horizontal = 16.dp)
//                    .fillMaxWidth()
//                    .clickable { onOpenLittleAI() }
//            ) {
//                Row(
//                    Modifier.padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    // GUNAKAN DRAWABLE LITTLE AI
//                    Image(
//                        painter = painterResource(R.drawable.ic_littlesteps_logo_white_notext),
//                        contentDescription = "Little-AI",
//                        modifier = Modifier
//                            .size(48.dp)
//                            .background(Color(0xFFFDE5F2), CircleShape)
//                            .padding(2.dp)
//                    )
//                    Spacer(Modifier.width(12.dp))
//                    Column(Modifier.weight(1f)) {
//                        Text("Tanyakan apa saja tentang si kecil", fontFamily = Poppins, fontWeight = FontWeight.Medium)
//                        Text(
//                            "Contoh: pola tidur, jadwal imunisasi, ide MPASI…",
//                            fontFamily = Poppins,
//                            fontSize = 12.sp,
//                            color = Color(0xFF6A6A6B)
//                        )
//                    }
//                    FilledTonalButton(
//                        onClick = onOpenLittleAI,
//                        shape = RoundedCornerShape(30),
//                    ) { Text("Mulai", fontFamily = Poppins) }
//                }
//            }

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
            icon = { Icon(Icons.Filled.History, contentDescription = null) }, // TODO: ganti drawable ic_history
            label = { Text("Histori", fontFamily = Poppins, ) }
        )
        // Tombol tengah: Little-AI (DRAWABLE)
        NavigationBarItem(
            selected = false, onClick = onLittleAI,
            icon = {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(brush = Brush.linearGradient(colorStops = primaryGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_littlesteps_logo_white_notext),
                        contentDescription = "Little-AI",
                        modifier = Modifier.size(32.dp)
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
