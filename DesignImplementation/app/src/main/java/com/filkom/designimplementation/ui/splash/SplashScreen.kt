package com.filkom.designimplementation.ui.splash

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.filkom.designimplementation.R
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue

@Composable
fun SplashScreen(navController: NavController) {
    var scale by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(0f) }
    var bgColor by remember { mutableStateOf(Color.White) }
    var showGlow by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(1000)
    )

    val animatedBg by animateColorAsState(
        targetValue = bgColor,
        animationSpec = tween(1000)
    )

    LaunchedEffect(Unit) {
        alpha = 1f
        delay(700)
        scale = 1.2f
        delay(400)
        showGlow = true
        delay(600)
        bgColor = Color(0xFFD77AD9)
        delay(1200)
        navController.navigate("onboarding") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBg),
        contentAlignment = Alignment.Center
    ) {
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFEA80FC), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_littlesteps_logo),
            contentDescription = "LittleSteps Logo",
            modifier = Modifier
                .size(120.dp)
                .scale(animatedScale)
                .alpha(animatedAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "LittleSteps",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF8E24AA)
            )
        }
    }
}
