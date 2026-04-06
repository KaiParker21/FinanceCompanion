package com.skye.financecompanion.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.model.ChartDataPoint
import com.skye.financecompanion.domain.model.TimeRange
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import com.skye.financecompanion.domain.usecase.CalculateBurnRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class CategoryTotal(
    val category: Category,
    val totalAmount: Double
)

data class InsightsUiState(
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val totalExpenseAmount: Double = 0.0,
    val projectedMonthEndSpend: Double = 0.0,
    val chartData: List<ChartDataPoint> = emptyList(), // Feeds the interactive chart
    val selectedTimeRange: TimeRange = TimeRange.WEEK, // Tracks current selection
    val targetDailyBudget: Float = 0f,
    val isLoading: Boolean = true
)

class InsightsViewModel(
    private val repository: TransactionRepository,
    private val calculateBurnRate: CalculateBurnRateUseCase = CalculateBurnRateUseCase()
) : ViewModel() {

    // 1. Hold the current selected time range (Defaults to 7 Days)
    private val _timeRange = MutableStateFlow(TimeRange.WEEK)

    // 2. COMBINE the database flow AND the time range flow
    val uiState: StateFlow<InsightsUiState> = combine(
        repository.getAllTransactions(),
        _timeRange
    ) { transactions, timeRange ->

        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        val total = expenses.sumOf { it.amount }

        // Donut Chart Data (Grouped by Category)
        val groupedExpenses = expenses
            .groupBy { it.category }
            .map { (category, txs) ->
                CategoryTotal(
                    category = category,
                    totalAmount = txs.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.totalAmount }

        // AI Forecast Data
        val projectedSpend = calculateBurnRate(transactions)

        // --- DYNAMIC CHART DATA CALCULATION ---
        val today = LocalDate.now()
        // If WEEK (7 days), we subtract 6 days from today to get a 7-day inclusive window
        val startDate = today.minusDays((timeRange.days - 1).toLong())

        val rangeExpenses = expenses.filter { !it.date.isBefore(startDate) }

        val expensesByDate = rangeExpenses
            .groupBy { it.date }
            .mapValues { entry -> entry.value.sumOf { it.amount }.toFloat() }

        // Build the precise array of DataPoints for the Canvas
        val dataPoints = mutableListOf<ChartDataPoint>()
        for (i in 0 until timeRange.days) {
            val dateToCheck = startDate.plusDays(i.toLong())
            val amountThatDay = expensesByDate[dateToCheck] ?: 0f
            dataPoints.add(ChartDataPoint(dateToCheck, amountThatDay))
        }

        // Calculate budget based on this specific time window
        val averageDailySpend = if (dataPoints.sumOf { it.amount.toDouble() } > 0) {
            (dataPoints.sumOf { it.amount.toDouble() } / timeRange.days).toFloat()
        } else 50f

        InsightsUiState(
            categoryTotals = groupedExpenses,
            totalExpenseAmount = total,
            projectedMonthEndSpend = projectedSpend,
            chartData = dataPoints,
            selectedTimeRange = timeRange,
            targetDailyBudget = averageDailySpend * 0.8f,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsUiState(isLoading = true)
    )

    // 3. NEW: The function called when a user taps a button on the chart
    fun setTimeRange(range: TimeRange) {
        _timeRange.update { range }
    }
}