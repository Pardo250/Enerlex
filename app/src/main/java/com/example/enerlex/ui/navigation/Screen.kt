package com.example.enerlex.ui.navigation

/**
 * Define todas las rutas de navegación de la aplicación.
 * El uso de sealed class evita errores por strings hardcodeados.
 */
sealed class Screen(val route: String) {
    object Login        : Screen("login")
    object Register     : Screen("register")
    object Dashboard    : Screen("dashboard")
    object Devices      : Screen("devices")
    object DeviceDetail : Screen("device_detail/{deviceId}") {
        fun createRoute(deviceId: String) = "device_detail/$deviceId"
    }
    object Alerts       : Screen("alerts")
    object Settings     : Screen("settings")
}

/** Pantallas que forman parte de la bottom nav bar */
val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.Devices,
    Screen.Alerts,
    Screen.Settings
)
