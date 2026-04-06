package com.skye.financecompanion.presentation

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skye.financecompanion.FinanceApplication
import com.skye.financecompanion.presentation.auth.AuthViewModel
import com.skye.financecompanion.presentation.home.HomeViewModel
import com.skye.financecompanion.presentation.insights.InsightsViewModel
import com.skye.financecompanion.presentation.profile.ProfileViewModel
import com.skye.financecompanion.presentation.transactions.TransactionListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            val application = (this[APPLICATION_KEY] as FinanceApplication)
            val container = application.container
            AuthViewModel(auth = container.auth)
        }

        initializer {
            val application = (this[APPLICATION_KEY] as FinanceApplication)
            val container = application.container

            HomeViewModel(
                transactionRepository = container.transactionRepository,
                calculateStreakUseCase = container.calculateStreakUseCase
            )
        }

        initializer {
            val application = (this[APPLICATION_KEY] as FinanceApplication)
            val container = application.container

            TransactionListViewModel(
                repository = container.transactionRepository
            )
        }

        initializer {
            val application = (this[APPLICATION_KEY] as FinanceApplication)
            val container = application.container
            InsightsViewModel(repository = container.transactionRepository)
        }

        initializer {
            val application = (this[APPLICATION_KEY] as FinanceApplication)
            val container = application.container
            ProfileViewModel(
                auth = container.auth,
                firestore = container.firestore,
                repository = container.transactionRepository
            )
        }
    }
}