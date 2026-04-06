package com.skye.financecompanion.presentation.insights.engine

import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.presentation.insights.SpendingInsight

interface InsightRule {
    val priority: Int
    fun evaluate(transactions: List<Transaction>): SpendingInsight?
}

class InsightEngine(
    private val rules: List<InsightRule>
) {
    fun generate(transactions: List<Transaction>): List<SpendingInsight> {
        return rules
            .sortedByDescending { it.priority }
            .mapNotNull { rule ->
                rule.evaluate(transactions)
            }
            .take(3)
    }
}