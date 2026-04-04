package com.skye.financecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skye.financecompanion.presentation.AppViewModelProvider
import com.skye.financecompanion.presentation.home.HomeScreen
import com.skye.financecompanion.presentation.home.HomeViewModel
import com.skye.financecompanion.ui.theme.FinanceCompanionTheme

class MainActivity :
    ComponentActivity() {
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceCompanionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Ask Compose to create our ViewModel using our custom Factory!
                    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)

                    HomeScreen(
                        viewModel = homeViewModel,
                        onAddTransactionClick = {
                            // TODO: We will open the Add Transaction Sheet here next!
                            println("FAB Clicked! Time to add a transaction.")
                        }
                    )
                }
            }
        }
    }
}