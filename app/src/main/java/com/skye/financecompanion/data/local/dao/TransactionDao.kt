package com.skye.financecompanion.data.local.dao

import androidx.room.*
import com.skye.financecompanion.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // Removed 'suspend' and the return types!
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: TransactionEntity)

    // Removed 'suspend' and the return types!
    @Delete
    fun deleteTransaction(transaction: TransactionEntity)
}