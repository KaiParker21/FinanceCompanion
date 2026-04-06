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
import com.skye.financecompanion.presentation.profile.ProfileViewModel
import com.skye.financecompanion.presentation.transactions.TransactionListViewModel

@Composable
fun RootNavGraph(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    transactionListViewModel: TransactionListViewModel,
    profileViewModel: ProfileViewModel,
    insightsViewModel: InsightsViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = RootScreen.Splash.route
    ) {
        composable(RootScreen.Splash.route) {
            SplashScreen(
                authState = authState,
                onNavigateToMain = {
                    navController.navigate(RootScreen.MainApp.route) {
                        popUpTo(RootScreen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(RootScreen.Login.route) {
                        popUpTo(RootScreen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(RootScreen.Login.route) {
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    navController.navigate(RootScreen.MainApp.route) {
                        popUpTo(RootScreen.Login.route) { inclusive = true }
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

        composable(RootScreen.Register.route) {
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    navController.navigate(RootScreen.MainApp.route) {
                        popUpTo(RootScreen.Login.route) { inclusive = true }
                    }
                }
            }

            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(RootScreen.MainApp.route) {

            LaunchedEffect(authState) {
                if (authState is AuthState.Idle) {
                    navController.navigate(RootScreen.Login.route) {
                        popUpTo(RootScreen.MainApp.route) { inclusive = true }
                    }
                }
            }

            MainScreen(
                homeViewModel = homeViewModel,
                transactionListViewModel = transactionListViewModel,
                insightsViewModel = insightsViewModel,
                profileViewModel = profileViewModel,
                onLogoutClick = { authViewModel.logout() }
            )
        }
    }
}