package com.holki9.shawarmapos.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.model.AppState
import com.holki9.shawarmapos.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HeaderView(
    state: AppState,
    onOpenCalculator: () -> Unit,
    onOpenShiftManager: () -> Unit,
    onOpenPriceSettings: () -> Unit
) {
    val dateStr = rememberDateString()

    // Pulse animation for shift status dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Top status and fast action row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Pulsating status dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (state.shiftOpen) EmeraldNeon.copy(alpha = dotAlpha) else RoseNeon)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (state.shiftOpen) "Смена открыта" else "Смена закрыта",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )

                        // Quick "Цены" button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberNeon.copy(alpha = 0.16f))
                                .border(1.dp, AmberNeon.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                                .clickable(onClick = onOpenPriceSettings)
                                .padding(horizontal = 9.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Цены",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldNeon
                            )
                        }
                    }
                }
            }

            // Top Quick Buttons: Calculator and Shift Manager
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Calculator Icon Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    GoldNeon.copy(alpha = 0.15f),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(1.5.dp, GoldNeon.copy(alpha = 0.5f), RoundedCornerShape(15.dp))
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

                // Shift Manager Icon Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    PurpleNeon.copy(alpha = 0.20f),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(1.5.dp, PurpleNeon.copy(alpha = 0.55f), RoundedCornerShape(15.dp))
                        .clickable(onClick = onOpenShiftManager),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = "Управление сменой",
                        tint = TextWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 3-Column Hero Neon Balance Card
        NeonHeroBox(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Column 1: Total cash
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "В КАССЕ (ИТОГ)",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = formatSum(state.totalCashInRegister),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "С прошл: ${formatSum(state.carryoverCash)}",
                        fontSize = 10.sp,
                        color = TextDarkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Divider 1
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(38.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )

                // Column 2: Day's Income
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "ВЫРУЧКА ДНЯ",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldNeon,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "+${formatSum(state.totalIncome)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldNeon,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    val net = state.netProfitToday
                    Text(
                        text = (if (net >= 0) "Чистыми: +" else "Минус: ") + formatSum(net),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (net >= 0) EmeraldLight else RoseNeon,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Divider 2
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(38.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )

                // Column 3: Day's Expense & Stock summary
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "РАСХОД ДНЯ",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoseNeon,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "-${formatSum(state.totalExpense)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = RoseNeon,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Лав: ${state.stock.totalLavash} | Бул: ${state.stock.totalBuns}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldNeon,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun formatSum(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        String.format(Locale("ru"), "%,d ₽", amount.toLong()).replace(',', ' ')
    } else {
        String.format(Locale("ru"), "%,.0f ₽", amount).replace(',', ' ')
    }
}

@Composable
private fun rememberDateString(): String {
    val sdf = SimpleDateFormat("EE, d MMMM", Locale("ru"))
    return sdf.format(Date()).uppercase(Locale("ru"))
}
