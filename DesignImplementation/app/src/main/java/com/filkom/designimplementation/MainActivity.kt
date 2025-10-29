package com.filkom.designimplementation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.filkom.designimplementation.ui.navigation.NavGraph
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen
import com.filkom.designimplementation.ui.splash.SplashAnimation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavGraph(navController)
        }
    }
}
