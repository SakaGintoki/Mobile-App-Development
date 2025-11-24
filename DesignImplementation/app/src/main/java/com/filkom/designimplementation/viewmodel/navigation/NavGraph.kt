package com.filkom.designimplementation.viewmodel.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.core.ai.RealAiService
import com.filkom.designimplementation.model.data.product.Product
import com.filkom.designimplementation.ui.auth.*
import com.filkom.designimplementation.ui.components.FailedScreen
import com.filkom.designimplementation.ui.components.SuccessScreen
import com.filkom.designimplementation.ui.feature.cart.CartScreen
import com.filkom.designimplementation.ui.feature.checkout.CheckoutScreen
import com.filkom.designimplementation.ui.feature.checkout.PaymentMethodScreen
import com.filkom.designimplementation.ui.feature.consultation.ConsultationDetailScreen
import com.filkom.designimplementation.ui.feature.consultation.ConsultationListScreen
import com.filkom.designimplementation.ui.feature.daycare.DaycareDetailScreen
import com.filkom.designimplementation.ui.feature.daycare.DaycareListScreen
import com.filkom.designimplementation.ui.feature.donation.DonationDetailScreen
import com.filkom.designimplementation.ui.feature.donation.DonationListScreen
import com.filkom.designimplementation.ui.feature.esitter.ESitterDetailScreen
import com.filkom.designimplementation.ui.feature.esitter.ESitterListScreen
import com.filkom.designimplementation.ui.feature.history.HistoryScreen
import com.filkom.designimplementation.ui.feature.home.HomeScreen
import com.filkom.designimplementation.ui.feature.littleai.ChatScreen
import com.filkom.designimplementation.ui.feature.profile.EditProfileScreen
import com.filkom.designimplementation.ui.feature.profile.ProfileScreen
import com.filkom.designimplementation.ui.feature.shop.AddProductScreen
import com.filkom.designimplementation.ui.feature.shop.ProductDetailScreen
import com.filkom.designimplementation.ui.feature.shop.ShopScreen
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen
import com.filkom.designimplementation.ui.splash.SplashScreen
import com.filkom.designimplementation.ui.start.StartScreen
import com.filkom.designimplementation.viewmodel.auth.LoginState
import com.filkom.designimplementation.viewmodel.auth.LoginViewModel
import com.filkom.designimplementation.viewmodel.feature.chat.ChatViewModel
import com.filkom.designimplementation.viewmodel.feature.chat.ChatViewModelFactory
import com.filkom.designimplementation.viewmodel.feature.checkout.CheckoutViewModel
import com.filkom.designimplementation.viewmodel.feature.consultation.ConsultationViewModel
import com.filkom.designimplementation.viewmodel.feature.daycare.DaycareViewModel
import com.filkom.designimplementation.viewmodel.feature.esitter.ESitterViewModel
import com.filkom.designimplementation.viewmodel.feature.profile.ProfileViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val animDuration = 400

    // --- SHARED STATE & VIEWMODELS ---
    // 1. Variabel sementara untuk menyimpan produk yang "Beli Langsung"
    var tempDirectBuyProduct by remember { mutableStateOf<Product?>(null) }

    // 2. Checkout ViewModel di-Hoisting (diangkat) ke sini
    // Agar satu instance dipakai bersama oleh: Shop, Sitter, Checkout, dan Payment
    val checkoutViewModel: CheckoutViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animDuration))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(animDuration))
        },
        popEnterTransition = {
            if (initialState.destination.route == "little_ai") {
                androidx.compose.animation.EnterTransition.None
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animDuration))
            }
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(animDuration))
        }
    ) {

        // ==================== AUTH & ONBOARDING ====================
        composable(route = "splash", exitTransition = { fadeOut(tween(500)) }) {
            SplashScreen {
                if (isUserLoggedIn) {
                    navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                } else {
                    navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                }
            }
        }

        composable(route = "onboarding", exitTransition = { fadeOut(tween(animDuration)) }) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("start") { popUpTo("onboarding") { inclusive = true } }
                }
            )
        }

        composable(route = "start", enterTransition = { fadeIn(tween(animDuration)) }) {
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel()
            val loginState by loginViewModel.loginState.collectAsState()

            LaunchedEffect(loginState) {
                if (loginState is LoginState.Success) {
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            StartScreen(
                onLoginClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("signup") },
                onGoogleClick = {
                    val webClientId = context.getString(R.string.web_client_id)
                    loginViewModel.signInWithGoogle(context, webClientId)
                },
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable("login") {
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel()
            val loginState by loginViewModel.loginState.collectAsState()

            LaunchedEffect(loginState) {
                if (loginState is LoginState.Success) {
                    navController.navigate("login_success") {
                        popUpTo("start") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onForgotPassword = { navController.navigate("forgot") },
                onLogin = { email, password ->
                    loginViewModel.signInWithEmailPassword(email, password)
                },
                onGoogle = {
                    val webClientId = context.getString(R.string.web_client_id)
                    loginViewModel.signInWithGoogle(context, webClientId)
                },
                onSuccess = {}, // Handled by LaunchedEffect
                onFailed = { message -> println("Login gagal: $message") },
                onToSignUp = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            val context = LocalContext.current
            SignUpScreen(
                onSuccess = { _, _, _ ->
                    navController.navigate("signup_success") {
                        popUpTo("signup") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onFailed = { errorMessage ->
                    Toast.makeText(context, "Gagal: $errorMessage", Toast.LENGTH_LONG).show()
                },
                onFacebook = { /* TODO */ },
                onGoogle = { /* TODO */ },
                onToLogin = { navController.popBackStack() }
            )
        }

        composable("signup_success") {
            SuccessScreen(
                title = "Akun Berhasil Dibuat!",
                description = "Terima kasih telah bergabung. Kami senang bisa menemani perjalananmu.",
                buttonText = "Mulai Sekarang",
                onButtonClick = {
                    navController.navigate("home") {
                        popUpTo("start") { inclusive = true }
                    }
                }
            )
        }

        composable("signup_failed") {
            FailedScreen(
                title = "Gagal Membuat Akun",
                description = "Terjadi kesalahan koneksi atau data tidak valid. Silakan coba lagi.",
                buttonText = "Coba Lagi",
                onButtonClick = { navController.popBackStack() }
            )
        }

        composable("login_success") {
            SuccessScreen(
                title = "Login Berhasil",
                description = "Selamat datang kembali! Yuk lanjut eksplorasi.",
                onButtonClick = { navController.navigate("home") }
            )
        }

        composable("payment_success") {
            SuccessScreen(
                title = "Pembayaran Berhasil",
                description = "Transaksi kamu sudah diproses. Terima kasih telah menggunakan layanan kami.",
                onButtonClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }

        composable("forgot") {
            ForgotPasswordScreen(
                onSubmit = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        // ==================== MAIN FEATURES ====================

        composable("home") {
            HomeScreen(
                onOpenLittleAI = { navController.navigate("little_ai") },
                onNavigate = { destination ->
                    if (destination == "shop") {
                        navController.navigate("shop")
                    } else if (destination != "home") {
                        navController.navigate(destination) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                }
            )
        }

        composable("shop") {
            ShopScreen(
                onNavigateToCart = { navController.navigate("cart") },
                onNavigateToDetail = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onBack = { navController.popBackStack() },
                // Jika Anda membuat fitur tambah produk:
                onNavigateToAdd = { navController.navigate("add_product") }
            )
        }

        // Fitur Tambah Produk (Hanya Admin)
        composable("add_product") {
            AddProductScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val detailViewModel: com.filkom.designimplementation.viewmodel.feature.shop.ProductDetailViewModel = viewModel()
            val context = LocalContext.current

            LaunchedEffect(productId) {
                if (productId != null) {
                    detailViewModel.loadProductById(productId)
                }
            }

            ProductDetailScreen(
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() },
                onAddToCart = {
                    val product = detailViewModel.productState.value
                    if (product != null) {
                        detailViewModel.addToCart(product)
                        Toast.makeText(context, "Masuk Keranjang", Toast.LENGTH_SHORT).show()
                    }
                },
                onBuyNow = {
                    val product = detailViewModel.productState.value
                    if (product != null) {
                        // Simpan ke temp variabel untuk diambil CheckoutScreen
                        tempDirectBuyProduct = product
                        navController.navigate("checkout?mode=direct")
                    }
                }
            )
        }

        composable("profile") {
            val profileViewModel: ProfileViewModel = viewModel()

            // Refresh data profil saat masuk kembali
            LaunchedEffect(Unit) {
                profileViewModel.fetchUserProfile()
            }

            ProfileScreen(
                viewModel = profileViewModel,
                onOpenLittleAI = { navController.navigate("little_ai") },
                onNavigate = { dest ->
                    if (dest != "profile") {
                        navController.navigate(dest) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEditProfile = { navController.navigate("edit_profile") },
                onSettings = { /* TODO */ }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("history") {
            // Tidak perlu onNavigate karena BottomBar dihandle MainActivity
            HistoryScreen()
        }

        composable("cart") {
            CartScreen(
                onBack = { navController.popBackStack() },
                onCheckout = {
                    navController.navigate("checkout?mode=cart")
                }
            )
        }

        // ==================== E-SITTER FLOW ====================

        composable("esitter_list") {
            val esitterViewModel: ESitterViewModel = viewModel()
            ESitterListScreen(
                viewModel = esitterViewModel,
                onBack = { navController.popBackStack() },
                onSitterClick = { sitter ->
                    esitterViewModel.selectSitter(sitter)
                    navController.navigate("esitter_detail")
                }
            )
        }

        composable("esitter_detail") {
            val parentEntry = remember(it) { navController.getBackStackEntry("esitter_list") }
            val esitterViewModel:ESitterViewModel = viewModel(parentEntry)

            ESitterDetailScreen(
                viewModel = esitterViewModel,
                onBack = { navController.popBackStack() },
                onBookNow = { sitter, date, time ->
                    checkoutViewModel.prepareSitterCheckout(sitter, date, time)
                    navController.navigate("checkout?mode=sitter")
                }
            )
        }

        composable("donation_list") {
            DonationListScreen(
                onBack = { navController.popBackStack() },
                onDonationClick = { donationId ->
                    navController.navigate("donation_detail/$donationId")
                }
            )
        }

        composable(
            route = "donation_detail/{donationId}",
            arguments = listOf(navArgument("donationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val donationId = backStackEntry.arguments?.getString("donationId") ?: ""

            DonationDetailScreen(
                donationId = donationId,
                checkoutViewModel = checkoutViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToPayment = {
                    navController.navigate("payment_method")
                }
            )
        }

        composable("consultation_list") {
            val consultationViewModel: ConsultationViewModel = viewModel()
            ConsultationListScreen(
                viewModel = consultationViewModel, // Inject ViewModel yang sama
                onBack = { navController.popBackStack() },
                onDoctorClick = { doctorId ->
                    navController.navigate("consultation_detail/$doctorId")
                }
            )
        }

        composable(
            route = "consultation_detail/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val consultationViewModel: ConsultationViewModel = viewModel()
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            ConsultationDetailScreen(
                doctorId = doctorId,
                viewModel = consultationViewModel,
                onBack = { navController.popBackStack() },
                onBookNow = { doctor, date, time ->
                    checkoutViewModel.prepareConsultationCheckout(doctor, date, time)
                    navController.navigate("checkout?mode=consultation")
                }
            )
        }

        // --- DAYCARE ROUTES ---

        composable("daycare_list") {
            val daycareViewModel: DaycareViewModel = viewModel()
            DaycareListScreen(
                viewModel = daycareViewModel,
                onBack = { navController.popBackStack() },
                onItemClick = { id -> navController.navigate("daycare_detail/$id") }
            )
        }

        composable(
            route = "daycare_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val daycareViewModel: DaycareViewModel = viewModel()
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DaycareDetailScreen(
                daycareId = id,
                viewModel = daycareViewModel,
                onBack = { navController.popBackStack() },
                onBookNow = { daycare, date ->
                    checkoutViewModel.prepareDaycareCheckout(daycare, date)
                    navController.navigate("payment_method")
                }
            )
        }
        // ==================== CHECKOUT & PAYMENT (SHARED VM) ====================

        composable(
            route = "checkout?mode={mode}",
            arguments = listOf(navArgument("mode") { defaultValue = "cart" })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "cart"

            LaunchedEffect(mode) {
                if (mode == "direct" && tempDirectBuyProduct != null) {
                    checkoutViewModel.prepareDirectCheckout(tempDirectBuyProduct!!)
                } else if (mode == "cart") {
                    checkoutViewModel.prepareCartCheckout()
                }
            }
            CheckoutScreen(
                viewModel = checkoutViewModel,
                onBack = { navController.popBackStack() },
                onToPaymentMethod = { navController.navigate("payment_method") }
            )
        }

        composable("payment_method") {
            PaymentMethodScreen(
                viewModel = checkoutViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("payment_success") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }

        // ==================== LITTLE AI ====================

        composable(
            route = "little_ai",
        ) {
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