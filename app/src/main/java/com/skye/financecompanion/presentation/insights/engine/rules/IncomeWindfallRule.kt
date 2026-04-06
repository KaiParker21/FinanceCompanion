package com.skye.financecompanion.presentation.insights.engine.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.ui.graphics.Color
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.presentation.insights.SpendingInsight
import com.skye.financecompanion.presentation.insights.engine.InsightRule

class IncomeWindfallRule : InsightRule {
    override val priority: Int = 100

    override fun evaluate(transactions: List<Transaction>): SpendingInsight? {
        val incomes = transactions.filter { it.type == TransactionType.INCOME }

        val massiveIncome = incomes.maxByOrNull { it.amount }

        if (massiveIncome != null && massiveIncome.amount >= 30000.0) {
            val suggestedSavings = massiveIncome.amount * 0.20

            return SpendingInsight(
                title = "Payday!",
                description = "You received ₹${String.format("%,.0f", massiveIncome.amount)}. Rule of thumb: try moving ₹${String.format("%,.0f", suggestedSavings)} (20%) straight into your savings or investments.",
                icon = Icons.Rounded.TrendingUp,
                color = Color(0xFF4CAF50),
                trend = "Opportunity"
            )
        }
        return null
    }
}