package com.holki9.shawarmapos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory2
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
import com.holki9.shawarmapos.model.Stock
import com.holki9.shawarmapos.ui.components.Card3D
import com.holki9.shawarmapos.ui.components.EmeraldButton3D
import com.holki9.shawarmapos.ui.components.GoldButton3D
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun StockScreen(
    stock: Stock,
    packSize: Int,
    onSetPackSize: (Int) -> Unit,
    onAddLavash: (whitePacks: Int, orangePacks: Int, cost: Double) -> Unit,
    onAddBuns: (cost: Double, bigQty: Int, smallQty: Int) -> Unit,
    onOpenStockCorrection: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Lavash Arrival Form State
    var whitePacksText by remember { mutableStateOf("5") }
    var orangePacksText by remember { mutableStateOf("0") }
    var lavashCostText by remember { mutableStateOf("") }

    // Buns Arrival Form State
    var bunsCostText by remember { mutableStateOf("") }
    var bunsBigText by remember { mutableStateOf("") }
    var bunsSmallText by remember { mutableStateOf("") }

    val whitePacks = whitePacksText.toIntOrNull() ?: 0
    val orangePacks = orangePacksText.toIntOrNull() ?: 0
    val lavashTotalUnitsPreview = (whitePacks + orangePacks) * packSize

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp)
    ) {
        // Current Stock Overview Card
        item {
            Card3D(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 22.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Inventory2,
                                contentDescription = null,
                                tint = GoldNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ОСТАТКИ НА СКЛАДЕ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldNeon,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0x4094A3B8), RoundedCornerShape(10.dp))
                                .clickable(onClick = onOpenStockCorrection)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Коррекция",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Lavash Total
                        StockTile(
                            title = "ЛАВАШ ВСЕГО",
                            value = "${stock.totalLavash}",
                            subtitle = "Бел: ${stock.lavashWhite} | Оранж: ${stock.lavashOrange}",
                            valueColor = GoldNeon,
                            modifier = Modifier.weight(1f)
                        )

                        // Big Bun
                        StockTile(
                            title = "БУЛКА БОЛ.",
                            value = "${stock.bunBig}",
                            subtitle = "Дёнер большой",
                            valueColor = EmeraldNeon,
                            modifier = Modifier.weight(1f)
                        )

                        // Small Bun
                        StockTile(
                            title = "БУЛКА МАЛ.",
                            value = "${stock.bunSmall}",
                            subtitle = "Дёнер малый",
                            valueColor = SkyNeon,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Card: Lavash Arrival
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
                            text = "Приход Лаваша (Упаковками)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Text(
                            text = "По умолч. $packSize шт/уп",
                            fontSize = 11.sp,
                            color = TextDarkMuted
                        )
                    }

                    // Pack Size Switcher (10 шт vs 5 шт)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Штук в 1 упаковке:",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(start = 6.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            PackSizeButton(size = 10, selectedSize = packSize, onClick = { onSetPackSize(10) })
                            PackSizeButton(size = 5, selectedSize = packSize, onClick = { onSetPackSize(5) })
                        }
                    }

                    // Packs inputs: White and Orange
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = whitePacksText,
                            onValueChange = { whitePacksText = it },
                            label = { Text("Упаковок (Белый / Общ)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldNeon,
                                unfocusedBorderColor = Color(0x3094A3B8),
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = orangePacksText,
                            onValueChange = { orangePacksText = it },
                            label = { Text("Оранжевого (необяз.)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberNeon,
                                unfocusedBorderColor = Color(0x3094A3B8),
                                focusedTextColor = GoldNeon,
                                unfocusedTextColor = GoldNeon
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Cost input
                    OutlinedTextField(
                        value = lavashCostText,
                        onValueChange = { lavashCostText = it },
                        placeholder = { Text("Сумма оплаты поставщику (₽)", fontSize = 12.sp, color = TextDarkMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldNeon,
                            unfocusedBorderColor = Color(0x3094A3B8),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Итого придет: $lavashTotalUnitsPreview шт",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldNeon
                        )

                        GoldButton3D(
                            text = "Оприходовать лаваш",
                            onClick = {
                                val cost = lavashCostText.toDoubleOrNull() ?: 0.0
                                onAddLavash(whitePacks, orangePacks, cost)
                                lavashCostText = ""
                            }
                        )
                    }
                }
            }
        }

        // Card: Buns Arrival
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
                            text = "Приход Булочек (Один поставщик)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Text(
                            text = "Общий чек",
                            fontSize = 11.sp,
                            color = TextDarkMuted
                        )
                    }

                    OutlinedTextField(
                        value = bunsCostText,
                        onValueChange = { bunsCostText = it },
                        placeholder = { Text("Общая сумма оплаты за булочки (₽)", fontSize = 12.sp, color = TextDarkMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldNeon,
                            unfocusedBorderColor = Color(0x3094A3B8),
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bunsBigText,
                            onValueChange = { bunsBigText = it },
                            label = { Text("Больших булочек (шт)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldNeon,
                                unfocusedBorderColor = Color(0x3094A3B8),
                                focusedTextColor = EmeraldNeon,
                                unfocusedTextColor = EmeraldNeon
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = bunsSmallText,
                            onValueChange = { bunsSmallText = it },
                            label = { Text("Маленьких (шт)", fontSize = 10.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SkyNeon,
                                unfocusedBorderColor = Color(0x3094A3B8),
                                focusedTextColor = SkyNeon,
                                unfocusedTextColor = SkyNeon
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "Можно ввести только сумму (напр. 2000 ₽) либо с указанием количества больших и маленьких.",
                        fontSize = 10.sp,
                        color = TextDarkMuted
                    )

                    EmeraldButton3D(
                        text = "Записать булочки и списать расход",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val cost = bunsCostText.toDoubleOrNull() ?: 0.0
                            val big = bunsBigText.toIntOrNull() ?: 0
                            val small = bunsSmallText.toIntOrNull() ?: 0
                            onAddBuns(cost, big, small)
                            bunsCostText = ""
                            bunsBigText = ""
                            bunsSmallText = ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StockTile(
    title: String,
    value: String,
    subtitle: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0A0F1E))
            .border(1.dp, Color(0x2594A3B8), RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = valueColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextDarkMuted
            )
        }
    }
}

@Composable
private fun PackSizeButton(
    size: Int,
    selectedSize: Int,
    onClick: () -> Unit
) {
    val isSel = size == selectedSize
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSel) GoldNeon else Color(0xFF1E293B))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$size шт",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSel) Color(0xFF090D16) else TextMuted
        )
    }
}
