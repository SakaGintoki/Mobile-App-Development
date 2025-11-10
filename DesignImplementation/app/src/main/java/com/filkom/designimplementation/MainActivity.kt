package com.filkom.designimplementation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavGraph
import androidx.navigation.compose.rememberNavController
import com.filkom.designimplementation.ui.navigation.NavGraph
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavGraph(navController)
        }
    }
}
