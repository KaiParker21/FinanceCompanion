package com.skye.financecompanion.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.repository.TransactionRepository
import com.skye.financecompanion.domain.usecase.CalculateStreakUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val balance: Double = 0.0,
    val currentStreak: Int = 0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val calculateStreakUseCase: CalculateStreakUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        transactionRepository.getBalance(),
        transactionRepository.getAllTransactions()
    ) { balance, transactions ->
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val streak = calculateStreakUseCase(transactions)

        HomeUiState(
            balance = balance,
            currentStreak = streak,
            totalIncome = income,
            totalExpense = expense,
            recentTransactions = transactions.take(5),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        category: Category,
        note: String,
        isEssential: Boolean
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                date = LocalDate.now(),
                note = note,
                isEssential = isEssential
            )
            transactionRepository.insertTransaction(transaction)
        }
    }
}