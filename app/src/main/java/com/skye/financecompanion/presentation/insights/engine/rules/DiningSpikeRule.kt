package com.skye.financecompanion.presentation.insights.engine.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.ui.graphics.Color
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.presentation.insights.SpendingInsight
import com.skye.financecompanion.presentation.insights.engine.InsightRule

class DiningSpikeRule : InsightRule {
    override val priority: Int = 80

    override fun evaluate(transactions: List<Transaction>): SpendingInsight? {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        if (expenses.isEmpty()) return null

        val totalSpend = expenses.sumOf { it.amount }
        val diningSpend = expenses.filter { it.category == Category.FOOD }.sumOf { it.amount }

        if (totalSpend > 0 && (diningSpend / totalSpend) > 0.40) {
            return SpendingInsight(
                title = "Dining Alert",
                description = "Over 40% of your recent spending is on food. Consider meal prepping this week to save.",
                icon = Icons.Rounded.Restaurant,
                color = Color(0xFFFF7043),
                trend = "High"
            )
        }
        return null
    }
}