package com.skye.financecompanion.domain.usecase

import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class CalculateStreakUseCase(
    private val repository: TransactionRepository
) {
    /**
     * The 'invoke' operator allows us to call this class like a function.
     */
    operator fun invoke(): Flow<Int> {
        return repository.getAllTransactions().map { transactions ->
            var streak = 0
            var currentDate = LocalDate.now()

            // Group transactions by date for fast lookup
            val transactionsByDate = transactions.groupBy { it.date }

            while (true) {
                val dailyTransactions = transactionsByDate[currentDate] ?: emptyList()

                // If they spent on something non-essential, the streak ends
                val hasNonEssentialExpense = dailyTransactions.any {
                    it.type == TransactionType.EXPENSE && !it.isEssential
                }

                if (hasNonEssentialExpense) {
                    break
                } else {
                    // No bad spending today! Increment and check yesterday
                    streak++
                    currentDate = currentDate.minusDays(1)
                }
            }
            streak
        }
    }
}