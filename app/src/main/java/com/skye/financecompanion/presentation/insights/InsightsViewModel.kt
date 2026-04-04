package com.skye.financecompanion.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class InsightsUiState(
    val categoryExpenses: List<Pair<Category, Double>> = emptyList(),
    val totalExpense: Double = 0.0,
    val isEmpty: Boolean = true
)

class InsightsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = repository.getAllTransactions()
        .map { transactions ->
            // 1. Only look at expenses
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

            // 2. Calculate the total spent
            val total = expenses.sumOf { it.amount }

            // 3. Group by Category, sum the amounts, and sort highest to lowest
            val groupedExpenses = expenses
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
                .toList()
                .sortedByDescending { it.second }

            InsightsUiState(
                categoryExpenses = groupedExpenses,
                totalExpense = total,
                isEmpty = expenses.isEmpty()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InsightsUiState()
        )
}