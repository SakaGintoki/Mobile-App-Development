package com.filkom.designimplementation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.filkom.designimplementation.model.core.ai.RealAiService
import com.filkom.designimplementation.model.feature.chat.ChatViewModel
import com.filkom.designimplementation.model.feature.chat.ChatViewModelFactory
import com.filkom.designimplementation.ui.auth.AccountCreatedScreen
import com.filkom.designimplementation.ui.auth.ForgotPasswordScreen
import com.filkom.designimplementation.ui.auth.LoginScreen
import com.filkom.designimplementation.ui.auth.SignUpScreen
import com.filkom.designimplementation.ui.home.HomeScreen
import com.filkom.designimplementation.ui.littleai.ChatScreen
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen
import com.filkom.designimplementation.ui.splash.SplashScreen
import com.filkom.designimplementation.ui.start.StartScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen {
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

        // START
        composable("start") {
            StartScreen(
                onLoginClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("signup") },
                onFacebookClick = { /* optional */ },
                onGoogleClick = { /* optional */ }
            )
        }

        // LOGIN
        composable("login") {
            LoginScreen(
                onForgotPassword = { navController.navigate("forgot") },
                onLogin = { _, _ ->
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onFacebook = { /* TODO */ },
                onGoogle   = { /* TODO */ },
                onToSignUp = { navController.navigate("signup") }
            )
        }

        // SIGN UP
        composable("signup") {
            SignUpScreen(
                onSignUp = { _, _, _ ->
                    navController.navigate("signup_success") {
                        launchSingleTop = true
                    }
                },
                onFacebook = { /* TODO */ },
                onGoogle   = { /* TODO */ },
                onToLogin  = { navController.popBackStack() }
            )
        }

        // FORGOT
        composable("forgot") {
            ForgotPasswordScreen(
                onSubmit = { _ ->
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        // SIGN UP SUCCESS
        composable("signup_success") {
            AccountCreatedScreen(
                onClose = {
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // HOME
        composable("home") {
            HomeScreen(
                onOpenLittleAI = { navController.navigate("little_ai") },
                onNavigate = { _ -> /* hook other tabs if needed */ }
            )
        }

        // LITTLE-AI (✅ with factory + welcome seed)
        composable("little_ai") {
            val ai = remember { RealAiService() }
            val vm: ChatViewModel = viewModel(factory = ChatViewModelFactory(ai))

            LaunchedEffect(Unit) { vm.seedWelcome() }

            ChatScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
