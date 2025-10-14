package com.filkom.composenavigationapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import androidx.navigation.compose.composable

@Composable
fun RootNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        // 1) Splash sebagai start
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = {
                    // Pindah ke main_graph dan hapus splash dari back stack
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // 2) Main graph (akan diisi pada langkah berikut)
        navigation(
            startDestination = Routes.HOME,
            route = Routes.MAIN_GRAPH
        ) {
            // Untuk sementara, definisikan minimal satu layar agar bisa dicoba
            composable(Routes.HOME) { PlaceholderHomeScreen() }

            // Nanti di Langkah 3+ kita tambah: DETAIL, PROFILE, SETTINGS, ADD
        }
    }
}
