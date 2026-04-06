package com.skye.financecompanion.presentation.insights

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SpendingInsight(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val trend: String
)