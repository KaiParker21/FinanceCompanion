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
                // 1. Grab all current local transactions
                val localTransactions = repository.getAllTransactions().first()

                // 2. Prepare a massive Firestore batch write
                val batch = firestore.batch()
                val userRef = firestore.collection("users").document(userId)

                localTransactions.forEach { tx ->
                    val txRef = userRef.collection("transactions").document(tx.id)
                    val txMap = hashMapOf(
                        "amount" to tx.amount,
                        "type" to tx.type.name,
                        "category" to tx.category.name,
                        "date" to tx.date.toString(), // Firestore doesn't like Java LocalDates directly
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
                        // doc.id is the unique String ID from Firestore.
                        // This ensures the local Room ID matches the Cloud ID!
                        id = doc.id,
                        amount = doc.getDouble("amount") ?: 0.0,
                        type = TransactionType.valueOf(doc.getString("type") ?: "EXPENSE"),
                        category = Category.valueOf(doc.getString("category") ?: "OTHER"),
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
}