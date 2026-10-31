package com.holki9.shawarmapos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.model.MenuGroup
import com.holki9.shawarmapos.model.MenuItem
import com.holki9.shawarmapos.ui.components.Card3D
import com.holki9.shawarmapos.ui.components.EmeraldButton3D
import com.holki9.shawarmapos.ui.components.NeonHeroBox
import com.holki9.shawarmapos.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PosScreen(
    menu: List<MenuItem>,
    draftOrder: Map<String, Int>,
    onIncrement: (String, Int) -> Unit,
    onOpenQtyDialog: (MenuItem) -> Unit,
    onQuickSale: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var quickSaleText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val mainItems = remember(menu) { menu.filter { it.group == MenuGroup.MAIN } }
    val addonItems = remember(menu) { menu.filter { it.group == MenuGroup.ADDON } }
    val snackItems = remember(menu) { menu.filter { it.group == MenuGroup.SNACK } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp)
    ) {
        // Quick sale bar
        item {
            NeonHeroBox(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(EmeraldNeon.copy(alpha = 0.18f))
                            .border(1.2.dp, EmeraldNeon.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = "Быстрый ввод",
                            tint = EmeraldNeon,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    OutlinedTextField(
                        value = quickSaleText,
                        onValueChange = { quickSaleText = it },
                        placeholder = {
                            Text(
                                "Быстрая сумма в кассу",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val amount = quickSaleText.toDoubleOrNull() ?: 0.0
                                if (amount > 0) {
                                    onQuickSale(amount)
                                    quickSaleText = ""
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldNeon,
                            unfocusedBorderColor = Color(0x4094A3B8),
                            focusedContainerColor = Color(0xD0070B16),
                            unfocusedContainerColor = Color(0xD0070B16),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    )

                    EmeraldButton3D(
                        text = "+ В кассу",
                        onClick = {
                            val amount = quickSaleText.toDoubleOrNull() ?: 0.0
                            if (amount > 0) {
                                onQuickSale(amount)
                                quickSaleText = ""
                                focusManager.clearFocus()
                            }
                        }
                    )
                }
            }
        }

        // Section 1: Main Items (Шаурма и Дёнеры)
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.RestaurantMenu,
                            contentDescription = null,
                            tint = GoldNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ШАУРМА И ДЁНЕРЫ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Нажми на цифру для ввода",
                        fontSize = 10.sp,
                        color = TextDarkMuted
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    mainItems.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { item ->
                                val qty = draftOrder[item.id] ?: 0
                                MainItemCard(
                                    item = item,
                                    qty = qty,
                                    onIncrement = { delta -> onIncrement(item.id, delta) },
                                    onOpenQty = { onOpenQtyDialog(item) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Addons (Добавки к заказу)
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Fastfood,
                            contentDescription = null,
                            tint = GoldNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ДОБАВКИ К ЗАКАЗУ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Отдельный учёт",
                        fontSize = 10.sp,
                        color = GoldNeon
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    addonItems.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                val qty = draftOrder[item.id] ?: 0
                                AddonItemCard(
                                    item = item,
                                    qty = qty,
                                    onIncrement = { delta -> onIncrement(item.id, delta) },
                                    onOpenQty = { onOpenQtyDialog(item) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Snacks & Drinks (Закуски и Напитки)
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.LocalDrink,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ЗАКУСКИ И НАПИТКИ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = "Без списания лаваша",
                        fontSize = 10.sp,
                        color = TextDarkMuted
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    snackItems.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { item ->
                                val qty = draftOrder[item.id] ?: 0
                                SnackItemCard(
                                    item = item,
                                    qty = qty,
                                    onIncrement = { delta -> onIncrement(item.id, delta) },
                                    onOpenQty = { onOpenQtyDialog(item) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainItemCard(
    item: MenuItem,
    qty: Int,
    onIncrement: (Int) -> Unit,
    onOpenQty: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = qty > 0

    Card3D(
        modifier = modifier.heightIn(min = 144.dp),
        isActive = isSelected,
        onClick = { onIncrement(1) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge ("Лаваш", "Пита", "Булка Бол.")
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x8019233A))
                            .border(1.dp, Color(0x4094A3B8), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.badge ?: "Меню",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }

                    Text(
                        text = "${item.price.toInt()} ₽",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldNeon
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Counter capsule [- 0 шт +]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(PillBg)
                    .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(14.dp))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Minus button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CounterMinusBg)
                        .clickable { onIncrement(-1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = "Минус",
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Count text (tap opens direct quantity dialog)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onOpenQty),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$qty шт",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) GoldNeon else TextMuted
                    )
                }

                // Plus button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFFFDE047), Color(0xFFEAB308), Color(0xFFCA8A04))
                            )
                        )
                        .clickable { onIncrement(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Плюс",
                        tint = Color(0xFF090D16),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddonItemCard(
    item: MenuItem,
    qty: Int,
    onIncrement: (Int) -> Unit,
    onOpenQty: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = qty > 0

    Card3D(
        modifier = modifier,
        isActive = isSelected,
        cornerRadius = 16.dp,
        onClick = { onIncrement(1) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "+${item.price.toInt()} ₽",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = GoldNeon,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PillBg)
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CounterMinusBg)
                        .clickable { onIncrement(-1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Remove,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Text(
                    text = "$qty",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) GoldNeon else TextMuted,
                    modifier = Modifier.clickable(onClick = onOpenQty)
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldNeon)
                        .clickable { onIncrement(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Color(0xFF090D16),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SnackItemCard(
    item: MenuItem,
    qty: Int,
    onIncrement: (Int) -> Unit,
    onOpenQty: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = qty > 0

    Card3D(
        modifier = modifier,
        isActive = isSelected,
        cornerRadius = 18.dp,
        onClick = { onIncrement(1) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.price.toInt()} ₽",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldNeon
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(PillBg)
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (qty > 0) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CounterMinusBg)
                            .clickable { onIncrement(-1) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Remove,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "$qty",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldNeon,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable(onClick = onOpenQty)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldNeon)
                        .clickable { onIncrement(1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = Color(0xFF090D16),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
