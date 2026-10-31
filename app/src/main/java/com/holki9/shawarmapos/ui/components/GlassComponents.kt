package com.holki9.shawarmapos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun NeonHeroBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = Brush.horizontalGradient(
        listOf(
            EmeraldNeon.copy(alpha = 0.85f),
            SkyNeon.copy(alpha = 0.45f),
            PurpleNeon.copy(alpha = 0.70f),
            RoseNeon.copy(alpha = 0.85f)
        )
    )
    val bgBrush = Brush.linearGradient(
        listOf(
            Color(0xFA0E172A),
            Color(0xFA0A0F1E)
        )
    )

    Box(
        modifier = modifier
            .shadow(16.dp, shape = shape, spotColor = EmeraldNeon.copy(alpha = 0.25f))
            .clip(shape)
            .background(bgBrush)
            .border(1.5.dp, borderBrush, shape),
        content = content
    )
}

@Composable
fun Card3D(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1.0f, label = "card_press")

    val shape = RoundedCornerShape(cornerRadius)

    val borderBrush = if (isActive) {
        Brush.linearGradient(
            listOf(GoldNeon, AmberNeon, EmeraldNeon)
        )
    } else {
        Brush.linearGradient(
            listOf(
                CardBorderTeal.copy(alpha = 0.55f),
                Color(0xFF3B82F6).copy(alpha = 0.25f),
                CardBorderPurple.copy(alpha = 0.40f)
            )
        )
    }

    val bgBrush = if (isActive) {
        Brush.verticalGradient(
            listOf(Color(0xFF1E1E2F), Color(0xFF0F1221))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFF11192C), Color(0xFF090D1A))
        )
    }

    val clickableMod = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Column(
        modifier = modifier
            .scale(scale)
            .shadow(12.dp, shape = shape, spotColor = if (isActive) GoldNeon.copy(alpha = 0.35f) else Color.Black)
            .clip(shape)
            .background(bgBrush)
            .border(if (isActive) 1.8.dp else 1.2.dp, borderBrush, shape)
            .then(clickableMod),
        content = content
    )
}

@Composable
fun GoldButton3D(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1.0f, label = "btn_press")

    val shape = RoundedCornerShape(16.dp)
    val brush = Brush.verticalGradient(
        listOf(Color(0xFFFDE047), Color(0xFFEAB308), Color(0xFFCA8A04))
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(10.dp, shape = shape, spotColor = GoldNeon.copy(alpha = 0.45f))
            .clip(shape)
            .background(if (enabled) brush else Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray)))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF090D16),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun EmeraldButton3D(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1.0f, label = "btn_press")

    val shape = RoundedCornerShape(16.dp)
    val brush = Brush.verticalGradient(
        listOf(Color(0xFF34D399), Color(0xFF10B981), Color(0xFF059669))
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(12.dp, shape = shape, spotColor = EmeraldNeon.copy(alpha = 0.5f))
            .clip(shape)
            .background(if (enabled) brush else Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray)))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF040711),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun RoseButton3D(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1.0f, label = "btn_press")

    val shape = RoundedCornerShape(16.dp)
    val brush = Brush.verticalGradient(
        listOf(Color(0xFFFB7185), Color(0xFFE11D48), Color(0xFFBE123C))
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(10.dp, shape = shape, spotColor = RoseNeon.copy(alpha = 0.45f))
            .clip(shape)
            .background(if (enabled) brush else Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray)))
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
