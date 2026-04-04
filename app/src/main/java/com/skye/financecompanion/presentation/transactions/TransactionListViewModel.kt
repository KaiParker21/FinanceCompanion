package com.skye.financecompanion.presentation.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true
)

class TransactionListViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    // We fetch ALL transactions here, without the .take(5) limit we used on the Home screen
    val uiState: StateFlow<TransactionListUiState> = repository.getAllTransactions()
        .map { transactions ->
            TransactionListUiState(
                transactions = transactions,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TransactionListUiState()
        )
}