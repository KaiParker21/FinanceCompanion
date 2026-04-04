package com.skye.financecompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val dateMillis: Long, // Room cannot store LocalDate natively, so we store timestamps
    val note: String,
    val isEssential: Boolean
)