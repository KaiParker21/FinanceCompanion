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

// 1. A clean data class to hold the mapped data for the UI
data class CategoryTotal(
    val category: Category,
    val totalAmount: Double
)

// 2. State mapped perfectly to what the InsightsScreen needs
data class InsightsUiState(
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val totalExpenseAmount: Double = 0.0,
    val isLoading: Boolean = true // Starts true until data is emitted
)

class InsightsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = repository.getAllTransactions()
        .map { transactions ->
            // Filter out income, we only want to chart expenses
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

            // Calculate the total spent for the center of the Donut Chart
            val total = expenses.sumOf { it.amount }

            // Group by Category, sum the amounts, and map to our clean data class
            val groupedExpenses = expenses
                .groupBy { it.category }
                .map { (category, txs) ->
                    CategoryTotal(
                        category = category,
                        totalAmount = txs.sumOf { it.amount }
                    )
                }
                .sortedByDescending { it.totalAmount } // Highest spenders at the top

            InsightsUiState(
                categoryTotals = groupedExpenses,
                totalExpenseAmount = total,
                isLoading = false // Data has arrived
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InsightsUiState(isLoading = true)
        )
}