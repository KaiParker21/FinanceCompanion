package com.skye.financecompanion.domain.model

import java.time.LocalDate

enum class TimeRange(val title: String, val days: Int) {
    WEEK("7 Days", 7),
    MONTH("30 Days", 30)
}

data class ChartDataPoint(
    val date: LocalDate,
    val amount: Float
)