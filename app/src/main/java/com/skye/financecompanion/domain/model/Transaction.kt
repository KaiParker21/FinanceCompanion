package com.skye.financecompanion.domain.model

import java.time.LocalDate
import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category(val displayName: String, val icon: String) {
    FOOD("Food", "🍔"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    ENTERTAINMENT("Fun", "🎮"),
    BILLS("Bills", "💳"),
    SALARY("Salary", "💰"),
    OTHER("Other", "✨")
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val date: LocalDate,
    val note: String = "",
    val isEssential: Boolean = true // Needed for your "No-Spend Streak" wow feature!
)