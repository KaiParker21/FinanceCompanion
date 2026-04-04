package com.skye.financecompanion.data.repository

import com.skye.financecompanion.data.local.dao.TransactionDao
import com.skye.financecompanion.data.mapper.toDomain
import com.skye.financecompanion.data.mapper.toEntity
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers // ADD THIS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext // ADD THIS

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBalance(): Flow<Double> {
        return dao.getAllTransactions().map { entities ->
            entities.sumOf {
                if (it.type == TransactionType.INCOME) it.amount else -it.amount
            }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        // Manually push this database write to the background IO thread
        withContext(Dispatchers.IO) {
            dao.insertTransaction(transaction.toEntity())
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        // Manually push this database delete to the background IO thread
        withContext(Dispatchers.IO) {
            dao.deleteTransaction(transaction.toEntity())
        }
    }
}