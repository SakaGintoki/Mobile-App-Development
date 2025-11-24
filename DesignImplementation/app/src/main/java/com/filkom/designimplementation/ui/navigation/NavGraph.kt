package com.filkom.designimplementation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.filkom.designimplementation.core.ai.RealAiService
import com.filkom.designimplementation.feature.chat.ChatViewModel
import com.filkom.designimplementation.feature.chat.ChatViewModelFactory
import com.filkom.designimplementation.ui.auth.AccountCreatedScreen
import com.filkom.designimplementation.ui.auth.ForgotPasswordScreen
import com.filkom.designimplementation.ui.auth.LoginScreen
import com.filkom.designimplementation.ui.auth.SignUpScreen
import com.filkom.designimplementation.ui.home.HomeScreen
import com.filkom.designimplementation.ui.littleai.ChatScreen
import com.filkom.designimplementation.ui.marketplace.MarketplaceScreen
import com.filkom.designimplementation.ui.marketplace.ProductDetailScreen
import com.filkom.designimplementation.ui.marketplace.OrderConfirmationScreen // <-- BARU
import com.filkom.designimplementation.ui.marketplace.PaymentMethodScreen // <-- BARU
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen
import com.filkom.designimplementation.ui.splash.SplashAnimation
import com.filkom.designimplementation.ui.start.StartScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ... (Rute splash, onboarding, start, login, signup, forgot, signup_success, home, marketplace, little_ai TIDAK BERUBAH)

        // HOME
        composable("home") {
            HomeScreen(
                onOpenLittleAI = { navController.navigate("little_ai") },
                onOpenMarketplace = { navController.navigate("marketplace") },
                onNavigate = { _ -> /* hook other tabs if needed */ }
            )
        }

        // MARKETPLACE
        composable("marketplace") {
            MarketplaceScreen(
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate("detail/$productId")
                }
            )
        }

        // PRODUCT DETAIL
        composable(
            route = "detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                // NAVIGASI BARU: Ke konfirmasi pesanan
                onCheckout = { id ->
                    navController.navigate("confirm_order/$id")
                }
            )
        }

        // KONFIRMASI PESANAN
        composable(
            route = "confirm_order/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            OrderConfirmationScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                // NAVIGASI BARU: Ke Metode Pembayaran
                onProceedToPayment = { id, qty ->
                    navController.navigate("payment_method/$id/$qty")
                }
            )
        }

        // METODE PEMBAYARAN
        composable(
            route = "payment_method/{productId}/{quantity}",
            arguments = listOf(
                navArgument("productId") { type = NavType.IntType },
                navArgument("quantity") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val quantity = backStackEntry.arguments?.getInt("quantity") ?: 1
            PaymentMethodScreen(
                productId = productId,
                quantity = quantity,
                onBack = { navController.popBackStack() },
                // TODO: Implement onPaymentConfirmed logic (misalnya, ke halaman sukses/keranjang)
                onPaymentConfirmed = { method ->
                    // Saat pembayaran dikonfirmasi, kembali ke Home (contoh)
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
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