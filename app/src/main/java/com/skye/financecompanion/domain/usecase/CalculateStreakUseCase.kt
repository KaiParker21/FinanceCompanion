package com.skye.financecompanion.domain.usecase

import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalculateStreakUseCase {
    operator fun invoke(transactions: List<Transaction>): Int {
        if (transactions.isEmpty()) return 0

        // 1. Get all non-essential expenses, sorted by date (newest first)
        val badSpendingDays = transactions
            .filter { it.type == TransactionType.EXPENSE && !it.isEssential }
            .map { it.date }
            .distinct()
            .sortedDescending()

        val today = LocalDate.now()

        // 2. If they spent money on garbage today, streak is 0
        if (badSpendingDays.firstOrNull() == today) return 0

        // 3. Otherwise, count days between today and the last "bad" purchase
        val lastBadDay = badSpendingDays.firstOrNull() ?: return 30 // Max 30 if they've been perfect

        return ChronoUnit.DAYS.between(lastBadDay, today).toInt()
    }
}