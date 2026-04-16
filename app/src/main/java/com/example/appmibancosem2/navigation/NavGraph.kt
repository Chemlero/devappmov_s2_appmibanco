package com.example.appmibancosem2.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appmibancosem2.ui.screens.*

@Composable
fun MiBancoNavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {
        // M1 - Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // M2 - Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateTo = { screen ->
                    navController.navigate(screen.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // M3 - Transacciones
        composable(Screen.Transacciones.route) {
            TransaccionesScreen(onBack = { navController.popBackStack() })
        }

        // M4 - Pagos
        composable(Screen.Pagos.route) {
            PagosScreen(onBack = { navController.popBackStack() })
        }

        // M5 - Préstamos
        composable(Screen.Prestamos.route) {
            PrestamosScreen(onBack = { navController.popBackStack() })
        }

        // M6 - Ahorro
        composable(Screen.Ahorro.route) {
            AhorroScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SolicitudCredito.route) {
            SolicitudCreditoScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}