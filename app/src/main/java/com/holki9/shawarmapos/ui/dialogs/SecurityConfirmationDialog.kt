package com.holki9.shawarmapos.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
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
import com.holki9.shawarmapos.ui.components.RoseButton3D
import com.holki9.shawarmapos.ui.theme.*
import com.holki9.shawarmapos.viewmodel.SecurityDialogData

@Composable
fun SecurityConfirmationDialog(
    data: SecurityDialogData,
    onDismiss: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    // Edit fields
    var editAmountText by remember { mutableStateOf(if (data.isEdit) data.initialAmount.toInt().toString() else "") }
    var editTitleText by remember { mutableStateOf(if (data.isEdit) data.initialTitle else "") }

    // Math challenge
    val num1 = remember { (4..12).random() }
    val num2 = remember { (3..9).random() }
    val correctSolution = remember { num1 + num2 }
    var mathAnswerText by remember { mutableStateOf("") }
    var mathError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF80A0F1E))
                .border(1.5.dp, RoseNeon.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = RoseNeon,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = data.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                }

                Text(
                    text = if (step == 1) data.description else "ШАГ 2 ИЗ 2: Реши пример для подтверждения действия:",
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 17.sp
                )

                // If editing in step 1
                if (data.isEdit && step == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Новая сумма (₽):", fontSize = 11.sp, color = TextDarkMuted)
                        OutlinedTextField(
                            value = editAmountText,
                            onValueChange = { editAmountText = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoseNeon,
                                unfocusedBorderColor = Color(0x3094A3B8),
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Описание:", fontSize = 11.sp, color = TextDarkMuted)
                        OutlinedTextField(
                            value = editTitleText,
                            onValueChange = { editTitleText = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoseNeon,
                                unfocusedBorderColor = Color(0x3094A3B8),
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // If step 2 with math challenge
                if (step == 2 && data.requireMath) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF04060F))
                            .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$num1 + $num2 = ?",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldNeon
                            )

                            OutlinedTextField(
                                value = mathAnswerText,
                                onValueChange = {
                                    mathAnswerText = it
                                    mathError = false
                                },
                                placeholder = { Text("Ответ", fontSize = 12.sp, color = TextDarkMuted) },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Black),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = if (mathError) RoseNeon else GoldNeon,
                                    unfocusedBorderColor = if (mathError) RoseNeon else Color(0x3094A3B8),
                                    focusedTextColor = GoldNeon,
                                    unfocusedTextColor = GoldNeon
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.width(90.dp)
                            )
                        }
                    }

                    if (mathError) {
                        Text(
                            text = "Неверный ответ! Попробуй снова.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoseNeon
                        )
                    }
                }

                // Buttons
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

                    RoseButton3D(
                        text = if (step == 1) "Продолжить (1/2)" else "Выполнить (2/2)",
                        modifier = Modifier.weight(1.3f),
                        onClick = {
                            if (step == 1) {
                                if (data.requireMath) {
                                    step = 2
                                } else {
                                    // No math required, execute
                                    data.onConfirm(
                                        editAmountText.toDoubleOrNull(),
                                        editTitleText.trim().ifEmpty { null }
                                    )
                                    onDismiss()
                                }
                            } else {
                                // Step 2 verify math
                                val ans = mathAnswerText.toIntOrNull()
                                if (ans == correctSolution) {
                                    data.onConfirm(
                                        editAmountText.toDoubleOrNull(),
                                        editTitleText.trim().ifEmpty { null }
                                    )
                                    onDismiss()
                                } else {
                                    mathError = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
