package com.skye.financecompanion.data.mapper

import com.skye.financecompanion.data.local.entity.TransactionEntity
import com.skye.financecompanion.domain.model.Transaction
import java.time.Instant
import java.time.ZoneId

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        type = type,
        category = category,
        date = Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate(),
        note = note,
        isEssential = isEssential
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type,
        category = category,
        dateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        note = note,
        isEssential = isEssential
    )
}