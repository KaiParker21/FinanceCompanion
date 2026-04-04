package com.skye.financecompanion.domain.usecase

import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class CalculateStreakUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getAllTransactions().map { transactions ->
            if (transactions.isEmpty()) return@map 0

            var streak = 0
            var currentDate = LocalDate.now()

            // Find the oldest transaction date so we know when to stop looping!
            val oldestDate = transactions.minOfOrNull { it.date } ?: return@map 0

            val transactionsByDate = transactions.groupBy { it.date }

            // FIX: Only loop until we hit the oldest transaction date
            while (!currentDate.isBefore(oldestDate)) {
                val dailyTransactions = transactionsByDate[currentDate] ?: emptyList()

                val hasNonEssentialExpense = dailyTransactions.any {
                    it.type == TransactionType.EXPENSE && !it.isEssential
                }

                if (hasNonEssentialExpense) {
                    break // Streak broken
                } else {
                    streak++
                    currentDate = currentDate.minusDays(1)
                }
            }
            streak
        }
    }
}