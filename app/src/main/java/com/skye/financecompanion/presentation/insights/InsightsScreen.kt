package com.skye.financecompanion.presentation.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skye.financecompanion.domain.model.Category
import com.skye.financecompanion.presentation.components.ChartSlice
import com.skye.financecompanion.presentation.components.SpendingDonutChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel
) {
    // Assuming your ViewModel exposes an InsightsUiState
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (uiState.categoryTotals.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No spending data available yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        // Define our custom Midnight Ocean palette for the chart
        val chartColors = listOf(
            MaterialTheme.colorScheme.inversePrimary,  // Vibrant Sky Blue
            MaterialTheme.colorScheme.tertiary,      // #B3CFE5 - Ice Blue (The "Pop")
            Color(0xFF6495ED),                        // Cornflower Blue (A classic, clean blue)
            Color(0xFF4682B4),                        // Steel Blue (Professional & crisp)
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f) // Muted Deep Sea
        )
        // Map the real data to the ChartSlice data class
        val slices = uiState.categoryTotals.mapIndexed { index, categoryTotal ->
            ChartSlice(
                categoryName = categoryTotal.category.displayName,
                amount = categoryTotal.totalAmount.toFloat(),
                // Cycle through colors if there are more categories than colors
                color = chartColors[index % chartColors.size]
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. The Donut Chart
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SpendingDonutChart(
                    slices = slices,
                    totalSpent = uiState.totalExpenseAmount
                )
                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = "Top Spending Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. The Legend / Category Breakdown List
            items(uiState.categoryTotals.size) { index ->
                val categoryTotal = uiState.categoryTotals[index]
                val color = chartColors[index % chartColors.size]

                CategoryBreakdownItem(
                    category = categoryTotal.category,
                    amount = categoryTotal.totalAmount,
                    percentage = (categoryTotal.totalAmount / uiState.totalExpenseAmount) * 100,
                    dotColor = color
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryBreakdownItem(
    category: Category,
    amount: Double,
    percentage: Double,
    dotColor: androidx.compose.ui.graphics.Color
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color Indicator Dot
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(dotColor, RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Icon
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Text Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.1f", percentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$${String.format("%.2f", amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}