package com.filkom.designimplementation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.ui.theme.Red
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(
    title: String,
    description: String,
    buttonText: String = "Lanjut",
    onButtonClick: () -> Unit
) {
    BaseAnimatedScreen(
        iconRes = R.drawable.ceklis,
        title = title,
        description = description,
        buttonText = buttonText,
        primaryColor = Pink,
        onButtonClick = onButtonClick
    )
}

@Composable
fun FailedScreen(
    title: String,
    description: String,
    buttonText: String = "Coba Lagi",
    onButtonClick: () -> Unit
) {
    BaseAnimatedScreen(
        iconRes = R.drawable.ic_cross_failed,
        title = title,
        description = description,
        buttonText = buttonText,
        primaryColor = Red,
        onButtonClick = onButtonClick
    )
}

// ==========================================================
// 3. BASE ENGINE (Private, tidak perlu dipanggil langsung)
//    Ini yang menangani animasi & layout agar tidak duplikat
// ==========================================================
@Composable
private fun BaseAnimatedScreen(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    buttonText: String,
    primaryColor: Color,
    onButtonClick: () -> Unit
) {
    var playAnim by remember { mutableStateOf(false) }
    var showTexts by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (playAnim) 1f else 0.2f,
        animationSpec = tween(650, easing = EaseOutBack), label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (playAnim) 1f else 0f,
        animationSpec = tween(500, easing = EaseOutCubic), label = "alpha"
    )

    LaunchedEffect(Unit) {
        playAnim = true
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
            // Icon
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha)
            )

            Spacer(Modifier.height(12.dp))

            // Teks
            AnimatedVisibility(
                visible = showTexts,
                enter = fadeIn(tween(300)) + slideInVertically { it / 6 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = if (primaryColor == Pink) Color(0xFF222222) else primaryColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = description,
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

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(tween(300)) + slideInVertically { it / 8 }
            ) {
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text(
                        text = buttonText,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}