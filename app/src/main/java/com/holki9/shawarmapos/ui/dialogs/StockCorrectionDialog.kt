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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.holki9.shawarmapos.model.Stock
import com.holki9.shawarmapos.ui.components.GoldButton3D
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun StockCorrectionDialog(
    currentStock: Stock,
    onSaveStock: (white: Int, orange: Int, big: Int, small: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var whiteText by remember { mutableStateOf(currentStock.lavashWhite.toString()) }
    var orangeText by remember { mutableStateOf(currentStock.lavashOrange.toString()) }
    var bigText by remember { mutableStateOf(currentStock.bunBig.toString()) }
    var smallText by remember { mutableStateOf(currentStock.bunSmall.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(310.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF80A0F1E))
                .border(1.5.dp, GoldNeon.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Точная коррекция остатков",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhite
                )

                StockField(label = "Лаваш Белый (шт):", value = whiteText, onValueChange = { whiteText = it })
                StockField(label = "Лаваш Оранжевый (шт):", value = orangeText, onValueChange = { orangeText = it })
                StockField(label = "Булочки Большие (шт):", value = bigText, onValueChange = { bigText = it })
                StockField(label = "Булочки Маленькие (шт):", value = smallText, onValueChange = { smallText = it })

                Spacer(modifier = Modifier.height(4.dp))

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
                        text = "Сохранить",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val w = whiteText.toIntOrNull() ?: 0
                            val o = orangeText.toIntOrNull() ?: 0
                            val b = bigText.toIntOrNull() ?: 0
                            val s = smallText.toIntOrNull() ?: 0
                            onSaveStock(w, o, b, s)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StockField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(text = label, fontSize = 11.sp, color = TextDarkMuted, modifier = Modifier.padding(bottom = 2.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldNeon,
                unfocusedBorderColor = Color(0x3094A3B8),
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
