package com.skye.financecompanion.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.repository.TransactionRepository
import com.skye.financecompanion.domain.usecase.CalculateStreakUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// This data class represents the exact state of the Home Screen at any given moment
data class HomeUiState(
    val balance: Double = 0.0,
    val currentStreak: Int = 0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase
) : ViewModel() {

    // We use 'combine' to listen to the Balance, the Streak, and the Transactions all at once.
    // If ANY of them change in the Room database, the UI updates instantly.
    val uiState: StateFlow<HomeUiState> = combine(
        transactionRepository.getBalance(),
        calculateStreakUseCase(), // Using the invoke() operator we built
        transactionRepository.getAllTransactions()
    ) { balance, streak, transactions ->
        HomeUiState(
            balance = balance,
            currentStreak = streak,
            recentTransactions = transactions.take(5), // Only show 5 on the dashboard
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    // ADD THIS FUNCTION at the bottom of HomeViewModel
    fun addTransaction(
        amount: Double,
        type: com.skye.financecompanion.domain.model.TransactionType,
        category: com.skye.financecompanion.domain.model.Category,
        note: String,
        isEssential: Boolean
    ) {
        viewModelScope.launch {
            val transaction = com.skye.financecompanion.domain.model.Transaction(
                amount = amount,
                type = type,
                category = category,
                date = java.time.LocalDate.now(), // Defaults to today
                note = note,
                isEssential = isEssential
            )
            transactionRepository.insertTransaction(transaction)
        }
    }

}