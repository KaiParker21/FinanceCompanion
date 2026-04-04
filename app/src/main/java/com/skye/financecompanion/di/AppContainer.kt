package com.skye.financecompanion.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skye.financecompanion.data.local.FinanceDatabase
import com.skye.financecompanion.data.repository.TransactionRepositoryImpl
import com.skye.financecompanion.domain.repository.TransactionRepository
import com.skye.financecompanion.domain.usecase.CalculateStreakUseCase

// We will import the repository later when we build it

interface AppContainer {
    val auth: FirebaseAuth
    val firestore: FirebaseFirestore
    val transactionRepository: TransactionRepository
    val calculateStreakUseCase: CalculateStreakUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val database: FinanceDatabase by lazy {
        FinanceDatabase.getInstance(context)
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(database.transactionDao)
    }

    override val calculateStreakUseCase: CalculateStreakUseCase by lazy {
        CalculateStreakUseCase(transactionRepository)
    }
}