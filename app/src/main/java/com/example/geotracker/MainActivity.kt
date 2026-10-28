package com.example.geotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geotracker.data.repository.AuthRepository
import com.example.geotracker.ui.navigation.Screen
import com.example.geotracker.ui.screens.capture.CaptureLocationScreen
import com.example.geotracker.ui.screens.capture.CaptureLocationViewModel
import com.example.geotracker.ui.screens.dashboard.DashboardScreen
import com.example.geotracker.ui.screens.list.LocationListScreen
import com.example.geotracker.ui.screens.list.LocationListViewModel
import com.example.geotracker.ui.screens.login.LoginScreen
import com.example.geotracker.ui.screens.login.LoginViewModel
import com.example.geotracker.ui.screens.map.MapScreen
import com.example.geotracker.ui.screens.map.MapViewModel
import com.example.geotracker.ui.theme.GeoTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = AuthRepository(applicationContext)
        val startDestination = if (authRepository.isLoggedIn()) {
            Screen.Dashboard.route
        } else {
            Screen.Login.route
        }

        setContent {
            GeoTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeoTrackerApp(
                        startDestination = startDestination,
                        onLogoutAction = { authRepository.logout() }
                    )
                }
            }
        }
    }
}

@Composable
fun GeoTrackerApp(
    startDestination: String,
    onLogoutAction: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToCapture = { navController.navigate(Screen.CaptureLocation.route) },
                onNavigateToList = { navController.navigate(Screen.LocationList.route) },
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onLogout = {
                    onLogoutAction()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CaptureLocation.route) {
            val captureViewModel: CaptureLocationViewModel = viewModel()
            CaptureLocationScreen(
                viewModel = captureViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LocationList.route) {
            val listViewModel: LocationListViewModel = viewModel()
            LocationListScreen(
                viewModel = listViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Map.route) {
            val mapViewModel: MapViewModel = viewModel()
            MapScreen(
                viewModel = mapViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
