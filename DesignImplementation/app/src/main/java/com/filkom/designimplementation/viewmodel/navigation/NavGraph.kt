package com.filkom.designimplementation.viewmodel.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.filkom.designimplementation.viewmodel.feature.chat.ChatViewModel
import com.filkom.designimplementation.viewmodel.feature.chat.ChatViewModelFactory
import com.filkom.designimplementation.ui.auth.*
import com.filkom.designimplementation.ui.components.FailedScreen
import com.filkom.designimplementation.ui.components.SuccessScreen
import com.filkom.designimplementation.ui.feature.home.HomeScreen
import com.filkom.designimplementation.ui.feature.littleai.ChatScreen
import com.filkom.designimplementation.ui.onboarding.OnboardingScreen
import com.filkom.designimplementation.ui.feature.profile.ProfileScreen
import com.filkom.designimplementation.ui.splash.SplashScreen
import com.filkom.designimplementation.ui.start.StartScreen
import com.filkom.designimplementation.viewmodel.auth.LoginState
import com.filkom.designimplementation.viewmodel.auth.LoginViewModel
import com.filkom.designimplementation.ui.feature.cart.CartScreen
import com.filkom.designimplementation.ui.feature.checkout.CheckoutScreen
import com.filkom.designimplementation.ui.feature.checkout.PaymentMethodScreen
import com.filkom.designimplementation.ui.feature.history.HistoryScreen
import com.filkom.designimplementation.ui.feature.profile.EditProfileScreen
import com.filkom.designimplementation.ui.feature.shop.ProductDetailScreen
import com.filkom.designimplementation.ui.feature.shop.ShopScreen
import com.filkom.designimplementation.viewmodel.feature.checkout.CheckoutViewModel
import com.filkom.designimplementation.viewmodel.feature.profile.ProfileViewModel


@Composable

fun NavGraph(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val animDuration = 400
    var tempDirectBuyProduct: Product? = remember { null }

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


        composable(
            route = "splash",
            exitTransition = { fadeOut(tween(500)) }
        ) {
            SplashScreen {
                if (isUserLoggedIn) {
                    navController.navigate("home") { popUpTo("splash") { inclusive = true } }
                } else {
                    navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
                }
            }
        }



        composable(
            route = "onboarding",
            exitTransition = { fadeOut(tween(animDuration)) }
        ) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("start") { popUpTo("onboarding") { inclusive = true } }
                }
            )
        }


        composable(
            route = "start",
            enterTransition = { fadeIn(tween(animDuration)) }
        ) {
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
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
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
                onSuccess = { userId, email, name ->
                    navController.navigate("signup_success") {
                        popUpTo("signup") { inclusive = true }
                        launchSingleTop = true
                    }
                },

                onFailed = { errorMessage ->
                    android.widget.Toast.makeText(
                        context,
                        "Gagal: $errorMessage",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                },

                onFacebook = { /* TODO */ },
                onGoogle = { /* TODO: Logic di dalam screen sudah handle ini */ },
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
                onButtonClick = {
                    navController.popBackStack()
                }
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
                onButtonClick = { navController.navigate("home") }
            )
        }
        composable("forgot") {
            ForgotPasswordScreen(
                onSubmit = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

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
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            val detailViewModel: com.filkom.designimplementation.viewmodel.feature.shop.ProductDetailViewModel =
                viewModel()

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
                    if (product != null) detailViewModel.addToCart(product)
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
                profileViewModel
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
                onEditProfile = {
                    navController.navigate("edit_profile")
                },
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

        composable(
            route = "checkout?mode={mode}",
            arguments = listOf(navArgument("mode") { defaultValue = "cart" })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "cart"
            val checkoutViewModel: CheckoutViewModel = viewModel()

            LaunchedEffect(mode) {
                if (mode == "direct" && tempDirectBuyProduct != null) {
                    checkoutViewModel.prepareDirectCheckout(tempDirectBuyProduct)
                } else {
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
            val checkoutViewModel: CheckoutViewModel = viewModel(
                viewModelStoreOwner = navController.previousBackStackEntry!!
            )

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