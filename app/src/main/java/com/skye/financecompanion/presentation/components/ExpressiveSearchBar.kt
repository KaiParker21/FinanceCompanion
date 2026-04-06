package com.skye.financecompanion.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skye.financecompanion.domain.model.Transaction
import com.skye.financecompanion.presentation.transactions.HistoryTransactionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpressiveSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    searchResults: List<Transaction>,
    modifier: Modifier = Modifier
) {
    // Controls whether the search bar is a pill or full-screen
    var isActive by remember { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { isActive = false }, // Hide keyboard when they press enter
        active = isActive,
        onActiveChange = { isActive = it },
        placeholder = { Text("Search transactions...") },
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = "Search Icon")
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = isActive || query.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = {
                    if (query.isNotEmpty()) {
                        onClear()
                    } else {
                        isActive = false // Close search if already empty
                    }
                }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear Search")
                }
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            dividerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (isActive) 0.dp else 16.dp) // Expands to edges when active
    ) {
        // --- THIS IS THE FULL-SCREEN EXPANDED VIEW ---
        if (searchResults.isEmpty() && query.isNotEmpty()) {
            // Empty State
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "No transactions found for \"$query\"",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { tx ->
                    // Assuming you have a TransactionItem component already built!
                    // TransactionItem(transaction = tx)

                    // Fallback simple list item if you don't have a component yet:
                    HistoryTransactionItem(tx)
                }
            }
        }
    }
}