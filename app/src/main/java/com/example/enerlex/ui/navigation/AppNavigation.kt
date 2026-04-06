package com.example.enerlex.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.enerlex.ui.alerts.AlertsScreen
import com.example.enerlex.ui.alerts.AlertsViewModel
import com.example.enerlex.ui.dashboard.DashboardScreen
import com.example.enerlex.ui.dashboard.DashboardViewModel
import com.example.enerlex.ui.devicedetail.DeviceDetailScreen
import com.example.enerlex.ui.devicedetail.DeviceDetailViewModel
import com.example.enerlex.ui.devices.DevicesScreen
import com.example.enerlex.ui.devices.DevicesViewModel
import com.example.enerlex.ui.login.LoginScreen
import com.example.enerlex.ui.login.LoginViewModel
import com.example.enerlex.ui.settings.SettingsScreen
import com.example.enerlex.ui.settings.SettingsViewModel
import com.example.enerlex.ui.theme.EnerSurface

private val screensWithBottomBar = setOf(
    Screen.Dashboard.route,
    Screen.Devices.route,
    Screen.Alerts.route,
    Screen.Settings.route
)

/**
 * Punto central de navegación. Gestiona el NavHost y la bottom bar.
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = EnerSurface,
        bottomBar = {
            if (currentRoute in screensWithBottomBar) {
                EnerBottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── LOGIN ─────────────────────────────────────────────────────
            composable(Screen.Login.route) {
                val vm: LoginViewModel = viewModel()
                LoginScreen(
                    viewModel = vm,
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── DASHBOARD ─────────────────────────────────────────────────
            composable(Screen.Dashboard.route) {
                val vm: DashboardViewModel = viewModel()
                DashboardScreen(
                    viewModel = vm,
                    onDeviceClick = { deviceId ->
                        navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
                    }
                )
            }

            // ── DISPOSITIVOS ──────────────────────────────────────────────
            composable(Screen.Devices.route) {
                val vm: DevicesViewModel = viewModel()
                DevicesScreen(
                    viewModel = vm,
                    onDeviceClick = { deviceId ->
                        navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
                    }
                )
            }

            // ── DETALLE DE DISPOSITIVO ────────────────────────────────────
            composable(
                route = Screen.DeviceDetail.route,
                arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
                val vm: DeviceDetailViewModel = viewModel(
                    factory = DeviceDetailViewModel.Factory(deviceId)
                )
                DeviceDetailScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            // ── ALERTAS ───────────────────────────────────────────────────
            composable(Screen.Alerts.route) {
                val vm: AlertsViewModel = viewModel()
                AlertsScreen(viewModel = vm)
            }

            // ── CONFIGURACIÓN ─────────────────────────────────────────────
            composable(Screen.Settings.route) {
                val vm: SettingsViewModel = viewModel()
                SettingsScreen(viewModel = vm)
            }
        }
    }
}
