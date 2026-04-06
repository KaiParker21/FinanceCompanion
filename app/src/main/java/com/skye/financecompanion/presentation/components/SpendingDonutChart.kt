package com.skye.financecompanion.presentation.components

import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ChartSlice(
    val categoryName: String,
    val amount: Float,
    val color: Color
)

@Composable
fun SpendingDonutChart(
    slices: List<ChartSlice>,
    totalSpent: Double,
    modifier: Modifier = Modifier
) {
    val total = slices.sumOf { it.amount.toDouble() }.toFloat()


    // Smooth sweep animation on load
    var animationPlayed by remember { mutableStateOf(false) }
    val animateSweep by animateFloatAsState(
        targetValue = if (animationPlayed) 360f else 0f,
        animationSpec = FloatTweenSpec(duration = 1200),
        label = "donut_chart_anim"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        val trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 36.dp.toPx()
            val innerRadius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Draw the track background (soft variant color)
            drawCircle(
                color = trackColor,
                radius = innerRadius,
                center = center,
                style = Stroke(width = strokeWidth)
            )

            if (total > 0) {
                var startAngle = -90f // Start at the top (12 o'clock)

                slices.forEach { slice ->
                    val sweepAngle = (slice.amount / total) * animateSweep
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    // Add a tiny gap between slices
                    startAngle += (sweepAngle + 2f)
                }
            }
        }

        // Center Data
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Total Spent",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹${String.format("%.2f", totalSpent)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}