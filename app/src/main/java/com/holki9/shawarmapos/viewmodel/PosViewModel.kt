package com.holki9.shawarmapos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.holki9.shawarmapos.data.PosRepository
import com.holki9.shawarmapos.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class PosTab {
    POS,
    EXPENSES,
    STOCK,
    HISTORY
}

data class SecurityDialogData(
    val title: String,
    val description: String,
    val requireMath: Boolean,
    val isEdit: Boolean = false,
    val initialAmount: Double = 0.0,
    val initialTitle: String = "",
    val onConfirm: (editedAmount: Double?, editedTitle: String?) -> Unit
)

class PosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PosRepository(application)

    private val _appState = MutableStateFlow(repository.loadState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    private val _activeTab = MutableStateFlow(PosTab.POS)
    val activeTab: StateFlow<PosTab> = _activeTab.asStateFlow()

    private val _draftOrder = MutableStateFlow<Map<String, Int>>(emptyMap())
    val draftOrder: StateFlow<Map<String, Int>> = _draftOrder.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Dialogs state
    val showCalculator = MutableStateFlow(false)
    val showPriceSettings = MutableStateFlow(false)
    val showShiftManager = MutableStateFlow(false)
    val showStockCorrection = MutableStateFlow(false)
    val activeQtyItem = MutableStateFlow<MenuItem?>(null)
    val activeSecurityData = MutableStateFlow<SecurityDialogData?>(null)

    private fun persist(newState: AppState) {
        _appState.value = newState
        repository.saveState(newState)
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _toastMessage.value = msg
            delay(2200)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    fun switchTab(tab: PosTab) {
        _activeTab.value = tab
    }

    // ================= DRAFT ORDER (КАССА) =================

    fun incrementDraftItem(itemId: String, delta: Int) {
        _draftOrder.update { current ->
            val currCount = current[itemId] ?: 0
            val next = (currCount + delta).coerceAtLeast(0)
            val updated = current.toMutableMap()
            if (next <= 0) {
                updated.remove(itemId)
            } else {
                updated[itemId] = next
            }
            updated
        }
    }

    fun setDraftItemQty(itemId: String, qty: Int) {
        _draftOrder.update { current ->
            val updated = current.toMutableMap()
            if (qty <= 0) {
                updated.remove(itemId)
            } else {
                updated[itemId] = qty
            }
            updated
        }
    }

    fun clearDraftOrder() {
        _draftOrder.value = emptyMap()
    }

    fun checkoutDraftOrder() {
        val draft = _draftOrder.value
        if (draft.isEmpty()) return

        val state = _appState.value
        var totalAmount = 0.0
        val itemSummaries = mutableListOf<String>()
        var deltaLavash = 0
        var deltaBunBig = 0
        var deltaBunSmall = 0

        draft.forEach { (id, count) ->
            val item = state.menu.find { it.id == id }
            if (item != null && count > 0) {
                totalAmount += item.price * count
                itemSummaries.add("${item.name} ($count)")
                when (item.stockType) {
                    StockType.LAVASH -> deltaLavash += count
                    StockType.BUN_BIG -> deltaBunBig += count
                    StockType.BUN_SMALL -> deltaBunSmall += count
                    StockType.NONE -> {}
                }
            }
        }

        // Deduct from stock
        var newWhite = state.stock.lavashWhite
        var newOrange = state.stock.lavashOrange
        if (deltaLavash > 0) {
            if (newWhite >= deltaLavash) {
                newWhite -= deltaLavash
            } else {
                val rem = deltaLavash - newWhite
                newWhite = 0
                newOrange = (newOrange - rem).coerceAtLeast(0)
            }
        }
        val newBunBig = (state.stock.bunBig - deltaBunBig).coerceAtLeast(0)
        val newBunSmall = (state.stock.bunSmall - deltaBunSmall).coerceAtLeast(0)

        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newTx = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            type = TransactionType.INCOME,
            title = itemSummaries.joinToString(", "),
            amount = totalAmount,
            time = timeStr,
            stockDelta = StockDelta(deltaLavash, deltaBunBig, deltaBunSmall)
        )

        val updatedState = state.copy(
            stock = state.stock.copy(
                lavashWhite = newWhite,
                lavashOrange = newOrange,
                bunBig = newBunBig,
                bunSmall = newBunSmall
            ),
            transactions = listOf(newTx) + state.transactions
        )

        _draftOrder.value = emptyMap()
        persist(updatedState)
        showToast("Пробито: +${formatMoney(totalAmount)}")
    }

    fun submitQuickSale(amount: Double) {
        if (amount <= 0.0) {
            showToast("Введи сумму больше 0")
            return
        }
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newTx = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            type = TransactionType.INCOME,
            title = "Свободная продажа (Быстрый ввод)",
            amount = amount,
            time = timeStr,
            stockDelta = null
        )
        val state = _appState.value
        persist(state.copy(transactions = listOf(newTx) + state.transactions))
        showToast("В кассу: +${formatMoney(amount)}")
    }

    // ================= РАСХОДЫ =================

    fun selectExpenseCategory(category: String) {
        _appState.update { it.copy(selectedExpenseCategory = category) }
        repository.saveState(_appState.value)
    }

    fun addExpense(amount: Double, note: String, category: String) {
        if (amount <= 0.0) {
            showToast("Укажи сумму расхода")
            return
        }
        val title = if (note.isNotBlank()) "$category: $note" else category
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newTx = Transaction(
            id = "tx_${System.currentTimeMillis()}",
            type = TransactionType.EXPENSE,
            title = title,
            amount = amount,
            time = timeStr
        )
        val state = _appState.value
        persist(state.copy(transactions = listOf(newTx) + state.transactions))
        showToast("Расход записан: -${formatMoney(amount)}")
    }

    // ================= СКЛАД (ЛАВАШ И БУЛОЧКИ) =================

    fun setLavashPackSize(size: Int) {
        _appState.update { it.copy(lavashPackSize = size) }
        repository.saveState(_appState.value)
    }

    fun addLavashArrival(whitePacks: Int, orangePacks: Int, cost: Double) {
        val state = _appState.value
        val packSize = state.lavashPackSize
        val addWhite = whitePacks.coerceAtLeast(0) * packSize
        val addOrange = orangePacks.coerceAtLeast(0) * packSize
        val totalAdd = addWhite + addOrange

        if (totalAdd <= 0 && cost <= 0) {
            showToast("Укажи упаковки или сумму")
            return
        }

        var newTxList = state.transactions
        if (cost > 0) {
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val tx = Transaction(
                id = "tx_${System.currentTimeMillis()}",
                type = TransactionType.EXPENSE,
                title = "Закупка: Лаваш (+$totalAdd шт)",
                amount = cost,
                time = timeStr
            )
            newTxList = listOf(tx) + newTxList
        }

        val updatedStock = state.stock.copy(
            lavashWhite = state.stock.lavashWhite + addWhite,
            lavashOrange = state.stock.lavashOrange + addOrange
        )

        persist(state.copy(stock = updatedStock, transactions = newTxList))
        showToast("Склад: +$totalAdd лавашей")
    }

    fun addBunsArrival(totalCost: Double, bigQty: Int, smallQty: Int) {
        if (totalCost <= 0 && bigQty <= 0 && smallQty <= 0) {
            showToast("Укажи сумму или количество булочек")
            return
        }

        val state = _appState.value
        var newTxList = state.transactions
        if (totalCost > 0) {
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val details = if (bigQty > 0 || smallQty > 0) " (Бол: $bigQty, Мал: $smallQty)" else " (Общий чек)"
            val tx = Transaction(
                id = "tx_${System.currentTimeMillis()}",
                type = TransactionType.EXPENSE,
                title = "Закупка: Булочки для дёнера$details",
                amount = totalCost,
                time = timeStr
            )
            newTxList = listOf(tx) + newTxList
        }

        val updatedStock = state.stock.copy(
            bunBig = state.stock.bunBig + bigQty.coerceAtLeast(0),
            bunSmall = state.stock.bunSmall + smallQty.coerceAtLeast(0)
        )

        persist(state.copy(stock = updatedStock, transactions = newTxList))
        showToast("Булочки оприходованы!")
    }

    fun updateExactStock(white: Int, orange: Int, big: Int, small: Int) {
        val state = _appState.value
        val updatedStock = Stock(
            lavashWhite = white.coerceAtLeast(0),
            lavashOrange = orange.coerceAtLeast(0),
            bunBig = big.coerceAtLeast(0),
            bunSmall = small.coerceAtLeast(0)
        )
        persist(state.copy(stock = updatedStock))
        showToast("Остатки склада скорректированы")
    }

    // ================= НАСТРОЙКА ЦЕН МЕНЮ =================

    fun updatePrice(itemId: String, newPrice: Double) {
        val state = _appState.value
        val updatedMenu = state.menu.map { item ->
            if (item.id == itemId) item.copy(price = newPrice.coerceAtLeast(0.0)) else item
        }
        persist(state.copy(menu = updatedMenu))
    }

    fun updatePrices(pricesMap: Map<String, Double>) {
        val state = _appState.value
        val updatedMenu = state.menu.map { item ->
            val p = pricesMap[item.id]
            if (p != null) item.copy(price = p.coerceAtLeast(0.0)) else item
        }
        persist(state.copy(menu = updatedMenu))
        showToast("Цены меню успешно обновлены!")
    }

    fun resetPricesToDefault() {
        val state = _appState.value
        val defaultMap = DefaultMenu.items.associate { it.id to it.price }
        val updatedMenu = state.menu.map { item ->
            val defPrice = defaultMap[item.id] ?: item.price
            item.copy(price = defPrice)
        }
        persist(state.copy(menu = updatedMenu))
        showToast("Цены сброшены на стандартные")
    }

    // ================= ОПЕРАЦИИ (РЕДАКТИРОВАНИЕ И УДАЛЕНИЕ) =================

    fun deleteTransaction(txId: String) {
        val state = _appState.value
        val tx = state.transactions.find { it.id == txId } ?: return

        // Restore stock if it was sold
        var newStock = state.stock
        tx.stockDelta?.let { delta ->
            newStock = newStock.copy(
                lavashWhite = newStock.lavashWhite + delta.lavash,
                bunBig = newStock.bunBig + delta.bunBig,
                bunSmall = newStock.bunSmall + delta.bunSmall
            )
        }

        val updatedTxList = state.transactions.filter { it.id != txId }
        persist(state.copy(stock = newStock, transactions = updatedTxList))
        showToast("Запись удалена")
    }

    fun editTransaction(txId: String, newAmount: Double, newTitle: String) {
        val state = _appState.value
        val updatedTxList = state.transactions.map { tx ->
            if (tx.id == txId) {
                tx.copy(
                    amount = if (newAmount >= 0) newAmount else tx.amount,
                    title = if (newTitle.isNotBlank()) newTitle else tx.title
                )
            } else tx
        }
        persist(state.copy(transactions = updatedTxList))
        showToast("Запись изменена")
    }

    // ================= УПРАВЛЕНИЕ СМЕНОЙ =================

    fun closeShift() {
        val state = _appState.value
        val nowStr = SimpleDateFormat("d MMM, HH:mm", Locale("ru")).format(Date())
        val closingCash = state.totalCashInRegister

        val archiveEntry = ArchivedShift(
            id = "arch_${System.currentTimeMillis()}",
            date = nowStr,
            income = state.totalIncome,
            expense = state.totalExpense,
            closingCash = closingCash,
            previousCarryover = state.carryoverCash,
            savedTransactions = state.transactions
        )

        val updatedState = state.copy(
            carryoverCash = closingCash,
            transactions = emptyList(),
            archivedShifts = listOf(archiveEntry) + state.archivedShifts
        )
        persist(updatedState)
        showToast("Смена закрыта! Остаток перенесён.")
    }

    fun reopenLastArchivedShift() {
        val state = _appState.value
        if (state.archivedShifts.isEmpty()) return

        val last = state.archivedShifts.first()
        val remainingArchives = state.archivedShifts.drop(1)

        val updatedState = state.copy(
            carryoverCash = last.previousCarryover,
            transactions = last.savedTransactions + state.transactions,
            archivedShifts = remainingArchives
        )
        persist(updatedState)
        showToast("Смена возвращена для редактирования")
    }

    fun factoryReset() {
        repository.clearAll()
        _appState.value = AppState()
        _draftOrder.value = emptyMap()
        showToast("Приложение полностью сброшено")
    }

    fun formatMoney(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            String.format(Locale("ru"), "%,d ₽", amount.toLong()).replace(',', ' ')
        } else {
            String.format(Locale("ru"), "%,.2f ₽", amount).replace(',', ' ')
        }
    }
}
