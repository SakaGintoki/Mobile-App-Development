package com.filkom.designimplementation.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.ui.theme.Poppins
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState

@Composable
fun AccountCreatedScreen(
    onClose: () -> Unit = {},
) {
    var playCheck by remember { mutableStateOf(false) }
    var showTexts  by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (playCheck) 1f else 0.2f,
        animationSpec = tween(durationMillis = 650, easing = EaseOutBack),
        label = "check-scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (playCheck) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = EaseOutCubic),
        label = "check-alpha"
    )

    LaunchedEffect(Unit) {
        playCheck = true
        delay(650)
        showTexts = true
        delay(200)
        showButton = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Ceklis.png (tanpa konfeti)
            Image(
                painter = painterResource(R.drawable.ceklis),
                contentDescription = "Berhasil",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha)
            )

            Spacer(Modifier.height(12.dp))

            // Title + subtitle muncul setelah ceklis
            AnimatedVisibility(
                visible = showTexts,
                enter = fadeIn(tween(300)) + slideInVertically { it / 6 },
                exit  = fadeOut(tween(150)) + slideOutVertically { it / 6 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Akun Berhasil dibuat!",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color(0xFF222222),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Terima kasih telah bergabung. Kami senang bisa menemani perjalananmu.",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = Color(0xFF8F8F93),
                        lineHeight = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Tombol muncul paling akhir
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(300)) + slideInVertically { it / 8 },
                exit  = fadeOut(tween(150)) + slideOutVertically { it / 8 }
            ) {
                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5))
                ) {
                    Text(
                        "Lanjut",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
