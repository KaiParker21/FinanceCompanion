package com.skye.financecompanion.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category(
    val displayName: String,
    val icon: ImageVector
) {
    // Expenses
    FOOD("Food & Dining", Icons.Rounded.Restaurant),
    TRANSPORT("Transport", Icons.Rounded.DirectionsCar),
    SHOPPING("Shopping", Icons.Rounded.ShoppingBag),
    ENTERTAINMENT("Entertainment", Icons.Rounded.ConfirmationNumber),
    HEALTH("Health", Icons.Rounded.MedicalServices),
    BILLS("Bills & Utilities", Icons.Rounded.ReceiptLong),
    EDUCATION("Education", Icons.Rounded.School),

    // Income
    SALARY("Salary", Icons.Rounded.Payments),
    INVESTMENTS("Investments", Icons.Rounded.TrendingUp),
    GIFTS("Gifts", Icons.Rounded.CardGiftcard),

    // Catch-all
    OTHER("Other", Icons.Rounded.Category);

    companion object {
        // Helper to get enum from string if needed for database mapping
        fun fromString(value: String): Category {
            return entries.find { it.name == value } ?: OTHER
        }
    }
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