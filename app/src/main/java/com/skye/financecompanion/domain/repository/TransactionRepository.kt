package com.skye.financecompanion.domain.repository


import com.skye.financecompanion.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    // Flow allows the UI to automatically update when the database changes
    fun getAllTransactions(): Flow<List<Transaction>>

    fun getBalance(): Flow<Double>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    // We will add the Streak calculation function here later
}