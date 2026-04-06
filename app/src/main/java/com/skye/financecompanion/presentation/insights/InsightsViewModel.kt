package com.skye.financecompanion.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.model.ChartDataPoint
import com.skye.financecompanion.domain.model.TimeRange
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import com.skye.financecompanion.domain.usecase.CalculateBurnRateUseCase
import com.skye.financecompanion.presentation.insights.engine.InsightEngine
import com.skye.financecompanion.presentation.insights.engine.rules.DiningSpikeRule
import com.skye.financecompanion.presentation.insights.engine.rules.IncomeWindfallRule
import com.skye.financecompanion.presentation.insights.engine.rules.LargeTransactionRule
import com.skye.financecompanion.presentation.insights.engine.rules.MicroLeakRule
import com.skye.financecompanion.presentation.insights.engine.rules.WeekendWarriorRule
import com.skye.financecompanion.presentation.insights.engine.rules.ZeroSpendRule
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
    val chartData: List<ChartDataPoint> = emptyList(),
    val selectedTimeRange: TimeRange = TimeRange.WEEK,
    val targetDailyBudget: Float = 0f,
    val insights: List<SpendingInsight> = emptyList(),
    val isLoading: Boolean = true
)

class InsightsViewModel(
    private val repository: TransactionRepository,
    private val calculateBurnRate: CalculateBurnRateUseCase = CalculateBurnRateUseCase()
) : ViewModel() {

    private val insightEngine = InsightEngine(
        rules = listOf(
            LargeTransactionRule(),
            DiningSpikeRule(),
            IncomeWindfallRule(),
            WeekendWarriorRule(),
            ZeroSpendRule(),
            MicroLeakRule()
        )
    )

    private val _timeRange = MutableStateFlow(TimeRange.WEEK)

    val uiState: StateFlow<InsightsUiState> = combine(
        repository.getAllTransactions(),
        _timeRange
    ) { transactions, timeRange ->

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

        val today = LocalDate.now()
        val startDate = today.minusDays((timeRange.days - 1).toLong())

        val rangeExpenses = expenses.filter { !it.date.isBefore(startDate) }

        val expensesByDate = rangeExpenses
            .groupBy { it.date }
            .mapValues { entry -> entry.value.sumOf { it.amount }.toFloat() }

        val dataPoints = mutableListOf<ChartDataPoint>()
        for (i in 0 until timeRange.days) {
            val dateToCheck = startDate.plusDays(i.toLong())
            val amountThatDay = expensesByDate[dateToCheck] ?: 0f
            dataPoints.add(ChartDataPoint(dateToCheck, amountThatDay))
        }

        val averageDailySpend = if (dataPoints.sumOf { it.amount.toDouble() } > 0) {
            (dataPoints.sumOf { it.amount.toDouble() } / timeRange.days).toFloat()
        } else 50f

        val generatedInsights = insightEngine.generate(transactions)

        InsightsUiState(
            categoryTotals = groupedExpenses,
            totalExpenseAmount = total,
            projectedMonthEndSpend = projectedSpend,
            chartData = dataPoints,
            selectedTimeRange = timeRange,
            targetDailyBudget = averageDailySpend * 0.8f,
            insights = generatedInsights,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsUiState(isLoading = true)
    )

    fun setTimeRange(range: TimeRange) {
        _timeRange.update { range }
    }
}