package com.holki9.shawarmapos.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.holki9.shawarmapos.ui.components.EmeraldButton3D
import com.holki9.shawarmapos.ui.components.RoseButton3D
import com.holki9.shawarmapos.ui.theme.*

@Composable
fun CalculatorDialog(
    onApplyIncome: (Double) -> Unit,
    onApplyExpense: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }

    fun evaluate(expr: String): Double {
        return try {
            val clean = expr.replace("×", "*").replace("÷", "/")
            val tokens = mutableListOf<String>()
            var currentNum = ""
            for (ch in clean) {
                if (ch in "+-*/") {
                    if (currentNum.isNotEmpty()) {
                        tokens.add(currentNum)
                        currentNum = ""
                    }
                    tokens.add(ch.toString())
                } else {
                    currentNum += ch
                }
            }
            if (currentNum.isNotEmpty()) tokens.add(currentNum)

            if (tokens.isEmpty()) return 0.0

            // Multiplications & Divisions first
            val pass1 = mutableListOf<String>()
            var i = 0
            while (i < tokens.size) {
                val tok = tokens[i]
                if (tok == "*" || tok == "/") {
                    val prev = pass1.removeAt(pass1.size - 1).toDoubleOrNull() ?: 0.0
                    val next = tokens.getOrNull(i + 1)?.toDoubleOrNull() ?: 1.0
                    val res = if (tok == "*") prev * next else if (next != 0.0) prev / next else 0.0
                    pass1.add(res.toString())
                    i += 2
                } else {
                    pass1.add(tok)
                    i++
                }
            }

            // Additions & Subtractions
            var res = pass1.getOrNull(0)?.toDoubleOrNull() ?: 0.0
            var j = 1
            while (j < pass1.size) {
                val op = pass1[j]
                val next = pass1.getOrNull(j + 1)?.toDoubleOrNull() ?: 0.0
                if (op == "+") res += next
                if (op == "-") res -= next
                j += 2
            }
            res
        } catch (_: Exception) {
            0.0
        }
    }

    fun onKey(key: String) {
        when (key) {
            "C" -> {
                display = "0"
                expression = ""
            }
            "⌫" -> {
                display = if (display.length > 1) display.dropLast(1) else "0"
            }
            "=" -> {
                val res = evaluate(display)
                expression = "$display ="
                display = if (res % 1.0 == 0.0) res.toLong().toString() else String.format(java.util.Locale.US, "%.2f", res)
            }
            "+", "-", "×", "÷" -> {
                if (!display.endsWith("+") && !display.endsWith("-") && !display.endsWith("×") && !display.endsWith("÷")) {
                    display += key
                }
            }
            "." -> {
                if (!display.contains(".")) {
                    display += "."
                }
            }
            else -> {
                display = if (display == "0") key else display + key
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xF50A0F1E))
                .border(1.5.dp, GoldNeon.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Калькулятор",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Закрыть",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF04060F))
                        .border(1.dp, Color(0x3094A3B8), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = expression,
                            fontSize = 11.sp,
                            color = TextDarkMuted,
                            maxLines = 1
                        )
                        Text(
                            text = display,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite,
                            maxLines = 1
                        )
                    }
                }

                // Grid Buttons
                val buttons = listOf(
                    listOf("C", "⌫", "÷", "×"),
                    listOf("7", "8", "9", "−"),
                    listOf("4", "5", "6", "+"),
                    listOf("1", "2", "3", "="),
                    listOf("0", ".")
                )

                buttons.take(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { btn ->
                            val isAction = btn in listOf("C", "⌫", "÷", "×", "−", "+", "=")
                            val isEquals = btn == "="
                            val isClear = btn == "C"

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isEquals -> GoldNeon
                                            isClear -> RoseNeon.copy(alpha = 0.2f)
                                            isAction -> Color(0xFF1E293B)
                                            else -> Color(0xFF11192C)
                                        }
                                    )
                                    .border(1.dp, Color(0x2094A3B8), RoundedCornerShape(12.dp))
                                    .clickable { onKey(if (btn == "−") "-" else btn) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = btn,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isEquals -> Color(0xFF090D16)
                                        isClear -> RoseNeon
                                        isAction -> GoldNeon
                                        else -> TextWhite
                                    }
                                )
                            }
                        }
                    }
                }

                // Bottom row: "0" spanning 2 columns, "."
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF11192C))
                            .border(1.dp, Color(0x2094A3B8), RoundedCornerShape(12.dp))
                            .clickable { onKey("0") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("0", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF11192C))
                            .border(1.dp, Color(0x2094A3B8), RoundedCornerShape(12.dp))
                            .clickable { onKey(".") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(".", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Bottom Apply Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EmeraldButton3D(
                        text = "+ В Выручку",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val amount = evaluate(display)
                            if (amount > 0) {
                                onApplyIncome(amount)
                                onDismiss()
                            }
                        }
                    )

                    RoseButton3D(
                        text = "− В Расход",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val amount = evaluate(display)
                            if (amount > 0) {
                                onApplyExpense(amount)
                                onDismiss()
                            }
                        }
                    )
                }
            }
        }
    }
}
