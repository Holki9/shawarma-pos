package com.holki9.shawarmapos.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.holki9.shawarmapos.model.MenuItem
import com.holki9.shawarmapos.ui.components.GoldButton3D
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun QuantityDialog(
    item: MenuItem,
    currentQty: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var textQty by remember { mutableStateOf(if (currentQty > 0) currentQty.toString() else "1") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF50A0F1E))
                .border(1.5.dp, GoldNeon.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                    Text(
                        text = "Укажи количество штук (${item.price.toInt()} ₽/шт):",
                        fontSize = 11.sp,
                        color = TextDarkMuted
                    )
                }

                OutlinedTextField(
                    value = textQty,
                    onValueChange = { textQty = it },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldNeon
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldNeon,
                        unfocusedBorderColor = Color(0x4094A3B8),
                        focusedContainerColor = Color(0xFF04060F),
                        unfocusedContainerColor = Color(0xFF04060F)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Chips: 2, 4, 6, 10
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(2, 4, 6, 10).forEach { chipVal ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .clickable { textQty = chipVal.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$chipVal шт",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E293B))
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Отмена",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }

                    GoldButton3D(
                        text = "Применить",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val qty = textQty.toIntOrNull() ?: 0
                            onConfirm(qty)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}
