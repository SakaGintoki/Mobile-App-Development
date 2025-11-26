package com.filkom.designimplementation.ui.feature.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.product.Product
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.*
import com.filkom.designimplementation.viewmodel.feature.home.HomeUiState
import com.filkom.designimplementation.viewmodel.feature.home.HomeViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onOpenLittleAI: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onProductClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val mainScrollState = rememberScrollState()
    val categoryScrollState = rememberScrollState()
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {

        // 1. Header Background & Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.header),
                contentDescription = "Header Gradient",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().alpha(0.75f)
            )
            Image(
                painter = painterResource(R.drawable.ic_littlesteps_logo_notext),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (-80).dp, y = (-50).dp)
                    .blur(12.dp)
                    .alpha(0.6f)
            )
        }

        // 2. Konten Utama
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(mainScrollState)
                .statusBarsPadding()
        ) {
            // Icon Notifikasi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(Icons.Filled.Mail, "Mail", tint = Color.White)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Filled.Notifications, "Notif", tint = Color.White)
            }

            Spacer(Modifier.height(10.dp))


            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            ) {
                when (uiState) {
                    is HomeUiState.Loading -> {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Pink)
                            }
                        }
                    }
                    is HomeUiState.Error -> {
                        Text("Gagal memuat data", color = Color.White)
                    }
                    is HomeUiState.Success -> {
                        val state = uiState as HomeUiState.Success
                        val user = state.user

                        MainInfoCard(
                            name = user.name ?: "Pengguna",
                            userImage = user.imageUrl,
                            userId = user.usernumber.toString(),
                            points = user.points,
                            balance = formatRupiah(user.balance)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Kategori",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF222222),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(categoryScrollState)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CategoryItem(
                    icon = Icons.Outlined.ChildCare,
                    title = "Sitter",
                    onClick = { onNavigate("esitter_list") }
                )
                CategoryItem(
                    icon = Icons.Outlined.MedicalServices,
                    title = "Konsultasi",
                    onClick = { onNavigate("consultation_list")}
                )

                CategoryItem(
                    icon = Icons.Outlined.ShoppingCart,
                    title = "Belanja",
                    onClick = { onNavigate("shop") }
                )

                CategoryItem(
                    icon = Icons.Outlined.Store,
                    title = "Daycare",
                    onClick = { onNavigate("daycare_list") }
                )

                CategoryItem(
                    icon = Icons.Outlined.Money,
                    title = "Donasi",
                    onClick = { onNavigate("donation_list") }
                )

            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ScrollIndicator(
                    scrollState = categoryScrollState,
                    indicatorWidth = 50.dp,
                    thumbWidth = 25.dp
                )
            }

            Spacer(Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(R.drawable.banner_home),
                    contentDescription = "Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(0.9f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Rekomendasi",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF222222),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(Modifier.height(12.dp))

            if (uiState is HomeUiState.Success) {
                val products = (uiState as HomeUiState.Success).recommendedProducts

                if (products.isNotEmpty()) {
                    // Untuk produk, LazyRow tetap lebih baik karena datanya dinamis
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Belum ada rekomendasi saat ini.",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = Poppins
                    )
                }
            } else if (uiState is HomeUiState.Loading) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Pink)
                }
            }

            // Spacer Bottom agar tidak tertutup Bottom Bar
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
fun ScrollIndicator(
    scrollState: ScrollState,
    indicatorWidth: Dp = 50.dp,
    thumbWidth: Dp = 25.dp
) {
    val progress = if (scrollState.maxValue == 0) 0f else {
        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
    }

    val maxOffset = indicatorWidth - thumbWidth
    val currentOffset = maxOffset * progress

    Box(
        modifier = Modifier
            .width(indicatorWidth)
            .height(6.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFE0E0E0)) // Warna Track Abu
    ) {
        Box(
            modifier = Modifier
                .offset(x = currentOffset) // Gerakkan thumb
                .width(thumbWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
                .background(Pink) // Warna Thumb Pink
        )
    }
}

@Composable
fun MainInfoCard(name: String, userImage: String,userId: String, points: Int, balance: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = lightPurpleGradient
                )
            )
    ) {
        Canvas(modifier = Modifier.size(100.dp).align(Alignment.TopEnd)) {
            drawCircle(color = Color.White.copy(alpha = 0.1f), center = center, radius = size.minDimension)
        }

        Column(modifier = Modifier.padding(20.dp)) {
            // Header Profile
            Row(verticalAlignment = Alignment.CenterVertically) {
//                Image(
//                    painter = painterResource(R.drawable.ic_launcher_background),
//                    contentDescription = "Profile",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(CircleShape)
//                        .border(1.dp, Color.White, CircleShape)
//                )
                AsyncImage(
                    model = userImage,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White, CircleShape)
                    ,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background)
                )
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Hi, $name!", fontFamily = Poppins, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(userId, fontFamily = Poppins, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Poin", fontFamily = Poppins, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("$points", fontFamily = Poppins, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Logic Tier
            val (tierName, tierIcon, targetPoints) = when {
                points >= 40000 -> Triple("Gold", "\uD83E\uDD47", 40000)
                points >= 20000 -> Triple("Silver", "\uD83E\uDD48", 40000)
                else -> Triple("Bronze", "\uD83E\uDD49", 20000)
            }

            val progressValue = if (points >= 40000) 1f else (points.toFloat() / targetPoints.toFloat())
            val numberFormat = NumberFormat.getNumberInstance(Locale.Builder().setLanguage("id").setRegion("ID").build())

            // Tier Progress Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(text = tierIcon, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = tierName,
                            fontFamily = Poppins,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tierName == "Gold") Color(0xFFFFD700) else if (tierName == "Silver") Color(0xFFcccbd0) else Color(0xFFb66628)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (tierName == "Gold") Color(0xFFFFD700) else if (tierName == "Silver") Color(0xFFcccbd0) else Color(0xFFb66628),
                        trackColor = Color.White.copy(alpha = 0.3f),
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (points >= 40000) {
                            "Max Level ($points)"
                        } else {
                            "${numberFormat.format(points)}/${numberFormat.format(targetPoints)}"
                        }, fontFamily = Poppins, fontSize = 9.sp, color = Color.White, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(Modifier.height(20.dp))

            // Saldo & Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = primaryGradient
                        )
                    )
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Saldo", fontFamily = Poppins, fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        Text(balance, fontFamily = Poppins, fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    SmallActionButton(R.drawable.ic_saldo, "Isi Saldo")
                    Spacer(Modifier.width(12.dp))
                    SmallActionButton(R.drawable.ic_tukar, "Tukar")
                }
            }
        }
    }
}

@Composable
fun SmallActionButton(iconRes: Int, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.White, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Pink,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(text, fontFamily = Poppins, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CategoryItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .size(72.dp)
                .clickable (
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClick() }
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Pink,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = title,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = Color(0xFF6A6A6B),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(140.dp)
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.mainImage,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    error = painterResource(R.drawable.ic_launcher_background)
                )

                if (product.isNew || product.discount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(Pink, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if(product.isNew) "Baru!" else "Sale",
                            fontSize = 8.sp,
                            color = Color.White,
                            fontFamily = Poppins
                        )
                    }
                }
            }

            Column(Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatRupiah(product.price),
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = Pink
                )
            }
        }
    }
}