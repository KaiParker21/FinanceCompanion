package com.skye.financecompanion.presentation.insights.engine.rules

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Celebration
import androidx.compose.ui.graphics.Color
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.presentation.insights.SpendingInsight
import com.skye.financecompanion.presentation.insights.engine.InsightRule
import java.time.DayOfWeek

class WeekendWarriorRule : InsightRule {
    override val priority: Int = 60

    override fun evaluate(transactions: List<Transaction>): SpendingInsight? {
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
        if (expenses.isEmpty()) return null

        val totalSpend = expenses.sumOf { it.amount }

        val weekendSpend = expenses.filter {
            it.date.dayOfWeek == DayOfWeek.SATURDAY || it.date.dayOfWeek == DayOfWeek.SUNDAY
        }.sumOf { it.amount }

        if (totalSpend > 0 && (weekendSpend / totalSpend) > 0.50) {
            return SpendingInsight(
                title = "Weekend Warrior",
                description = "Over 50% of your expenses happen on the weekend. Try finding some free weekend activities to balance your burn rate.",
                icon = Icons.Rounded.Celebration,
                color = Color(0xFFAB47BC),
                trend = "Behavior"
            )
        }
        return null
    }
}