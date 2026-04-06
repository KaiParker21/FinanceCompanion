package com.skye.financecompanion.presentation.insights.engine.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.Color
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.presentation.insights.SpendingInsight
import com.skye.financecompanion.presentation.insights.engine.InsightRule

class LargeTransactionRule : InsightRule {
    override val priority: Int = 90

    override fun evaluate(transactions: List<Transaction>): SpendingInsight? {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

        val massiveExpense = expenses.maxByOrNull { it.amount }

        if (massiveExpense != null && massiveExpense.amount > 10000.0) {
            return SpendingInsight(
                title = "Large Outflow Detected",
                description = "You had a massive ${massiveExpense.category.displayName} expense of ₹${String.format("%,.0f", massiveExpense.amount)}. Make sure this was planned.",
                icon = Icons.Rounded.Warning,
                color = Color(0xFFE53935),
                trend = "Review"
            )
        }
        return null
    }
}