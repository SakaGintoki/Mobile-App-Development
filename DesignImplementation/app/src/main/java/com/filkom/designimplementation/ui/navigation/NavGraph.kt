package com.filkom.designimplementation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen
import com.filkom.designimplementation.ui.splash.SplashAnimation
import com.filkom.designimplementation.ui.start.StartScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashAnimation {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("start") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("start") {
            StartScreen()
        }
    }
}
