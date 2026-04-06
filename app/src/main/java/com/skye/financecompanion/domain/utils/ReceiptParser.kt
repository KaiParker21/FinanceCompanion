package com.skye.financecompanion.domain.utils

import com.skye.financecompanion.domain.model.Category

data class ParsedReceipt(
    val amount: Double?,
    val category: Category,
    val note: String
)

object ReceiptParser {

    fun parse(rawText: String): ParsedReceipt {
        val lines = rawText.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
        val lowerText = rawText.lowercase()

        val priceRegex = Regex("""([0-9]{1,3}(?:,[0-9]{3})*\.[0-9]{2})""")

        var finalAmount: Double? = null

        for ((index, line) in lines.withIndex()) {
            val lowerLine = line.lowercase()

            if (lowerLine.contains("total") && !lowerLine.contains("sub") && !lowerLine.contains("tax")) {

                var match = priceRegex.find(lowerLine)
                if (match != null) {
                    finalAmount = match.groupValues[1].replace(",", "").toDoubleOrNull()
                    if (finalAmount != null) break
                }

                if (index + 1 < lines.size) {
                    match = priceRegex.find(lines[index + 1])
                    if (match != null) {
                        finalAmount = match.groupValues[1].replace(",", "").toDoubleOrNull()
                        if (finalAmount != null) break
                    }
                }
            }
        }

        if (finalAmount == null) {
            val allPrices = priceRegex.findAll(rawText).mapNotNull {
                it.groupValues[1].replace(",", "").toDoubleOrNull()
            }.toList()

            finalAmount = allPrices.maxOrNull()
        }

        val category = when {
            listOf("restaurant", "cafe", "grill", "tip", "server", "table", "dine", "eat", "food").any { lowerText.contains(it) } -> Category.FOOD
            listOf("uber", "lyft", "taxi", "transit", "train", "flight").any { lowerText.contains(it) } -> Category.TRANSPORT
            listOf("walmart", "target", "store", "mart", "shop", "market").any { lowerText.contains(it) } -> Category.SHOPPING
            listOf("pharmacy", "health", "cvs", "walgreens", "hospital", "clinic").any { lowerText.contains(it) } -> Category.HEALTH
            else -> Category.OTHER
        }

        val validLines = lines.filter { it.length > 3 && it.any { char -> char.isLetter() } }
        val merchantName = validLines.firstOrNull()
            ?.replace(Regex("[^A-Za-z0-9 ]"), "")
            ?.trim()
            ?.take(20)
            ?: "Unknown Merchant"

        val note = "Receipt: $merchantName"

        return ParsedReceipt(finalAmount, category, note)
    }
}