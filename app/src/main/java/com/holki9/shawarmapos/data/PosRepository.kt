package com.holki9.shawarmapos.data

import android.content.Context
import com.holki9.shawarmapos.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class PosRepository(private val context: Context) {

    private val dataFile: File
        get() = File(context.filesDir, "shawarma_pos_state.json")

    fun loadState(): AppState {
        if (!dataFile.exists()) {
            return AppState()
        }
        return try {
            val jsonStr = dataFile.readText()
            val root = JSONObject(jsonStr)

            val carryoverCash = root.optDouble("carryoverCash", 0.0)
            val shiftOpen = root.optBoolean("shiftOpen", true)
            val lavashPackSize = root.optInt("lavashPackSize", 10)
            val selectedExpenseCategory = root.optString("selectedExpenseCategory", "Свободный расход")
            val menuVersion = root.optInt("menuVersion", 1)

            // Stock
            val stockObj = root.optJSONObject("stock")
            val stock = if (stockObj != null) {
                Stock(
                    lavashWhite = stockObj.optInt("lavashWhite", 50),
                    lavashOrange = stockObj.optInt("lavashOrange", 0),
                    bunBig = stockObj.optInt("bunBig", 20),
                    bunSmall = stockObj.optInt("bunSmall", 40)
                )
            } else Stock()

            // Menu (with version migration)
            val menuArr = root.optJSONArray("menu")
            val menuList = mutableListOf<MenuItem>()
            if (menuArr != null && menuArr.length() > 0 && menuVersion >= DefaultMenu.CURRENT_VERSION) {
                for (i in 0 until menuArr.length()) {
                    val mObj = menuArr.getJSONObject(i)
                    val id = mObj.getString("id")
                    val name = mObj.getString("name")
                    val price = mObj.getDouble("price")
                    val groupStr = mObj.optString("group", "MAIN")
                    val group = try { MenuGroup.valueOf(groupStr) } catch (_: Exception) { MenuGroup.MAIN }
                    val stockTypeStr = mObj.optString("stockType", "NONE")
                    val stockType = try { StockType.valueOf(stockTypeStr) } catch (_: Exception) { StockType.NONE }
                    val badge = if (mObj.has("badge") && !mObj.isNull("badge")) mObj.getString("badge") else null

                    menuList.add(MenuItem(id, name, price, group, stockType, badge))
                }
            } else {
                menuList.addAll(DefaultMenu.items)
            }

            // Transactions
            val txArr = root.optJSONArray("transactions")
            val txList = mutableListOf<Transaction>()
            if (txArr != null) {
                for (i in 0 until txArr.length()) {
                    val tObj = txArr.getJSONObject(i)
                    val id = tObj.getString("id")
                    val type = TransactionType.valueOf(tObj.optString("type", "INCOME"))
                    val title = tObj.getString("title")
                    val amount = tObj.getDouble("amount")
                    val time = tObj.getString("time")

                    val deltaObj = tObj.optJSONObject("stockDelta")
                    val delta = if (deltaObj != null) {
                        StockDelta(
                            lavash = deltaObj.optInt("lavash", 0),
                            bunBig = deltaObj.optInt("bunBig", 0),
                            bunSmall = deltaObj.optInt("bunSmall", 0)
                        )
                    } else null

                    txList.add(Transaction(id, type, title, amount, time, delta))
                }
            }

            // Archived Shifts
            val archArr = root.optJSONArray("archivedShifts")
            val archList = mutableListOf<ArchivedShift>()
            if (archArr != null) {
                for (i in 0 until archArr.length()) {
                    val aObj = archArr.getJSONObject(i)
                    val id = aObj.getString("id")
                    val date = aObj.getString("date")
                    val income = aObj.getDouble("income")
                    val expense = aObj.getDouble("expense")
                    val closingCash = aObj.getDouble("closingCash")
                    val prevCarryover = aObj.optDouble("previousCarryover", 0.0)

                    val savedTxArr = aObj.optJSONArray("savedTransactions")
                    val savedTx = mutableListOf<Transaction>()
                    if (savedTxArr != null) {
                        for (j in 0 until savedTxArr.length()) {
                            val stObj = savedTxArr.getJSONObject(j)
                            savedTx.add(
                                Transaction(
                                    id = stObj.getString("id"),
                                    type = TransactionType.valueOf(stObj.optString("type", "INCOME")),
                                    title = stObj.getString("title"),
                                    amount = stObj.getDouble("amount"),
                                    time = stObj.getString("time")
                                )
                            )
                        }
                    }
                    archList.add(ArchivedShift(id, date, income, expense, closingCash, prevCarryover, savedTx))
                }
            }

            AppState(
                carryoverCash = carryoverCash,
                shiftOpen = shiftOpen,
                lavashPackSize = lavashPackSize,
                selectedExpenseCategory = selectedExpenseCategory,
                stock = stock,
                menu = menuList,
                transactions = txList,
                archivedShifts = archList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            AppState()
        }
    }

    fun saveState(state: AppState) {
        try {
            val root = JSONObject()
            root.put("carryoverCash", state.carryoverCash)
            root.put("shiftOpen", state.shiftOpen)
            root.put("lavashPackSize", state.lavashPackSize)
            root.put("selectedExpenseCategory", state.selectedExpenseCategory)
            root.put("menuVersion", DefaultMenu.CURRENT_VERSION)

            // Stock
            val stockObj = JSONObject().apply {
                put("lavashWhite", state.stock.lavashWhite)
                put("lavashOrange", state.stock.lavashOrange)
                put("bunBig", state.stock.bunBig)
                put("bunSmall", state.stock.bunSmall)
            }
            root.put("stock", stockObj)

            // Menu
            val menuArr = JSONArray()
            state.menu.forEach { item ->
                val mObj = JSONObject().apply {
                    put("id", item.id)
                    put("name", item.name)
                    put("price", item.price)
                    put("group", item.group.name)
                    put("stockType", item.stockType.name)
                    put("badge", item.badge)
                }
                menuArr.put(mObj)
            }
            root.put("menu", menuArr)

            // Transactions
            val txArr = JSONArray()
            state.transactions.forEach { tx ->
                val tObj = JSONObject().apply {
                    put("id", tx.id)
                    put("type", tx.type.name)
                    put("title", tx.title)
                    put("amount", tx.amount)
                    put("time", tx.time)
                    tx.stockDelta?.let { delta ->
                        put("stockDelta", JSONObject().apply {
                            put("lavash", delta.lavash)
                            put("bunBig", delta.bunBig)
                            put("bunSmall", delta.bunSmall)
                        })
                    }
                }
                txArr.put(tObj)
            }
            root.put("transactions", txArr)

            // Archived shifts
            val archArr = JSONArray()
            state.archivedShifts.forEach { shift ->
                val sObj = JSONObject().apply {
                    put("id", shift.id)
                    put("date", shift.date)
                    put("income", shift.income)
                    put("expense", shift.expense)
                    put("closingCash", shift.closingCash)
                    put("previousCarryover", shift.previousCarryover)
                    val sTxArr = JSONArray()
                    shift.savedTransactions.forEach { st ->
                        sTxArr.put(JSONObject().apply {
                            put("id", st.id)
                            put("type", st.type.name)
                            put("title", st.title)
                            put("amount", st.amount)
                            put("time", st.time)
                        })
                    }
                    put("savedTransactions", sTxArr)
                }
                archArr.put(sObj)
            }
            root.put("archivedShifts", archArr)

            // Atomic write to avoid partial writes
            val tempFile = File(context.filesDir, "shawarma_pos_state.tmp")
            tempFile.writeText(root.toString(2))
            tempFile.renameTo(dataFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearAll() {
        if (dataFile.exists()) {
            dataFile.delete()
        }
    }
}
