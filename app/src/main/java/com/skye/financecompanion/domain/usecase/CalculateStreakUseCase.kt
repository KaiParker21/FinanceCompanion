package com.skye.financecompanion.domain.usecase

import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalculateStreakUseCase {
    operator fun invoke(transactions: List<Transaction>): Int {
        if (transactions.isEmpty()) return 0

        val badSpendingDays = transactions
            .filter { it.type == TransactionType.EXPENSE && !it.isEssential }
            .map { it.date }
            .distinct()
            .sortedDescending()

        val today = LocalDate.now()

        if (badSpendingDays.firstOrNull() == today) return 0

        val lastBadDay = badSpendingDays.firstOrNull() ?: return 30

        return ChronoUnit.DAYS.between(lastBadDay, today).toInt()
    }
}