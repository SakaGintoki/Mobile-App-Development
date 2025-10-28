package com.example.designimplementations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12+ shows the system splash instantly
        val splash = installSplashScreen()
        // Remove the default icon scale/fade animation
        splash.setOnExitAnimationListener { it.remove() }

        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface { AppRoot() }
            }
        }
    }
}

@Composable
private fun AppRoot() {
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(1200); showSplash = false } // demo delay

    if (showSplash) SplashScreenComposable() else MainScreen()
}

@Composable
fun SplashScreenComposable() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_littlesteps_logo),
                contentDescription = "LittleSteps logo",
                modifier = Modifier.size(144.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text("", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun MainScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Home")
    }
}
