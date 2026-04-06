package com.skye.financecompanion.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import com.skye.financecompanion.domain.usecase.CalculateBurnRateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CategoryTotal(
    val category: Category,
    val totalAmount: Double
)

data class InsightsUiState(
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val totalExpenseAmount: Double = 0.0,
    val projectedMonthEndSpend: Double = 0.0,
    val isLoading: Boolean = true // Starts true until data is emitted
)

class InsightsViewModel(
    private val repository: TransactionRepository,
    private val calculateBurnRate: CalculateBurnRateUseCase = CalculateBurnRateUseCase()
) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = repository.getAllTransactions()
        .map { transactions ->
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val total = expenses.sumOf { it.amount }

            val groupedExpenses = expenses
                .groupBy { it.category }
                .map { (category, txs) ->
                    CategoryTotal(
                        category = category,
                        totalAmount = txs.sumOf { it.amount }
                    )
                }
                .sortedByDescending { it.totalAmount }

            val projectedSpend = calculateBurnRate(transactions)

            InsightsUiState(
                categoryTotals = groupedExpenses,
                totalExpenseAmount = total,
                projectedMonthEndSpend = projectedSpend,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InsightsUiState(isLoading = true)
        )
}