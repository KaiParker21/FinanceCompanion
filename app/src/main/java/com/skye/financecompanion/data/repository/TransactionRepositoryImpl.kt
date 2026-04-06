package com.skye.financecompanion.data.repository

import com.skye.financecompanion.data.local.dao.TransactionDao
import com.skye.financecompanion.data.mapper.toDomain
import com.skye.financecompanion.data.mapper.toEntity
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

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
        withContext(Dispatchers.IO) {
            dao.insertTransaction(transaction.toEntity())
        }
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        withContext(Dispatchers.IO) {
            dao.deleteTransaction(transaction.toEntity())
        }
    }
}