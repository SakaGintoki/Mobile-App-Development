package com.filkom.designimplementation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen
import com.filkom.designimplementation.ui.splash.SplashAnimation
import com.filkom.designimplementation.ui.start.StartScreen
import com.filkom.designimplementation.ui.auth.LoginScreen
import com.filkom.designimplementation.ui.auth.SignUpScreen
import com.filkom.designimplementation.ui.auth.ForgotPasswordScreen
import com.filkom.designimplementation.ui.auth.AccountCreatedScreen


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

        // START (landing)
        composable("start") {
            StartScreen(
                onLoginClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("signup") },
                onFacebookClick = { /* opsional: jalankan FB Login dari start */ },
                onGoogleClick = { /* opsional: jalankan Google Sign-In dari start */ }
            )
        }

        // LOGIN
        composable("login") {
            LoginScreen(
                onForgotPassword = { navController.navigate("forgot") },
                onLogin = { email, pass ->
                    navController.navigate("start") {
                        launchSingleTop = true
                    }
                },
                onFacebook = { /* TODO: jalankan Facebook Login */ },
                onGoogle   = { /* TODO: jalankan Google Sign-In */ },
                onToSignUp = { navController.navigate("signup") }
            )
        }

        // SIGN UP
        composable("signup") {
            SignUpScreen(
                onSignUp = { name, email, pass ->
                    navController.navigate("signup_success") {
                        launchSingleTop = true
                    }
                },
                onFacebook = { /* TODO */ },
                onGoogle   = { /* TODO */ },
                onToLogin  = { navController.popBackStack() } // balik ke screen sebelumnya
            )
        }

        // FORGET
        composable("forgot") {
            ForgotPasswordScreen(
                onSubmit = { email ->
                    // TODO: call reset-password API / Firebase sendPasswordResetEmail(email)
                    navController.popBackStack() // balik setelah kirim
                },
                onBack = { navController.popBackStack() }
            )
        }

        // SIGN UP SUCCESS
        composable("signup_success") {
            AccountCreatedScreen(
                onClose = {
                    // balikin user ke login (atau start/home sesuai kebutuhan)
                    navController.navigate("login") {
                        popUpTo("start") { inclusive = false } // sesuaikan jika punya home
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
