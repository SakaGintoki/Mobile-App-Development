package com.filkom.composenavigationapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.filkom.composenavigationapp.ui.screens.AddScreen
import com.filkom.composenavigationapp.ui.screens.DetailScreen
import kotlinx.coroutines.launch
import com.filkom.composenavigationapp.ui.screens.HomeScreen
import com.filkom.composenavigationapp.ui.screens.ProfileScreen
import com.filkom.composenavigationapp.ui.screens.SettingsScreen

// --- BottomNav items ---

sealed class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {

    data object Home : BottomItem(Routes.HOME, "Home", Icons.Filled.Home)

    data object Profile : BottomItem(Routes.PROFILE, "Profile", Icons.Filled.Person)

    data object Settings : BottomItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings)

}

private val bottomItems = listOf(BottomItem.Home, BottomItem.Profile, BottomItem.Settings)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold() {

    val navController = rememberNavController()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(

        drawerState = drawerState,

        drawerContent = {


            AppDrawer(

                onNavigate = { route ->

                    // Navigasi via Drawer

                    navController.navigate(route) {

                        // popUpTo start agar tidak numpuk
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }

                        launchSingleTop = true

                        restoreState = true

                    }

                    scope.launch { drawerState.close() }

                }

            )

        }

    ) {


        Scaffold(

            topBar = {

                TopAppBar(

                    title = {

                        val current = currentRoute(navController)

                        Text(current ?: "Compose App")

                    },

                    navigationIcon = {

                        IconButton(onClick = { scope.launch {

                            drawerState.open() } }) {

                            Icon(Icons.Filled.Home, contentDescription = "Menu") // ikon bebas, hanya pemicu drawer

                        }

                    }

                )

            },

            bottomBar = { BottomNavBar(navController) },

            floatingActionButton = {

                FloatingActionButton(onClick = {

                    // FAB menuju layar ADD

                    navController.navigate(Routes.ADD) {

                        launchSingleTop = true

                    }

                }) {

                    Icon(Icons.Filled.Add, contentDescription = "Add")

                }

            }

        ) { padding ->


            Box(Modifier.padding(padding)) {

                MainNavHost(navController)

            }

        }

    }

}

@Composable
private fun BottomNavBar(navController: NavHostController) {

    val backStack by navController.currentBackStackEntryAsState()

    val dest = backStack?.destination


    NavigationBar {

        bottomItems.forEach { item ->

            val selected = isTopLevelDestination(dest, item.route)

            NavigationBarItem(

                selected = selected,

                onClick = {

                    navController.navigate(item.route) {

                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }

                        launchSingleTop = true

                        restoreState = true

                    }

                },

                icon = { Icon(item.icon, contentDescription = item.label) },

                label = { Text(item.label) }

            )

        }

    }

}

private fun isTopLevelDestination(dest: NavDestination?, route: String): Boolean =
    dest?.hierarchy?.any { it.route == route } == true

private fun currentRoute(navController: NavHostController): String? =
    navController.currentBackStackEntry?.destination?.route

@Composable
private fun AppDrawer(onNavigate: (String) -> Unit) {

    ModalDrawerSheet {

        Text("Menu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

        NavigationDrawerItem(

            label = { Text("Home") },

            selected = false,

            onClick = { onNavigate(Routes.HOME) }

        )

        NavigationDrawerItem(

            label = { Text("Profile") },

            selected = false,

            onClick = { onNavigate(Routes.PROFILE) }

        )

        NavigationDrawerItem(

            label = { Text("Settings") },

            selected = false,

            onClick = { onNavigate(Routes.SETTINGS) }

        )

    }

}

@Composable
private fun MainNavHost(navController: NavHostController) {

    NavHost(navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) { HomeScreen(navController) }

        composable("${Routes.DETAIL}/{id}") { backStack ->

            val id = backStack.arguments?.getString("id")

            DetailScreen(navController, id)

        }

        composable(Routes.PROFILE) { ProfileScreen() }

        composable(Routes.SETTINGS) { SettingsScreen() }

        composable(Routes.ADD) { AddScreen(navController) }

    }

}
