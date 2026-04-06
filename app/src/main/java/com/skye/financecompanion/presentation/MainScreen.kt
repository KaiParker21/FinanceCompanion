package com.skye.financecompanion.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skye.financecompanion.presentation.home.HomeScreen
import com.skye.financecompanion.presentation.home.HomeViewModel
import com.skye.financecompanion.presentation.insights.InsightsScreen
import com.skye.financecompanion.presentation.insights.InsightsViewModel
import com.skye.financecompanion.presentation.navigation.Screen
import com.skye.financecompanion.presentation.profile.ProfileScreen
import com.skye.financecompanion.presentation.profile.ProfileViewModel
import com.skye.financecompanion.presentation.transactions.AddTransactionDialog
import com.skye.financecompanion.presentation.transactions.TransactionListScreen
import com.skye.financecompanion.presentation.transactions.TransactionListViewModel

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    transactionListViewModel: TransactionListViewModel,
    insightsViewModel: InsightsViewModel,
    profileViewModel: ProfileViewModel,
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()
    var showAddDialog by remember { mutableStateOf(false) }

    val items = listOf(
        Screen.Home,
        Screen.Insights,
        Screen.Transactions,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val haptic = LocalHapticFeedback.current

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // This NavHost acts as the container that swaps out the screens
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onAddTransactionClick = { showAddDialog = true },
                    onLogoutClick = onLogoutClick
                )
            }
            composable(Screen.Transactions.route) {
                TransactionListScreen(viewModel = transactionListViewModel)
            }
            composable(Screen.Insights.route) {
                InsightsScreen(viewModel = insightsViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogoutClick = onLogoutClick
                )
            }
        }

        // Keep the dialog logic here so it can be opened from anywhere if needed later
        if (showAddDialog) {
            AddTransactionDialog(
                onDismiss = { showAddDialog = false },
                onSave = { amount, type, category, note, isEssential ->
                    homeViewModel.addTransaction(amount, type, category, note, isEssential)
                }
            )
        }
    }
}