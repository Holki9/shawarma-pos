package com.holki9.shawarmapos.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
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
import androidx.compose.ui.window.DialogProperties
import com.holki9.shawarmapos.model.MenuGroup
import com.holki9.shawarmapos.model.MenuItem
import com.holki9.shawarmapos.ui.components.GoldButton3D
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun PriceSettingsDialog(
    menu: List<MenuItem>,
    onSavePrices: (Map<String, Double>) -> Unit,
    onResetDefaults: () -> Unit,
    onDismiss: () -> Unit
) {
    // Local editable map of id to price
    val editedPrices = remember {
        mutableStateMapOf<String, String>().apply {
            menu.forEach { put(it.id, it.price.toInt().toString()) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF80A0F1E))
                .border(1.5.dp, GoldNeon.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Настройка цен меню",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                        Text(
                            text = "Повышай или понижай цены в любой момент",
                            fontSize = 11.sp,
                            color = TextDarkMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Закрыть",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable items grouped by category
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryHeader("ОСНОВНЫЕ БЛЮДА (ШАУРМА И ДЁНЕРЫ)")
                    }
                    items(menu.filter { it.group == MenuGroup.MAIN }, key = { it.id }) { item ->
                        PriceEditRow(
                            item = item,
                            currentVal = editedPrices[item.id] ?: "${item.price.toInt()}",
                            onValueChange = { editedPrices[item.id] = it },
                            onDelta = { delta ->
                                val cur = (editedPrices[item.id]?.toIntOrNull() ?: item.price.toInt()) + delta
                                editedPrices[item.id] = cur.coerceAtLeast(0).toString()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        CategoryHeader("ДОБАВКИ К ЗАКАЗУ")
                    }
                    items(menu.filter { it.group == MenuGroup.ADDON }, key = { it.id }) { item ->
                        PriceEditRow(
                            item = item,
                            currentVal = editedPrices[item.id] ?: "${item.price.toInt()}",
                            onValueChange = { editedPrices[item.id] = it },
                            onDelta = { delta ->
                                val cur = (editedPrices[item.id]?.toIntOrNull() ?: item.price.toInt()) + delta
                                editedPrices[item.id] = cur.coerceAtLeast(0).toString()
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        CategoryHeader("ЗАКУСКИ И НАПИТКИ")
                    }
                    items(menu.filter { it.group == MenuGroup.SNACK }, key = { it.id }) { item ->
                        PriceEditRow(
                            item = item,
                            currentVal = editedPrices[item.id] ?: "${item.price.toInt()}",
                            onValueChange = { editedPrices[item.id] = it },
                            onDelta = { delta ->
                                val cur = (editedPrices[item.id]?.toIntOrNull() ?: item.price.toInt()) + delta
                                editedPrices[item.id] = cur.coerceAtLeast(0).toString()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom actions: Save and Reset
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(14.dp))
                            .clickable {
                                onResetDefaults()
                                onDismiss()
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.RestartAlt,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Заводские",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    }

                    GoldButton3D(
                        text = "Сохранить новые цены",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val resultMap = editedPrices.mapValues { (_, v) ->
                                v.toDoubleOrNull() ?: 0.0
                            }
                            onSavePrices(resultMap)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = GoldNeon,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
    )
}

@Composable
private fun PriceEditRow(
    item: MenuItem,
    currentVal: String,
    onValueChange: (String) -> Unit,
    onDelta: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xD011192C))
            .border(1.dp, Color(0x2094A3B8), RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // -10 quick button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onDelta(-10) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("-10", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }

                // Numeric input field
                OutlinedTextField(
                    value = currentVal,
                    onValueChange = onValueChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldNeon,
                        unfocusedBorderColor = Color(0x4094A3B8),
                        focusedTextColor = GoldNeon,
                        unfocusedTextColor = GoldNeon,
                        focusedContainerColor = Color(0xFF070B16),
                        unfocusedContainerColor = Color(0xFF070B16)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.width(72.dp)
                )

                // +10 quick button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { onDelta(10) },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+10", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldNeon)
                }
            }
        }
    }
}
