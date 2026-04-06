package com.skye.financecompanion.domain.usecase

import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import java.time.LocalDate
import java.time.YearMonth

class CalculateBurnRateUseCase {

    /**
     * Calculates the projected end-of-month spend based on current spending velocity.
     */
    operator fun invoke(transactions: List<Transaction>): Double {
        val today = LocalDate.now()
        val currentMonth = YearMonth.from(today)

        // 1. Filter for expenses that occurred in the current month
        val thisMonthExpenses = transactions.filter {
            it.type == TransactionType.EXPENSE &&
                    YearMonth.from(it.date) == currentMonth
        }

        if (thisMonthExpenses.isEmpty()) return 0.0

        // 2. Calculate Total Spent So Far
        val totalSpentSoFar = thisMonthExpenses.sumOf { it.amount }

        // 3. Calculate Elapsed Days
        // If today is the 5th, 5 days have elapsed.
        val daysElapsed = today.dayOfMonth.toDouble()

        // 4. Calculate Total Days in Month (e.g., 28, 30, or 31)
        val totalDaysInMonth = currentMonth.lengthOfMonth().toDouble()

        // 5. The Core Algorithm: Average Daily Burn * Total Days
        val dailyBurnRate = totalSpentSoFar / daysElapsed
        val projectedTotal = dailyBurnRate * totalDaysInMonth

        return projectedTotal
    }
}