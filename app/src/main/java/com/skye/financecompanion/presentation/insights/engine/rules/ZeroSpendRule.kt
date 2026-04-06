package com.skye.financecompanion.presentation.insights.engine.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.Color
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.presentation.insights.SpendingInsight
import com.skye.financecompanion.presentation.insights.engine.InsightRule
import java.time.LocalDate

class ZeroSpendRule : InsightRule {
    override val priority: Int = 85

    override fun evaluate(transactions: List<Transaction>): SpendingInsight? {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val twoDaysAgo = today.minusDays(2)

        val spentInLastThreeDays = expenses.any {
            it.date == today || it.date == yesterday || it.date == twoDaysAgo
        }

        if (!spentInLastThreeDays) {
            return SpendingInsight(
                title = "Flawless Victory",
                description = "You haven't spent a single Rupee in 3 days! Your wallet is thanking you right now.",
                icon = Icons.Rounded.Star,
                color = Color(0xFFFFD54F),
                trend = "Streak"
            )
        }
        return null
    }
}