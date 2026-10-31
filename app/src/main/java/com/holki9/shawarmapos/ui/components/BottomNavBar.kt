package com.holki9.shawarmapos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HistoryEdu
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.ui.theme.*
import com.holki9.shawarmapos.viewmodel.PosTab

@Composable
fun BottomNavBar(
    activeTab: PosTab,
    onTabSelected: (PosTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(26.dp)
    val borderBrush = Brush.horizontalGradient(
        listOf(
            TealNeon.copy(alpha = 0.65f),
            GoldNeon.copy(alpha = 0.40f),
            Color(0xFF3B82F6).copy(alpha = 0.30f),
            PurpleNeon.copy(alpha = 0.60f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, shape = shape, spotColor = Color.Black)
                .clip(shape)
                .background(Color(0xF00A0F1E))
                .border(1.5.dp, borderBrush, shape)
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                title = "Касса",
                icon = Icons.Rounded.ShoppingCart,
                isSelected = activeTab == PosTab.POS,
                onClick = { onTabSelected(PosTab.POS) }
            )
            NavItem(
                title = "Расходы",
                icon = Icons.Rounded.ReceiptLong,
                isSelected = activeTab == PosTab.EXPENSES,
                onClick = { onTabSelected(PosTab.EXPENSES) }
            )
            NavItem(
                title = "Склад",
                icon = Icons.Rounded.Inventory2,
                isSelected = activeTab == PosTab.STOCK,
                onClick = { onTabSelected(PosTab.STOCK) }
            )
            NavItem(
                title = "Журнал",
                icon = Icons.Rounded.HistoryEdu,
                isSelected = activeTab == PosTab.HISTORY,
                onClick = { onTabSelected(PosTab.HISTORY) }
            )
        }
    }
}

@Composable
private fun RowScope.NavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) GoldNeon else TextDarkMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isSelected) GoldNeon else TextDarkMuted
        )
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .width(26.dp)
                .height(3.dp)
                .clip(CircleShape)
                .background(if (isSelected) GoldNeon else Color.Transparent)
        )
    }
}
