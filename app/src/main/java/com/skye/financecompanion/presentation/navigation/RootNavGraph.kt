package com.skye.financecompanion.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skye.financecompanion.presentation.MainScreen
import com.skye.financecompanion.presentation.auth.AuthState
import com.skye.financecompanion.presentation.auth.AuthViewModel
import com.skye.financecompanion.presentation.auth.LoginScreen
import com.skye.financecompanion.presentation.auth.RegisterScreen
import com.skye.financecompanion.presentation.auth.SplashScreen
import com.skye.financecompanion.presentation.home.HomeViewModel
import com.skye.financecompanion.presentation.insights.InsightsViewModel
import com.skye.financecompanion.presentation.transactions.TransactionListViewModel

@Composable
fun RootNavGraph(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    transactionListViewModel: TransactionListViewModel,
    insightsViewModel: InsightsViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = RootScreen.Splash.route
    ) {
        // 1. SPLASH SCREEN
        composable(RootScreen.Splash.route) {
            SplashScreen(
                authState = authState,
                onNavigateToMain = {
                    navController.navigate(RootScreen.MainApp.route) {
                        popUpTo(RootScreen.Splash.route) { inclusive = true } // Destroy splash so we can't 'back' into it
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(RootScreen.Login.route) {
                        popUpTo(RootScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. LOGIN SCREEN
        composable(RootScreen.Login.route) {
            // Listen for successful login
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    navController.navigate(RootScreen.MainApp.route) {
                        popUpTo(RootScreen.Login.route) { inclusive = true } // Destroy login screen
                    }
                }
            }

            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(RootScreen.Register.route)
                }
            )
        }

        // 3. REGISTER SCREEN
        composable(RootScreen.Register.route) {
            // Listen for successful registration
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    navController.navigate(RootScreen.MainApp.route) {
                        popUpTo(RootScreen.Login.route) { inclusive = true } // Clear the whole auth stack
                    }
                }
            }

            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack() // Smoothly slides back to the Login screen
                }
            )
        }

        // 4. MAIN APP SHELL (Bottom Navigation)
        composable(RootScreen.MainApp.route) {

            LaunchedEffect(authState) {
                if (authState is AuthState.Idle) {
                    navController.navigate(RootScreen.Login.route) {
                        popUpTo(RootScreen.MainApp.route) { inclusive = true } // Destroy the main app stack
                    }
                }
            }

            MainScreen(
                homeViewModel = homeViewModel,
                transactionListViewModel = transactionListViewModel,
                insightsViewModel = insightsViewModel,
                onLogoutClick = { authViewModel.logout() }
            )
        }
    }
}