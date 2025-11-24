package com.filkom.designimplementation.ui.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.history.HistoryTransaction
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.history.HistoryViewModel
import androidx.compose.foundation.clickable
import com.filkom.designimplementation.ui.components.formatRupiah
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.text.style.TextAlign
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
) {
    val historyItems by viewModel.historyItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedProductId by remember { mutableStateOf("") }
    var selectedTransactionId by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }

    if (showRatingDialog) {
        RatingDialog(
            onDismiss = { showRatingDialog = false },
            onSubmit = { rating ->
                // Panggil ViewModel dengan ID Produk yang benar
                viewModel.submitReview(selectedTransactionId, selectedProductId, rating, selectedCategory)
                showRatingDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text("Riwayat", fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Pink)
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        // 2. LIST CONTENT
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Pink)
            }
        } else if (historyItems.isEmpty()) {
            EmptyHistoryState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyItems) { item ->
                    HistoryItemCard(
                        item = item,
                        onRateClick = {
                            selectedProductId = item.productId
                            selectedTransactionId = item.id
                            selectedCategory = item.category
                            showRatingDialog = true
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun HistoryItemCard(
    item: HistoryTransaction,
    onRateClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // --- HEADER (Kategori & Tanggal) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_littlesteps_logo),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = item.category,
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6A6A6B)
                    )
                }
                // Tanggal transaksi di pojok kanan atas
                Text(
                    text = item.date,
                    fontFamily = Poppins,
                    fontSize = 10.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

            // --- BODY (Gambar & Judul) ---
            Row(
                verticalAlignment = Alignment.CenterVertically, // Tetap di tengah secara vertikal agar rapi
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background),
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray.copy(alpha = 0.1f))
                )

                Spacer(Modifier.width(16.dp))

                // KOLOM TEXT JUDUL
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    Text(
                        text = item.title,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,


                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp,

                        color = Color(0xFF333333)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "ID: ${item.historyId}",
                        fontFamily = Poppins,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Belanja", fontFamily = Poppins, fontSize = 10.sp, color = Color.Gray)
                    Text(
                        text = formatRupiah(item.total),
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                }

                if (item.status == "Berhasil") {
                    if (item.category.equals("Donasi", ignoreCase = true)) {
                        StatusBadge(status = item.status)
                    }
                    else if (item.reviewed) {
                        Surface(
                            color = Color(0xFFEEEEEE),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Ulasan Terkirim",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                fontFamily = Poppins,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onRateClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp),
                            border = BorderStroke(1.dp, Pink)
                        ) {
                            Text("Beri Ulasan", fontSize = 10.sp, color = Pink, fontFamily = Poppins)
                        }
                    }
                } else {
                    StatusBadge(status = item.status)
                }
            }
        }
    }
}
@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status) {
        "Berhasil", "Selesai" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Diproses", "Pending" -> Pair(Color(0xFFFFF3E0), Color(0xFFEF6C00))
        "Dibatalkan", "Gagal" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Pair(Color.LightGray, Color.Black)
    }

    Surface(color = bgColor, shape = RoundedCornerShape(6.dp)) {
        Text(
            text = status,
            color = textColor,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun EmptyHistoryState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Belum ada riwayat transaksi", fontFamily = Poppins, color = Color.Gray)
    }
}
@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    var selectedStars by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Beri Ulasan",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Bagaimana kualitas produk ini?",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        val isSelected = i <= selectedStars
                        val icon = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star
                        val tint = if (isSelected) Color(0xFFFFC107) else Color.Gray

                        Icon(
                            imageVector = icon,
                            contentDescription = "Star $i",
                            tint = tint,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedStars = i
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedStars) },
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(24.dp),
                enabled = selectedStars > 0,
                modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
            ) {
                Text("Kirim", fontFamily = Poppins, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text("Batal", color = Color.Gray, fontFamily = Poppins)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}