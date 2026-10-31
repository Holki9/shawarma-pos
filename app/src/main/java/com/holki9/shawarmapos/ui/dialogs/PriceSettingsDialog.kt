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
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PostAdd
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
import com.holki9.shawarmapos.model.StockType
import com.holki9.shawarmapos.ui.components.EmeraldButton3D
import com.holki9.shawarmapos.ui.components.GoldButton3D
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun PriceSettingsDialog(
    menu: List<MenuItem>,
    onSavePrices: (Map<String, Double>) -> Unit,
    onAddProduct: (name: String, price: Double, group: MenuGroup, stockType: StockType, badge: String?) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onResetDefaults: () -> Unit,
    onDismiss: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    // Local editable map of id to price
    val editedPrices = remember {
        mutableStateMapOf<String, String>().apply {
            menu.forEach { put(it.id, it.price.toInt().toString()) }
        }
    }

    // Keep in sync if menu list changes
    LaunchedEffect(menu) {
        menu.forEach {
            if (!editedPrices.containsKey(it.id)) {
                editedPrices[it.id] = it.price.toInt().toString()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF80A0F1E))
                .border(1.5.dp, GoldNeon.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Управление меню и ценами",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                        Text(
                            text = "Меняй цены, добавляй новинки или удаляй позиции",
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

                Spacer(modifier = Modifier.height(10.dp))

                // Big "+ Добавить новый товар в меню" button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(EmeraldNeon.copy(alpha = 0.15f))
                        .border(1.2.dp, EmeraldNeon.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable { showAddDialog = true }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.PostAdd,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ Добавить новый товар в меню",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldNeon
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable items grouped by category
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category: MAIN
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
                            },
                            onDelete = { onDeleteProduct(item.id) }
                        )
                    }

                    // Category: ADDON
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
                            },
                            onDelete = { onDeleteProduct(item.id) }
                        )
                    }

                    // Category: SNACK
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
                            },
                            onDelete = { onDeleteProduct(item.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

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

            // Dialog for adding a new product
            if (showAddDialog) {
                AddNewProductModal(
                    onAdd = { name, price, group, stockType, badge ->
                        onAddProduct(name, price, group, stockType, badge)
                        showAddDialog = false
                    },
                    onDismiss = { showAddDialog = false }
                )
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
    onDelta: (Int) -> Unit,
    onDelete: () -> Unit
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                item.badge?.let {
                    Text(
                        text = "Бейдж: $it",
                        fontSize = 10.sp,
                        color = TextDarkMuted
                    )
                }
            }

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
                    modifier = Modifier.width(68.dp)
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

                // Delete product button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoseDeep.copy(alpha = 0.25f))
                        .clickable(onClick = onDelete),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Удалить товар",
                        tint = RoseNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddNewProductModal(
    onAdd: (name: String, price: Double, group: MenuGroup, stockType: StockType, badge: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var nameText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var badgeText by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(MenuGroup.MAIN) }
    var selectedStockType by remember { mutableStateOf(StockType.LAVASH) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(330.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xF90A0F1E))
                .border(1.5.dp, EmeraldNeon.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Добавить новый товар",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhite
                )

                // Name
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    placeholder = { Text("Название блюда (напр: Шаурма Сырная)", fontSize = 12.sp, color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = Color(0x3094A3B8),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Price
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    placeholder = { Text("Цена продажи (₽)", fontSize = 12.sp, color = TextDarkMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = Color(0x3094A3B8),
                        focusedTextColor = GoldNeon,
                        unfocusedTextColor = GoldNeon
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selection
                Text("Категория меню:", fontSize = 11.sp, color = TextDarkMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        MenuGroup.MAIN to "Основные",
                        MenuGroup.ADDON to "Добавки",
                        MenuGroup.SNACK to "Закуски"
                    ).forEach { (grp, label) ->
                        val isSel = selectedGroup == grp
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) EmeraldNeon.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                .border(1.dp, if (isSel) EmeraldNeon else Color(0x2094A3B8), RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedGroup = grp
                                    if (grp != MenuGroup.MAIN) selectedStockType = StockType.NONE
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) EmeraldNeon else TextMuted
                            )
                        }
                    }
                }

                // Stock tracking type
                if (selectedGroup == MenuGroup.MAIN) {
                    Text("Списание со склада:", fontSize = 11.sp, color = TextDarkMuted)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            StockType.LAVASH to "Лаваш",
                            StockType.BUN_BIG to "Булка Бол.",
                            StockType.BUN_SMALL to "Булка Мал.",
                            StockType.NONE to "Без списания"
                        ).forEach { (st, label) ->
                            val isSel = selectedStockType == st
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) GoldNeon.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                    .border(1.dp, if (isSel) GoldNeon else Color(0x2094A3B8), RoundedCornerShape(8.dp))
                                    .clickable { selectedStockType = st }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) GoldNeon else TextMuted
                                )
                            }
                        }
                    }
                }

                // Badge text (optional)
                OutlinedTextField(
                    value = badgeText,
                    onValueChange = { badgeText = it },
                    placeholder = { Text("Бейдж на карточке (напр. Лаваш, Новинка)", fontSize = 11.sp, color = TextDarkMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldNeon,
                        unfocusedBorderColor = Color(0x3094A3B8),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Отмена", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    }

                    EmeraldButton3D(
                        text = "+ Добавить",
                        modifier = Modifier.weight(1.3f),
                        onClick = {
                            val price = priceText.toDoubleOrNull() ?: 0.0
                            if (nameText.isNotBlank() && price > 0) {
                                onAdd(nameText, price, selectedGroup, selectedStockType, badgeText)
                            }
                        }
                    )
                }
            }
        }
    }
}
