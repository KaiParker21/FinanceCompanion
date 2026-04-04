package com.skye.financecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skye.financecompanion.presentation.AppViewModelProvider
import com.skye.financecompanion.presentation.MainScreen
import com.skye.financecompanion.presentation.auth.AuthState
import com.skye.financecompanion.presentation.auth.AuthViewModel
import com.skye.financecompanion.presentation.auth.LoginScreen
import com.skye.financecompanion.presentation.auth.RegisterScreen
import com.skye.financecompanion.presentation.home.HomeViewModel
import com.skye.financecompanion.presentation.insights.InsightsViewModel
import com.skye.financecompanion.presentation.navigation.RootNavGraph
import com.skye.financecompanion.presentation.profile.ProfileViewModel
import com.skye.financecompanion.presentation.transactions.TransactionListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
                    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
                    val historyViewModel: TransactionListViewModel = viewModel(factory = AppViewModelProvider.Factory)
                    val insightsViewModel: InsightsViewModel = viewModel(factory = AppViewModelProvider.Factory)
                    val profileViewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)


                    // 2. Hand control over to the Root Navigation Graph
                    RootNavGraph(
                        authViewModel = authViewModel,
                        homeViewModel = homeViewModel,
                        transactionListViewModel = historyViewModel,
                        insightsViewModel = insightsViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}