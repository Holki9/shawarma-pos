package com.holki9.shawarmapos.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.model.MenuItem
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun FloatingLiveOrderBar(
    draftOrder: Map<String, Int>,
    menu: List<MenuItem>,
    onClearOrder: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = draftOrder.values.sum()
    val isVisible = totalCount > 0

    var totalPrice = 0.0
    val summaryParts = mutableListOf<String>()

    draftOrder.forEach { (id, count) ->
        val item = menu.find { it.id == id }
        if (item != null && count > 0) {
            totalPrice += item.price * count
            summaryParts.add("${item.name} ×$count")
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        val shape = RoundedCornerShape(22.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .shadow(24.dp, shape = shape, spotColor = GoldNeon.copy(alpha = 0.35f))
                .clip(shape)
                .background(Color(0xF50D152A))
                .border(1.5.dp, GoldNeon.copy(alpha = 0.65f), shape)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Clear button + summary
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoseNeon.copy(alpha = 0.18f))
                            .border(1.dp, RoseNeon.copy(alpha = 0.40f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onClearOrder),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Сбросить заказ",
                            tint = RoseNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ВЫБРАНО: $totalCount ПОЗ.",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GoldNeon,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = summaryParts.joinToString(", "),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right: Big "ПРОБИТЬ X ₽"
                GoldButton3D(
                    text = "ПРОБИТЬ  ${formatMoney(totalPrice)}",
                    onClick = onCheckout
                )
            }
        }
    }
}

private fun formatMoney(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        String.format(java.util.Locale("ru"), "%,d ₽", amount.toLong()).replace(',', ' ')
    } else {
        String.format(java.util.Locale("ru"), "%,.0f ₽", amount).replace(',', ' ')
    }
}
