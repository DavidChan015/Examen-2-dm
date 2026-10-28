package com.example.geotracker.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object CaptureLocation : Screen("capture_location")
    object LocationList : Screen("location_list")
    object Map : Screen("map")
}
