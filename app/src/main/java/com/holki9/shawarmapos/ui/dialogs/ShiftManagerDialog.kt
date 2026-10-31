package com.holki9.shawarmapos.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CurrencyRuble
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.holki9.shawarmapos.model.AppState
import com.holki9.shawarmapos.ui.components.GoldButton3D
import com.holki9.shawarmapos.ui.theme.*
import java.util.Locale

@Composable
fun ShiftManagerDialog(
    state: AppState,
    onCloseShift: () -> Unit,
    onReopenLastShift: () -> Unit,
    onFactoryReset: () -> Unit,
    onOpenPriceSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF80A0F1E))
                .border(1.5.dp, PurpleNeon.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Управление сменой",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Закрыть",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Status info box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF04060F))
                        .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Статус:", fontSize = 12.sp, color = TextDarkMuted)
                            Text(
                                text = if (state.shiftOpen) "Открыта" else "Закрыта",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (state.shiftOpen) EmeraldNeon else RoseNeon
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Денег в кассе (перейдёт):", fontSize = 12.sp, color = TextDarkMuted)
                            Text(
                                text = formatMoney(state.totalCashInRegister),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Остаток лаваша:", fontSize = 12.sp, color = TextDarkMuted)
                            Text(
                                text = "${state.stock.totalLavash} шт",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldNeon
                            )
                        }
                    }
                }

                // Close shift action
                GoldButton3D(
                    text = "Закрыть смену (Перенести остаток)",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDismiss()
                        onCloseShift()
                    }
                )

                // Reopen last shift action (if available)
                if (state.archivedShifts.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, SkyNeon.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .clickable {
                                onDismiss()
                                onReopenLastShift()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Вернуть (переоткрыть) закрытую смену",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkyNeon
                        )
                    }
                }

                // Price Settings shortcut
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF11192C))
                        .border(1.dp, GoldNeon.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .clickable {
                        onDismiss()
                        onOpenPriceSettings()
                    }
                    .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.CurrencyRuble,
                            contentDescription = null,
                            tint = GoldNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Изменить цены на меню (Шаурма/Дёнеры)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldNeon
                        )
                    }
                }

                // Full Reset Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(RoseDeep.copy(alpha = 0.18f))
                        .border(1.dp, RoseNeon.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable {
                            onDismiss()
                            onFactoryReset()
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = RoseNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Полный сброс приложения (С нуля)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoseNeon
                        )
                    }
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
