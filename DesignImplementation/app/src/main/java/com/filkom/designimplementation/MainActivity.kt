package com.filkom.designimplementation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.filkom.designimplementation.ui.components.MainBottomBar
import com.filkom.designimplementation.viewmodel.navigation.NavGraph
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val isUserLoggedIn = auth.currentUser != null

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomBarRoutes = listOf("home", "profile", "history")
            val showBottomBar = currentRoute in bottomBarRoutes

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                NavGraph(
                    navController = navController,
                    isUserLoggedIn = isUserLoggedIn,
                    modifier = Modifier.fillMaxSize()
                )

                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it },
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    MainBottomBar(
                        currentRoute = currentRoute ?: "home",
                        onNavigate = { destination ->
                            if (currentRoute != destination) {
                                navController.navigate(destination) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onOpenLittleAI = { navController.navigate("little_ai") }
                    )
                }
            }
        }
    }
}
