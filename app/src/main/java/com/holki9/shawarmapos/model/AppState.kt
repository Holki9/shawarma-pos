package com.holki9.shawarmapos.model

data class AppState(
    val carryoverCash: Double = 0.0,
    val shiftOpen: Boolean = true,
    val lavashPackSize: Int = 10,
    val selectedExpenseCategory: String = "Свободный расход",
    val stock: Stock = Stock(),
    val menu: List<MenuItem> = DefaultMenu.items,
    val transactions: List<Transaction> = emptyList(),
    val archivedShifts: List<ArchivedShift> = emptyList()
) {
    val totalIncome: Double get() = transactions
        .filter { it.type == TransactionType.INCOME }
        .sumOf { it.amount }

    val totalExpense: Double get() = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }

    val netProfitToday: Double get() = totalIncome - totalExpense

    val totalCashInRegister: Double get() = carryoverCash + netProfitToday

    val soldLavashCount: Int get() = transactions.sumOf { it.stockDelta?.lavash ?: 0 }
    val soldBigBunsCount: Int get() = transactions.sumOf { it.stockDelta?.bunBig ?: 0 }
    val soldSmallBunsCount: Int get() = transactions.sumOf { it.stockDelta?.bunSmall ?: 0 }
}
