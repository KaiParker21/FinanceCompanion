package com.skye.financecompanion.domain.model

import java.time.LocalDate

// 1. The options for our sliding window
enum class TimeRange(val title: String, val days: Int) {
    WEEK("7 Days", 7),
    MONTH("30 Days", 30)
}

// 2. The new data structure for our chart
data class ChartDataPoint(
    val date: LocalDate,
    val amount: Float
)