package com.holki9.shawarmapos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.ui.components.BottomNavBar
import com.holki9.shawarmapos.ui.components.FloatingLiveOrderBar
import com.holki9.shawarmapos.ui.components.HeaderView
import com.holki9.shawarmapos.ui.dialogs.*
import com.holki9.shawarmapos.ui.screens.ExpensesScreen
import com.holki9.shawarmapos.ui.screens.HistoryScreen
import com.holki9.shawarmapos.ui.screens.PosScreen
import com.holki9.shawarmapos.ui.screens.StockScreen
import com.holki9.shawarmapos.ui.theme.*
import com.holki9.shawarmapos.viewmodel.PosTab
import com.holki9.shawarmapos.viewmodel.PosViewModel
import com.holki9.shawarmapos.viewmodel.SecurityDialogData

class MainActivity : ComponentActivity() {

    private val viewModel: PosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ShawarmaPOSTheme {
                val state by viewModel.appState.collectAsState()
                val activeTab by viewModel.activeTab.collectAsState()
                val draftOrder by viewModel.draftOrder.collectAsState()
                val toastMsg by viewModel.toastMessage.collectAsState()

                // Dialog states
                val showCalc by viewModel.showCalculator.collectAsState()
                val showPriceSettings by viewModel.showPriceSettings.collectAsState()
                val showShiftManager by viewModel.showShiftManager.collectAsState()
                val showStockCorrection by viewModel.showStockCorrection.collectAsState()
                val activeQtyItem by viewModel.activeQtyItem.collectAsState()
                val activeSecurityData by viewModel.activeSecurityData.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(BgGradientStart, BgGradientMid, BgGradientEnd)
                            )
                        )
                ) {
                    // Futuristic ambient neon rays in background
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Top-left diagonal neon beam
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(Color.Transparent, EmeraldNeon.copy(alpha = 0.12f), Color.Transparent),
                                start = Offset(w * 0.2f, 0f),
                                end = Offset(w * 0.6f, h * 0.35f)
                            ),
                            start = Offset(w * 0.2f, 0f),
                            end = Offset(w * 0.6f, h * 0.35f),
                            strokeWidth = 2f
                        )

                        // Bottom-right diagonal beam
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(Color.Transparent, TealNeon.copy(alpha = 0.18f), Color.Transparent),
                                start = Offset(w * 0.7f, h * 0.65f),
                                end = Offset(w, h * 0.95f)
                            ),
                            start = Offset(w * 0.7f, h * 0.65f),
                            end = Offset(w, h * 0.95f),
                            strokeWidth = 2.5f
                        )
                    }

                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            HeaderView(
                                state = state,
                                onOpenCalculator = { viewModel.showCalculator.value = true },
                                onOpenShiftManager = { viewModel.showShiftManager.value = true },
                                onOpenPriceSettings = { viewModel.showPriceSettings.value = true }
                            )
                        },
                        bottomBar = {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Floating order bar
                                FloatingLiveOrderBar(
                                    draftOrder = draftOrder,
                                    menu = state.menu,
                                    onClearOrder = {
                                        viewModel.activeSecurityData.value = SecurityDialogData(
                                            title = "Сбросить набранный заказ?",
                                            description = "Ты нажал очистку текущего чека. Чтобы случайно не стереть набранные позиции, подтверди сброс.",
                                            requireMath = false,
                                            onConfirm = { _, _ -> viewModel.clearDraftOrder() }
                                        )
                                    },
                                    onCheckout = { viewModel.checkoutDraftOrder() }
                                )

                                // Pinned bottom nav dock
                                BottomNavBar(
                                    activeTab = activeTab,
                                    onTabSelected = { viewModel.switchTab(it) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = innerPadding.calculateTopPadding())
                        ) {
                            when (activeTab) {
                                PosTab.POS -> PosScreen(
                                    menu = state.menu,
                                    draftOrder = draftOrder,
                                    onIncrement = { id, delta -> viewModel.incrementDraftItem(id, delta) },
                                    onOpenQtyDialog = { item -> viewModel.activeQtyItem.value = item },
                                    onQuickSale = { amount -> viewModel.submitQuickSale(amount) }
                                )
                                PosTab.EXPENSES -> ExpensesScreen(
                                    transactions = state.transactions,
                                    selectedCategory = state.selectedExpenseCategory,
                                    onCategorySelected = { viewModel.selectExpenseCategory(it) },
                                    onAddExpense = { amt, note, cat -> viewModel.addExpense(amt, note, cat) },
                                    onOpenCalculator = { viewModel.showCalculator.value = true },
                                    onSwitchTab = { viewModel.switchTab(it) },
                                    onRequestEdit = { tx ->
                                        viewModel.activeSecurityData.value = SecurityDialogData(
                                            title = "Изменение записи расхода",
                                            description = "Укажи исправленную сумму или описание расхода и реши пример:",
                                            requireMath = true,
                                            isEdit = true,
                                            initialAmount = tx.amount,
                                            initialTitle = tx.title,
                                            onConfirm = { newAmt, newTitle ->
                                                viewModel.editTransaction(tx.id, newAmt ?: tx.amount, newTitle ?: tx.title)
                                            }
                                        )
                                    },
                                    onRequestDelete = { tx ->
                                        viewModel.activeSecurityData.value = SecurityDialogData(
                                            title = "Удаление расхода",
                                            description = "Удалить запись «${tx.title}» на сумму ${viewModel.formatMoney(tx.amount)}? Защита от случайного нажатия:",
                                            requireMath = true,
                                            onConfirm = { _, _ -> viewModel.deleteTransaction(tx.id) }
                                        )
                                    }
                                )
                                PosTab.STOCK -> StockScreen(
                                    stock = state.stock,
                                    packSize = state.lavashPackSize,
                                    onSetPackSize = { viewModel.setLavashPackSize(it) },
                                    onAddLavash = { w, o, cost -> viewModel.addLavashArrival(w, o, cost) },
                                    onAddBuns = { cost, b, s -> viewModel.addBunsArrival(cost, b, s) },
                                    onOpenStockCorrection = { viewModel.showStockCorrection.value = true }
                                )
                                PosTab.HISTORY -> HistoryScreen(
                                    state = state,
                                    onOpenShiftManager = { viewModel.showShiftManager.value = true },
                                    onRequestEdit = { tx ->
                                        viewModel.activeSecurityData.value = SecurityDialogData(
                                            title = "Изменение сохранённой записи",
                                            description = "Укажи исправленную сумму или описание:",
                                            requireMath = true,
                                            isEdit = true,
                                            initialAmount = tx.amount,
                                            initialTitle = tx.title,
                                            onConfirm = { newAmt, newTitle ->
                                                viewModel.editTransaction(tx.id, newAmt ?: tx.amount, newTitle ?: tx.title)
                                            }
                                        )
                                    },
                                    onRequestDelete = { tx ->
                                        viewModel.activeSecurityData.value = SecurityDialogData(
                                            title = "Удаление записи из базы",
                                            description = "Удалить «${tx.title}» на ${viewModel.formatMoney(tx.amount)}? Если это была продажа, списанные лаваш или булочки вернутся на склад.",
                                            requireMath = true,
                                            onConfirm = { _, _ -> viewModel.deleteTransaction(tx.id) }
                                        )
                                    },
                                    onReopenLastShift = {
                                        viewModel.activeSecurityData.value = SecurityDialogData(
                                            title = "Вернуть закрытую смену?",
                                            description = "Все операции прошлой смены вернутся на экран для продолжения работы или исправлений.",
                                            requireMath = false,
                                            onConfirm = { _, _ -> viewModel.reopenLastArchivedShift() }
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Floating Toast Notification
                    AnimatedVisibility(
                        visible = toastMsg != null,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 70.dp)
                    ) {
                        toastMsg?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .shadow(16.dp, shape = RoundedCornerShape(20.dp))
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xF50D152A))
                                    .border(1.5.dp, EmeraldNeon.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldNeon)
                                    )
                                    Text(
                                        text = msg,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }

                    // Dialog Overlays
                    if (showPriceSettings) {
                        PriceSettingsDialog(
                            menu = state.menu,
                            onSavePrices = { viewModel.updatePrices(it) },
                            onAddProduct = { name, price, group, stockType, badge ->
                                viewModel.addProduct(name, price, group, stockType, badge)
                            },
                            onDeleteProduct = { viewModel.deleteProduct(it) },
                            onResetDefaults = { viewModel.resetPricesToDefault() },
                            onDismiss = { viewModel.showPriceSettings.value = false }
                        )
                    }

                    if (showCalc) {
                        CalculatorDialog(
                            onApplyIncome = { viewModel.submitQuickSale(it) },
                            onApplyExpense = { viewModel.addExpense(it, "Посчитано на калькуляторе", "Свободный расход") },
                            onDismiss = { viewModel.showCalculator.value = false }
                        )
                    }

                    activeQtyItem?.let { item ->
                        QuantityDialog(
                            item = item,
                            currentQty = draftOrder[item.id] ?: 0,
                            onConfirm = { qty -> viewModel.setDraftItemQty(item.id, qty) },
                            onDismiss = { viewModel.activeQtyItem.value = null }
                        )
                    }

                    if (showShiftManager) {
                        ShiftManagerDialog(
                            state = state,
                            onCloseShift = {
                                viewModel.activeSecurityData.value = SecurityDialogData(
                                    title = "Закрытие смены и перенос остатка",
                                    description = "Текущий остаток кассы (${viewModel.formatMoney(state.totalCashInRegister)}) и все остатки склада перейдут на новый день. Подтверди действие:",
                                    requireMath = true,
                                    onConfirm = { _, _ -> viewModel.closeShift() }
                                )
                            },
                            onReopenLastShift = { viewModel.reopenLastArchivedShift() },
                            onFactoryReset = {
                                viewModel.activeSecurityData.value = SecurityDialogData(
                                    title = "ПОЛНЫЙ СБРОС ПРИЛОЖЕНИЯ",
                                    description = "ВНИМАНИЕ! Все записи смен, архивы и остатки будут полностью очищены, как будто приложение только установили. Реши пример:",
                                    requireMath = true,
                                    onConfirm = { _, _ -> viewModel.factoryReset() }
                                )
                            },
                            onOpenPriceSettings = { viewModel.showPriceSettings.value = true },
                            onDismiss = { viewModel.showShiftManager.value = false }
                        )
                    }

                    if (showStockCorrection) {
                        StockCorrectionDialog(
                            currentStock = state.stock,
                            onSaveStock = { w, o, b, s -> viewModel.updateExactStock(w, o, b, s) },
                            onDismiss = { viewModel.showStockCorrection.value = false }
                        )
                    }

                    activeSecurityData?.let { secData ->
                        SecurityConfirmationDialog(
                            data = secData,
                            onDismiss = { viewModel.activeSecurityData.value = null }
                        )
                    }
                }
            }
        }
    }
}
