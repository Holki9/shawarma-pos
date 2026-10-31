package com.holki9.shawarmapos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.model.Transaction
import com.holki9.shawarmapos.model.TransactionType
import com.holki9.shawarmapos.ui.components.Card3D
import com.holki9.shawarmapos.ui.components.RoseButton3D
import com.holki9.shawarmapos.ui.theme.*
import com.holki9.shawarmapos.viewmodel.PosTab
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpensesScreen(
    transactions: List<Transaction>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAddExpense: (Double, String, String) -> Unit,
    onOpenCalculator: () -> Unit,
    onSwitchTab: (PosTab) -> Unit,
    onRequestEdit: (Transaction) -> Unit,
    onRequestDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    val categories = listOf(
        "Свободный расход",
        "Продукты / Заготовки",
        "Зарплата (ЗП)",
        "ЖКХ / Свет / Аренда"
    )

    val expenseTransactions = remember(transactions) {
        transactions.filter { it.type == TransactionType.EXPENSE }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp)
    ) {
        // Free Expense Input Card
        item {
            Card3D(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.RemoveCircleOutline,
                            contentDescription = null,
                            tint = RoseNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "СВОБОДНЫЙ ВВОД РАСХОДА",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoseNeon,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Amount input + calculator button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { amountText = it },
                            placeholder = { Text("Сумма расхода (₽)", fontSize = 13.sp, color = TextMuted) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoseNeon,
                                unfocusedBorderColor = Color(0x4094A3B8),
                                focusedContainerColor = Color(0xD00A0F1E),
                                unfocusedContainerColor = Color(0xD00A0F1E),
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0x4094A3B8), RoundedCornerShape(14.dp))
                            .clickable(onClick = onOpenCalculator),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Calculate,
                                contentDescription = "Калькулятор",
                                tint = GoldNeon,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Note input
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("Комментарий (напр: Докупил овощи)", fontSize = 12.sp, color = TextDarkMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoseNeon,
                            unfocusedBorderColor = Color(0x3094A3B8),
                            focusedContainerColor = Color(0xB00A0F1E),
                            unfocusedContainerColor = Color(0xB00A0F1E),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) RoseNeon.copy(alpha = 0.20f) else Color(0xFF1E293B))
                                    .border(
                                        1.dp,
                                        if (isSel) RoseNeon.copy(alpha = 0.6f) else Color(0x3094A3B8),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onCategorySelected(cat) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (cat == "Свободный расход") "Без категории" else cat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) RoseNeon else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    RoseButton3D(
                        text = "Записать расход",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            if (amount > 0) {
                                onAddExpense(amount, noteText.trim(), selectedCategory)
                                amountText = ""
                                noteText = ""
                            }
                        }
                    )
                }
            }
        }

        // Supply quick links card
        item {
            Card3D(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ЗАКУПКА: ЛАВАШ И БУЛОЧКИ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldNeon
                        )
                        Text(
                            text = "На склад →",
                            fontSize = 11.sp,
                            color = GoldNeon,
                            modifier = Modifier.clickable { onSwitchTab(PosTab.STOCK) }
                        )
                    }

                    Text(
                        text = "Оформи закупку лаваша упаковками или булочек для дёнера с автоматическим пополнением склада.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(14.dp))
                                .clickable { onSwitchTab(PosTab.STOCK) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Приход Лаваша", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("Упаковки по 10/5 шт", fontSize = 10.sp, color = TextDarkMuted)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(14.dp))
                                .clickable { onSwitchTab(PosTab.STOCK) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Приход Булочек", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("Общий чек или поштучно", fontSize = 10.sp, color = TextDarkMuted)
                            }
                        }
                    }
                }
            }
        }

        // Today's expenses list
        item {
            Text(
                text = "РАСХОДЫ ЗА ТЕКУЩУЮ СМЕНУ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (expenseTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Сегодня расходов пока не было",
                        fontSize = 12.sp,
                        color = TextDarkMuted
                    )
                }
            }
        } else {
            items(expenseTransactions, key = { it.id }) { tx ->
                ExpenseRow(
                    tx = tx,
                    onEdit = { onRequestEdit(tx) },
                    onDelete = { onRequestDelete(tx) }
                )
            }
        }
    }
}

@Composable
private fun ExpenseRow(
    tx: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xF00E172A))
            .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tx.time,
                    fontSize = 10.sp,
                    color = TextDarkMuted
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "-${formatMoney(tx.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = RoseNeon
                )

                // Edit Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable(onClick = onEdit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Изменить",
                        tint = GoldNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Delete Button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Удалить",
                        tint = RoseNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatMoney(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        String.format(Locale("ru"), "%,d ₽", amount.toLong()).replace(',', ' ')
    } else {
        String.format(Locale("ru"), "%,.0f ₽", amount).replace(',', ' ')
    }
}
