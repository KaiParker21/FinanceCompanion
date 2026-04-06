package com.skye.financecompanion.domain.insights

import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.presentation.insights.SpendingInsight

interface InsightRule {
    val priority: Int
    fun evaluate(transactions: List<Transaction>): SpendingInsight?
}