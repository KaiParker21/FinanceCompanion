package com.skye.financecompanion.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// We will import the repository later when we build it

interface AppContainer {
    val auth: FirebaseAuth
    val firestore: FirebaseFirestore
    // val transactionRepository: TransactionRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

// We will initialize the Room Database and Repository here later
}