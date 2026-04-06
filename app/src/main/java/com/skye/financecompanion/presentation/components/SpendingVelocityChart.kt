package com.skye.financecompanion.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skye.financecompanion.domain.model.ChartDataPoint
import com.skye.financecompanion.domain.model.TimeRange
import java.time.format.DateTimeFormatter
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

    val headerFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val axisFormatter = if (selectedRange == TimeRange.WEEK) {
        DateTimeFormatter.ofPattern("E")
    } else {
        DateTimeFormatter.ofPattern("MMM d")
    }

    LaunchedEffect(dataPoints) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(1200))
    }

    Column(modifier = modifier.fillMaxWidth()) {
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

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                TimeRange.entries.forEach { range ->
                    val isSelected = selectedRange == range
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                selectedIndex = null
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

        val haptic = LocalHapticFeedback.current
        var lastHapticIndex by remember { mutableStateOf<Int?>(null) }


        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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

            val leftPadding = 100f
            val bottomPadding = 60f

            val graphWidth = canvasWidth - leftPadding
            val graphHeight = canvasHeight - bottomPadding

            val rawMax = dataPoints.maxOfOrNull { it.amount } ?: 0f
            val maxAmount = if (rawMax > 0) (ceil(rawMax / 50.0) * 50).toFloat() else 100f

            val spacingX = graphWidth / (dataPoints.size - 1).coerceAtLeast(1).toFloat()

            val ySteps = 4
            for (i in 0..ySteps) {
                val stepAmount = maxAmount * (i.toFloat() / ySteps)
                val yPos = graphHeight - ((stepAmount / maxAmount) * graphHeight)

                drawLine(
                    color = gridColor,
                    start = Offset(leftPadding, yPos),
                    end = Offset(canvasWidth, yPos),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = "₹${stepAmount.toInt()}",
                    topLeft = Offset(0f, yPos - 20f),
                    style = TextStyle(color = textColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                )
            }

            val points = dataPoints.mapIndexed { index, point ->
                val x = leftPadding + (index * spacingX)
                val y = graphHeight - ((point.amount / maxAmount) * graphHeight)
                Offset(x, y)
            }

            if (touchX >= leftPadding) {
                selectedIndex = ((touchX - leftPadding) / spacingX).roundToInt().coerceIn(0, points.size - 1)
            }

            val labelStep = if (dataPoints.size > 10) dataPoints.size / 5 else 1

            dataPoints.forEachIndexed { index, point ->
                if (index % labelStep == 0 || index == dataPoints.size - 1) {
                    val xPos = leftPadding + (index * spacingX)
                    val dateText = point.date.format(axisFormatter)

                    drawText(
                        textMeasurer = textMeasurer,
                        text = dateText,
                        topLeft = Offset(xPos - 30f, graphHeight + 16f),
                        style = TextStyle(color = textColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

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

                if (selectedIndex != null && selectedIndex != lastHapticIndex) {
                    val targetPoint = points[selectedIndex!!]
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    lastHapticIndex = selectedIndex
                    drawLine(
                        color = lineColor.copy(alpha = 0.5f),
                        start = Offset(targetPoint.x, 0f),
                        end = Offset(targetPoint.x, graphHeight),
                        strokeWidth = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                    )

                    drawCircle(color = lineColor.copy(alpha = 0.3f), radius = 24f, center = targetPoint)
                    drawCircle(color = lineColor, radius = 12f, center = targetPoint)
                    drawCircle(color = Color.White, radius = 6f, center = targetPoint)
                }
            }
        }
    }
}