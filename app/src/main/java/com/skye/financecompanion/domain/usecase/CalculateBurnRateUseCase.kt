package com.skye.financecompanion.domain.usecase

import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import java.time.LocalDate
import java.time.YearMonth

class CalculateBurnRateUseCase {

    operator fun invoke(transactions: List<Transaction>): Double {
        val today = LocalDate.now()
        val currentMonth = YearMonth.from(today)

        val thisMonthExpenses = transactions.filter {
            it.type == TransactionType.EXPENSE &&
                    YearMonth.from(it.date) == currentMonth
        }

        if (thisMonthExpenses.isEmpty()) return 0.0

        val totalSpentSoFar = thisMonthExpenses.sumOf { it.amount }

        val daysElapsed = today.dayOfMonth.toDouble()

        val totalDaysInMonth = currentMonth.lengthOfMonth().toDouble()

        val dailyBurnRate = totalSpentSoFar / daysElapsed
        val projectedTotal = dailyBurnRate * totalDaysInMonth

        return projectedTotal
    }
}