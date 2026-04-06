package com.skye.financecompanion.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

class TransactionListViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<TransactionListUiState> = combine(
        repository.getAllTransactions(),
        _searchQuery
    ) { transactions, query ->

        val filteredTransactions = if (query.isBlank()) {
            transactions
        } else {
            val lowerQuery = query.lowercase()
            transactions.filter { tx ->
                tx.note.lowercase().contains(lowerQuery) ||
                        tx.category.displayName.lowercase().contains(lowerQuery) ||
                        tx.amount.toString().contains(lowerQuery)
            }
        }

        TransactionListUiState(
            transactions = filteredTransactions,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionListUiState(isLoading = true)
    )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.update { newQuery }
    }

    fun clearSearch() {
        _searchQuery.update { "" }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}