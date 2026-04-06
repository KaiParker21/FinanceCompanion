package com.skye.financecompanion.domain.util

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

        // Regex to find standard price formats (e.g., 12.99, 1,234.56)
        // We drop the $ from the regex capture group to be safer with OCR
        val priceRegex = Regex("""([0-9]{1,3}(?:,[0-9]{3})*\.[0-9]{2})""")

        var finalAmount: Double? = null

        // 1. INTELLIGENT LINE-BY-LINE SCAN
        // Loop through lines looking for the true "Total"
        for ((index, line) in lines.withIndex()) {
            val lowerLine = line.lowercase()

            // Look for "total" but EXPLICITLY ignore "subtotal" and "tax"
            if (lowerLine.contains("total") && !lowerLine.contains("sub") && !lowerLine.contains("tax")) {

                // A. Check if the price is on the exact same line (e.g., "TOTAL: $51.30")
                var match = priceRegex.find(lowerLine)
                if (match != null) {
                    finalAmount = match.groupValues[1].replace(",", "").toDoubleOrNull()
                    // If we found it, stop looking!
                    if (finalAmount != null) break
                }

                // B. Fallback: Sometimes OCR puts the price on the VERY NEXT line
                if (index + 1 < lines.size) {
                    match = priceRegex.find(lines[index + 1])
                    if (match != null) {
                        finalAmount = match.groupValues[1].replace(",", "").toDoubleOrNull()
                        if (finalAmount != null) break
                    }
                }
            }
        }

        // 2. THE FAILSAFE (Max Value)
        // If the word "Total" was smudged or completely missed by the camera,
        // just find the absolute largest price on the entire receipt.
        if (finalAmount == null) {
            val allPrices = priceRegex.findAll(rawText).mapNotNull {
                it.groupValues[1].replace(",", "").toDoubleOrNull()
            }.toList()

            finalAmount = allPrices.maxOrNull()
        }

        // 3. Category Heuristics
        val category = when {
            listOf("restaurant", "cafe", "grill", "tip", "server", "table", "dine", "eat", "food").any { lowerText.contains(it) } -> Category.FOOD
            listOf("uber", "lyft", "taxi", "transit", "train", "flight").any { lowerText.contains(it) } -> Category.TRANSPORT
            listOf("walmart", "target", "store", "mart", "shop", "market").any { lowerText.contains(it) } -> Category.SHOPPING
            listOf("pharmacy", "health", "cvs", "walgreens", "hospital", "clinic").any { lowerText.contains(it) } -> Category.HEALTH
            else -> Category.OTHER
        }

        // 4. Note Generation (Merchant Name)
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