package com.holki9.shawarmapos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.model.AppState
import com.holki9.shawarmapos.model.Transaction
import com.holki9.shawarmapos.model.TransactionType
import com.holki9.shawarmapos.ui.components.Card3D
import com.holki9.shawarmapos.ui.theme.*
import java.util.Locale

@Composable
fun HistoryScreen(
    state: AppState,
    onOpenShiftManager: () -> Unit,
    onRequestEdit: (Transaction) -> Unit,
    onRequestDelete: (Transaction) -> Unit,
    onReopenLastShift: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp)
    ) {
        // Shift Financial Report Card
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ФИНАНСОВЫЙ ОТЧЁТ СМЕНЫ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextMuted,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Закрыть / Архив",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldNeon,
                            modifier = Modifier.clickable(onClick = onOpenShiftManager)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Остаток с прошлой смены:", fontSize = 10.sp, color = TextDarkMuted)
                                Text(
                                    text = formatMoney(state.carryoverCash),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Чистая прибыль сегодня:", fontSize = 10.sp, color = TextDarkMuted)
                                val net = state.netProfitToday
                                Text(
                                    text = (if (net >= 0) "+" else "") + formatMoney(net),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (net >= 0) EmeraldNeon else RoseNeon
                                )
                            }
                        }
                    }

                    // Product Sales stats
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x700F172A))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Продано сегодня: Лавашей — ${state.soldLavashCount} шт, Дёнер Бол. — ${state.soldBigBunsCount} шт, Дёнер Мал. — ${state.soldSmallBunsCount} шт",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Today's All Transactions Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ВСЕ ОПЕРАЦИИ ЗА СЕГОДНЯ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Защита двойным подтверждением",
                    fontSize = 10.sp,
                    color = TextDarkMuted
                )
            }
        }

        if (state.transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "За эту смену пока нет записей",
                        fontSize = 12.sp,
                        color = TextDarkMuted
                    )
                }
            }
        } else {
            items(state.transactions, key = { it.id }) { tx ->
                TransactionRow(
                    tx = tx,
                    onEdit = { onRequestEdit(tx) },
                    onDelete = { onRequestDelete(tx) }
                )
            }
        }

        // Archived Shifts Section
        item {
            Text(
                text = "АРХИВ ЗАКРЫТЫХ СМЕН",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
        }

        if (state.archivedShifts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Закрытых смен в архиве пока нет",
                        fontSize = 12.sp,
                        color = TextDarkMuted
                    )
                }
            }
        } else {
            items(state.archivedShifts, key = { it.id }) { arch ->
                val isFirst = arch == state.archivedShifts.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xD00A0F1E))
                        .border(1.dp, Color(0x2594A3B8), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = arch.date,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Выручка: +${formatMoney(arch.income)} | Расход: -${formatMoney(arch.expense)}",
                                fontSize = 10.sp,
                                color = TextDarkMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Итог: ${formatMoney(arch.closingCash)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldNeon
                            )
                            if (isFirst) {
                                Text(
                                    text = "Вернуть и исправить",
                                    fontSize = 10.sp,
                                    color = SkyNeon,
                                    modifier = Modifier.clickable(onClick = onReopenLastShift)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    tx: Transaction,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isInc = tx.type == TransactionType.INCOME

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xF00E172A))
            .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isInc) EmeraldNeon.copy(alpha = 0.15f) else RoseNeon.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isInc) "+" else "−",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isInc) EmeraldNeon else RoseNeon
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.title,
                        fontSize = 12.sp,
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
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "${if (isInc) "+" else "-"}${formatMoney(tx.amount)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isInc) EmeraldNeon else RoseNeon
                )

                // Edit Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable(onClick = onEdit),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Изменить",
                        tint = GoldNeon,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Delete Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Удалить",
                        tint = RoseNeon,
                        modifier = Modifier.size(15.dp)
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
