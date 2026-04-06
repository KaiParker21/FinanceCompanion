package com.skye.financecompanion.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skye.financecompanion.domain.model.ChartDataPoint
import com.skye.financecompanion.domain.model.TimeRange
import java.time.format.DateTimeFormatter
import kotlin.collections.forEachIndexed
import kotlin.collections.mapIndexed
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun SpendingVelocityChart(
    dataPoints: List<ChartDataPoint>,
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit,
    dailyBudget: Float,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    val lineColor = MaterialTheme.colorScheme.primary
    val gradientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    val gridColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()

    val animationProgress = remember { Animatable(0f) }
    var touchX by remember { mutableStateOf(-1f) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // Date formatters depending on the range
    val headerFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val axisFormatter = if (selectedRange == TimeRange.WEEK) {
        DateTimeFormatter.ofPattern("E") // Mon, Tue, Wed
    } else {
        DateTimeFormatter.ofPattern("MMM d") // Oct 4, Oct 10
    }

    LaunchedEffect(dataPoints) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(1200))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // --- 1. SLIDING WINDOW FILTERS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Velocity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // The Pill Selector
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                TimeRange.values().forEach { range ->
                    val isSelected = selectedRange == range
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                selectedIndex = null // Reset crosshair
                                onRangeSelected(range)
                            }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = range.title,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- 2. DYNAMIC CROSSHAIR HEADER ---
        Box(modifier = Modifier.height(24.dp).padding(bottom = 8.dp)) {
            if (selectedIndex != null) {
                val point = dataPoints[selectedIndex!!]
                Text(
                    text = "${point.date.format(headerFormatter)}: ₹${String.format("%.2f", point.amount)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // --- 3. THE CANVAS CHART ---
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Slightly taller to fit the X-axis text
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            touchX = offset.x
                            tryAwaitRelease()
                            touchX = -1f; selectedIndex = null
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> touchX = offset.x },
                        onDragEnd = { touchX = -1f; selectedIndex = null },
                        onDragCancel = { touchX = -1f; selectedIndex = null },
                        onDrag = { change, _ -> touchX = change.position.x }
                    )
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Padding for the Axes
            val leftPadding = 100f
            val bottomPadding = 60f

            val graphWidth = canvasWidth - leftPadding
            val graphHeight = canvasHeight - bottomPadding

            val rawMax = dataPoints.maxOfOrNull { it.amount } ?: 0f
            // Dynamic Ceiling (Round up to nearest 50 for clean Y-axis labels)
            val maxAmount = if (rawMax > 0) (ceil(rawMax / 50.0) * 50).toFloat() else 100f

            val spacingX = graphWidth / (dataPoints.size - 1).coerceAtLeast(1).toFloat()

            // --- DRAW Y-AXIS (Amounts & Gridlines) ---
            val ySteps = 4
            for (i in 0..ySteps) {
                val stepAmount = maxAmount * (i.toFloat() / ySteps)
                val yPos = graphHeight - ((stepAmount / maxAmount) * graphHeight)

                // Grid line
                drawLine(
                    color = gridColor,
                    start = Offset(leftPadding, yPos),
                    end = Offset(canvasWidth, yPos),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )

                // Y-Axis Text
                drawText(
                    textMeasurer = textMeasurer,
                    text = "₹${stepAmount.toInt()}",
                    topLeft = Offset(0f, yPos - 20f), // Shift up slightly to center on line
                    style = TextStyle(color = textColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                )
            }

            // --- CALCULATE DATA POINTS ---
            val points = dataPoints.mapIndexed { index, point ->
                val x = leftPadding + (index * spacingX)
                val y = graphHeight - ((point.amount / maxAmount) * graphHeight)
                Offset(x, y)
            }

            // Snapping logic for touch
            if (touchX >= leftPadding) {
                selectedIndex = ((touchX - leftPadding) / spacingX).roundToInt().coerceIn(0, points.size - 1)
            }

            // --- DRAW X-AXIS (Dates) ---
            // For 30 days, we don't want to draw 30 labels (it overlaps). We draw ~5.
            val labelStep = if (dataPoints.size > 10) dataPoints.size / 5 else 1

            dataPoints.forEachIndexed { index, point ->
                if (index % labelStep == 0 || index == dataPoints.size - 1) {
                    val xPos = leftPadding + (index * spacingX)
                    val dateText = point.date.format(axisFormatter)

                    drawText(
                        textMeasurer = textMeasurer,
                        text = dateText,
                        topLeft = Offset(xPos - 30f, graphHeight + 16f), // Center text below tick
                        style = TextStyle(color = textColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            // --- DRAW THE SPLINE CURVE ---
            val path = Path()
            val fillPath = Path()

            if (points.isNotEmpty()) {
                path.moveTo(points.first().x, points.first().y)
                fillPath.moveTo(points.first().x, graphHeight)
                fillPath.lineTo(points.first().x, points.first().y)

                for (i in 0 until points.size - 1) {
                    val p1 = points[i]
                    val p2 = points[i + 1]
                    val controlX = (p1.x + p2.x) / 2f

                    path.cubicTo(controlX, p1.y, controlX, p2.y, p2.x, p2.y)
                    fillPath.cubicTo(controlX, p1.y, controlX, p2.y, p2.x, p2.y)
                }

                fillPath.lineTo(points.last().x, graphHeight)
                fillPath.close()
            }

            // Animate drawing from left to right
            clipRect(right = leftPadding + (graphWidth * animationProgress.value)) {

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientColor, Color.Transparent),
                        startY = 0f,
                        endY = graphHeight
                    )
                )

                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // --- DRAW INTERACTIVE CROSSHAIR ---
                if (selectedIndex != null) {
                    val targetPoint = points[selectedIndex!!]

                    // Vertical Line
                    drawLine(
                        color = lineColor.copy(alpha = 0.5f),
                        start = Offset(targetPoint.x, 0f),
                        end = Offset(targetPoint.x, graphHeight),
                        strokeWidth = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )

                    // Glow Dot
                    drawCircle(color = lineColor.copy(alpha = 0.3f), radius = 24f, center = targetPoint)
                    // Solid Dot
                    drawCircle(color = lineColor, radius = 12f, center = targetPoint)
                    // White Center
                    drawCircle(color = Color.White, radius = 6f, center = targetPoint)
                }
            }
        }
    }
}