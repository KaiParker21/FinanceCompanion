package com.skye.financecompanion.presentation.insights.engine.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.ui.graphics.Color
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.presentation.insights.SpendingInsight
import com.skye.financecompanion.presentation.insights.engine.InsightRule

class MicroLeakRule : InsightRule {
    override val priority: Int = 70

    override fun evaluate(transactions: List<Transaction>): SpendingInsight? {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

        val microTransactions = expenses.filter { it.amount <= 200.0 }

        if (microTransactions.size >= 7) {
            val totalMicroSpend = microTransactions.sumOf { it.amount }

            return SpendingInsight(
                title = "Death by a Thousand \nCuts",
                description = "You've made ${microTransactions.size} purchases under ₹200 recently, totaling ₹${String.format("%,.0f", totalMicroSpend)}. Watch out for those small leaks!",
                icon = Icons.Rounded.Coffee,
                color = Color(0xFFF06292),
                trend = "Habit"
            )
        }
        return null
    }
}