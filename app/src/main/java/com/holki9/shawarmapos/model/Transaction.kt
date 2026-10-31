package com.holki9.shawarmapos.model

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class StockDelta(
    val lavash: Int = 0,
    val bunBig: Int = 0,
    val bunSmall: Int = 0
)

data class Transaction(
    val id: String,
    val type: TransactionType,
    val title: String,
    val amount: Double,
    val time: String,
    val stockDelta: StockDelta? = null
)
