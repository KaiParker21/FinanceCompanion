package com.skye.financecompanion.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.model.TransactionType
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.UUID

class ProfileViewModel(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val repository: TransactionRepository
) : ViewModel() {

    val userEmail = auth.currentUser?.email ?: "Unknown User"

    private val _syncState = MutableStateFlow("Idle")
    val syncState: StateFlow<String> = _syncState.asStateFlow()

    fun backupData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _syncState.value = "Backing up..."
            try {
                val localTransactions = repository.getAllTransactions().first()
                val batch = firestore.batch()
                val userRef = firestore.collection("users").document(userId)

                localTransactions.forEach { tx ->
                    val txRef = userRef.collection("transactions").document(tx.id)
                    val txMap = hashMapOf(
                        "amount" to tx.amount,
                        "type" to tx.type.name,
                        "category" to tx.category.name,
                        "date" to tx.date.toString(),
                        "note" to tx.note,
                        "isEssential" to tx.isEssential
                    )
                    batch.set(txRef, txMap)
                }

                batch.commit().await()
                _syncState.value = "Backup successful!"
            } catch (e: Exception) {
                _syncState.value = "Backup failed: ${e.message}"
            }
        }
    }

    fun restoreData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _syncState.value = "Restoring..."
            try {
                val snapshot = firestore.collection("users").document(userId)
                    .collection("transactions").get().await()

                snapshot.documents.forEach { doc ->
                    val transaction = Transaction(
                        id = doc.id,
                        amount = doc.getDouble("amount") ?: 0.0,
                        type = TransactionType.valueOf(doc.getString("type") ?: "EXPENSE"),
                        category = Category.fromString(doc.getString("category") ?: "OTHER"),
                        date = LocalDate.parse(doc.getString("date") ?: LocalDate.now().toString()),
                        note = doc.getString("note") ?: "",
                        isEssential = doc.getBoolean("isEssential") ?: true
                    )
                    repository.insertTransaction(transaction)
                }
                _syncState.value = "Restore complete!"
            } catch (e: Exception) {
                _syncState.value = "Restore failed: ${e.message}"
            }
        }
    }

    // NEW: The "Evaluator Friendly" Mock Data Generator
    fun loadMockData() {
        viewModelScope.launch {
            _syncState.value = "Loading demo data..."
            val today = LocalDate.now()

            val mockData = listOf(
                Transaction(UUID.randomUUID().toString(), 4500.0, TransactionType.INCOME, Category.SALARY, today.minusDays(5), "Monthly Salary", true),
                Transaction(UUID.randomUUID().toString(), 1200.0, TransactionType.EXPENSE, Category.BILLS, today.minusDays(4), "Rent", true),
                Transaction(UUID.randomUUID().toString(), 45.50, TransactionType.EXPENSE, Category.FOOD, today.minusDays(3), "Groceries", true),
                Transaction(UUID.randomUUID().toString(), 15.00, TransactionType.EXPENSE, Category.TRANSPORT, today.minusDays(2), "Train Ticket", true),
                Transaction(UUID.randomUUID().toString(), 120.0, TransactionType.EXPENSE, Category.SHOPPING, today.minusDays(1), "New Shoes", false), // Breaks streak
                Transaction(UUID.randomUUID().toString(), 25.00, TransactionType.EXPENSE, Category.FOOD, today, "Lunch out", false) // Breaks streak today
            )

            mockData.forEach { tx ->
                repository.insertTransaction(tx)
            }
            _syncState.value = "Demo data loaded!"
        }
    }

    fun resetSyncState() {
        _syncState.value = "Idle"
    }
}