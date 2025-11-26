package com.filkom.designimplementation.ui.feature.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage // PENTING: Import Coil
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.product.CartItem
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.shop.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = viewModel(),
    onBack: () -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsState()

    val totalPrice = cartItems.filter { it.isSelected }.sumOf { it.price * it.quantity }
    val totalItems = cartItems.filter { it.isSelected }.sumOf { it.quantity }
    val isAllSelected = cartItems.isNotEmpty() && cartItems.all { it.isSelected }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "Keranjang",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = Pink
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Pink
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Total Barang: ${cartItems.size}",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            items(cartItems, key = { it.id }) { item ->
                CartItemCard(
                    item = item,
                    onQuantityChange = { newQty -> viewModel.updateQuantity(item.id, newQty) },
                    onCheckedChange = { viewModel.toggleSelection(item.id) }
                )
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        CartBottomBar(
            totalPrice = totalPrice,
            totalItems = totalItems,
            isAllSelected = isAllSelected,
            onSelectAll = { select -> viewModel.toggleSelectAll(select) },
            onBuyClick = onCheckout
        )
    }
}

@Composable
fun CartBottomBar(
    totalPrice: Double,
    totalItems: Int,
    isAllSelected: Boolean,
    onSelectAll: (Boolean) -> Unit,
    onBuyClick: () -> Unit = {}
) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().coerceAtLeast(16.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomCheckbox(checked = isAllSelected, onCheckedChange = onSelectAll)
            Spacer(Modifier.width(8.dp))
            Text("Semua", fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)

            Spacer(Modifier.weight(1f))

            // Total Price
            Column(horizontalAlignment = Alignment.End) {
                Text("Total", fontSize = 10.sp, fontFamily = Poppins, color = Color.Gray)
                Text(
                    text = formatRupiah(totalPrice),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(Modifier.width(16.dp))

            // Button Beli
            Button(
                onClick = onBuyClick,
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    "Beli ($totalItems)",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// --- Komponen Pendukung ---

@Composable
fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 1. Checkbox & Image (UPDATED WITH COIL)
                Box(contentAlignment = Alignment.TopStart) {
                    // GANTI Image biasa dengan AsyncImage untuk URL
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_launcher_background), // Gambar loading
                        error = painterResource(R.drawable.ic_launcher_background), // Gambar jika error
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray.copy(alpha = 0.1f))
                    )

                    // Checkbox Overlay
                    Box(
                        modifier = Modifier
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomEnd = 8.dp))
                            .background(Color.White)
                            .padding(2.dp)
                    ) {
                        CustomCheckbox(checked = item.isSelected, onCheckedChange = onCheckedChange)
                    }
                }

                Spacer(Modifier.width(12.dp))

                // 2. Info Produk
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color(0xFF333333)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(item.rating.toString(), fontFamily = Poppins, fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(formatRupiah(item.price), fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Text("Diskon 33%", fontSize = 10.sp, color = Color(0xFFFF3B30), fontFamily = Poppins, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.width(6.dp))
                        Text(formatRupiah(item.originalPrice), fontSize = 10.sp, color = Color.Gray, textDecoration = TextDecoration.LineThrough, fontFamily = Poppins)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            // 3. Footer Card (Quantity & Subtotal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Jumlah Order:", fontSize = 10.sp, fontFamily = Poppins, color = Color.Gray)
                    Text(formatRupiah(item.price * item.quantity), fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Pink)
                }
                QuantitySelector(qty = item.quantity, onQtyChange = onQuantityChange)
            }
        }
    }
}

@Composable
fun QuantitySelector(qty: Int, onQtyChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, if (qty > 1) Pink else Color.LightGray, RoundedCornerShape(6.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = qty > 1
                ) { onQtyChange(qty - 1) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Remove, null, tint = if (qty > 1) Pink else Color.LightGray, modifier = Modifier.size(16.dp))
        }
        Text(text = "$qty", modifier = Modifier.width(32.dp), textAlign = TextAlign.Center, fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Pink)
                .clickable (
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onQtyChange(qty + 1) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun CustomCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (checked) Pink else Color.Transparent)
            .border(1.5.dp, if (checked) Pink else Color(0xFFCCCCCC), RoundedCornerShape(6.dp))
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ){ onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}