package com.holki9.shawarmapos.model

data class ArchivedShift(
    val id: String,
    val date: String,
    val income: Double,
    val expense: Double,
    val closingCash: Double,
    val previousCarryover: Double,
    val savedTransactions: List<Transaction>
)
