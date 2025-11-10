package com.filkom.designimplementation.ui.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.filkom.designimplementation.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }
    var showGlow by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.6f,
        animationSpec = tween(800, easing = OvershootInterpolator(2f).toEasing())
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(1000)
    )

    val bgColor by animateColorAsState(
        targetValue = if (showGlow) Color(0xFFF987C5) else Color.White,
        animationSpec = tween(1200)
    )

    // Urutan animasi
    LaunchedEffect(Unit) {
        startAnim = true
        delay(800)
        showGlow = true
        delay(1600)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFE1BEE7), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_littlesteps_logo_white),
                contentDescription = "LittleSteps Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .alpha(alpha)
            )
            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "LittleSteps",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Medium,
//                color = Color(0xFF8E24AA)
//            )
        }
    }
}

fun OvershootInterpolator.toEasing(): Easing = Easing { x -> getInterpolation(x) }
