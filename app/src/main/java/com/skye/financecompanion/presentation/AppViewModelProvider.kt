package com.skye.financecompanion.presentation

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skye.financecompanion.FinanceApplication
import com.skye.financecompanion.presentation.home.HomeViewModel

/**
 * Provides Factory to create instances of ViewModels for the entire app.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Initializer for HomeViewModel
        initializer {
            val application = (this[APPLICATION_KEY] as FinanceApplication)
            val container = application.container

            HomeViewModel(
                transactionRepository = container.transactionRepository,
                calculateStreakUseCase = container.calculateStreakUseCase
            )
        }

        // As we build more screens (Transactions, Insights), we will add their ViewModels here!
    }
}