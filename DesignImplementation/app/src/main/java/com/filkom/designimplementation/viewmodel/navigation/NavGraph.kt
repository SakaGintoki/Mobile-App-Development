package com.filkom.designimplementation.viewmodel.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.filkom.designimplementation.R
import com.filkom.designimplementation.data.repository.ChatRepository
import com.filkom.designimplementation.model.core.ai.RealAiService
import com.filkom.designimplementation.model.data.product.Product
import com.filkom.designimplementation.ui.auth.*
import com.filkom.designimplementation.ui.components.FailedScreen
import com.filkom.designimplementation.ui.components.SuccessScreen
import com.filkom.designimplementation.ui.feature.cart.CartScreen
// Pastikan path import ChatScreenDoctor sesuai dengan lokasi file Anda
import com.filkom.designimplementation.ui.feature.consultation.chat.ChatScreenDoctor
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
import com.filkom.designimplementation.viewmodel.feature.shop.ProductDetailViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val animDuration = 400

    // --- SHARED VIEW MODELS ---
    // ViewModel ini akan bertahan selama NavHost hidup (sepanjang aplikasi berjalan)
    // Ini memungkinkan data (seperti keranjang belanjaan checkout) tidak hilang saat navigasi.
    val checkoutViewModel: CheckoutViewModel = viewModel()
    val consultationViewModel: ConsultationViewModel = viewModel()
    val esitterViewModel: ESitterViewModel = viewModel()
    val daycareViewModel: DaycareViewModel = viewModel()

    // Temp data untuk Direct Buy (Beli Langsung Barang)
    var tempDirectBuyProduct by remember { mutableStateOf<Product?>(null) }

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
            // Khusus Little AI tidak pakai animasi slide agar smooth saat back
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
                onSuccess = {},
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
                onNavigateToAdd = { navController.navigate("add_product") }
            )
        }

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
            val detailViewModel: ProductDetailViewModel = viewModel()
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
                        tempDirectBuyProduct = product
                        navController.navigate("checkout?mode=direct")
                    }
                }
            )
        }

        composable("profile") {
            val profileViewModel: ProfileViewModel = viewModel()

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
            ESitterDetailScreen(
                viewModel = esitterViewModel,
                onBack = { navController.popBackStack() },
                onBookNow = { sitter, date, time ->
                    checkoutViewModel.prepareSitterCheckout(sitter, date, time)
                    navController.navigate("checkout?mode=sitter")
                }
            )
        }

        // ==================== DONASI ====================

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

        // ==================== KONSULTASI DOKTER ====================

        composable("consultation_list") {
            ConsultationListScreen(
                viewModel = consultationViewModel,
                onBack = { navController.popBackStack() },
                onDoctorClick = { doctorId ->
                    navController.navigate("consultation_detail/$doctorId")
                },
                onChatClick = { routeUrl ->
                    navController.navigate(routeUrl)
                }
            )
        }

        composable(
            route = "consultation_detail/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
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

        composable(
            route = "chat_room/{doctorId}/{doctorName}?doctorImage={doctorImage}",
            arguments = listOf(
                navArgument("doctorId") { type = NavType.StringType },
                navArgument("doctorName") { type = NavType.StringType },
                navArgument("doctorImage") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("doctorSpecialization") { type = NavType.StringType; defaultValue = "Dokter" }

            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("doctorId") ?: ""
            val name = backStackEntry.arguments?.getString("doctorName") ?: "Dokter"
            val image = backStackEntry.arguments?.getString("doctorImage") ?: ""
            val specialization = backStackEntry.arguments?.getString("doctorSpecialization") ?: ""
            ChatScreenDoctor(
                doctorId = id,
                doctorName = name,
                doctorImage = image,
                doctorSpecialization = specialization,
                viewModel = consultationViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ==================== DAYCARE ====================

        composable("daycare_list") {
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

        // ==================== CHECKOUT & PAYMENT ====================

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

        composable("little_ai") {
            // Dependency Injection di sini, aman karena di dalam Composable
            val aiService = remember { RealAiService() }
            val chatRepository = remember { ChatRepository(aiService) }
            val chatViewModelFactory = remember { ChatViewModelFactory(chatRepository) }

            val viewModel: ChatViewModel = viewModel(factory = chatViewModelFactory)

            ChatScreen(
                vm = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}