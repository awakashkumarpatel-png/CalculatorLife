package com.calculatorlife.app.ui.calculator.scientific

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculatorlife.app.R
import com.calculatorlife.app.data.HistoryRecorder
import com.calculatorlife.app.ui.calculator.standard.CalculatorKey
import com.calculatorlife.app.ui.calculator.standard.KeyStyle
import com.calculatorlife.app.ui.common.CalculatorScaffold

@Composable
fun ScientificCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: ScientificCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val calculatorTitle = stringResource(R.string.calc_scientific)
    val context = LocalContext.current

    LaunchedEffect(state.justEvaluated, state.expression) {
        if (state.justEvaluated && !state.isError) {
            HistoryRecorder.record(context, calculatorTitle, "${state.expressionPreview} ${state.expression}".trim(), null)
        }
    }

    CalculatorScaffold(title = calculatorTitle, onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                AssistChip(
                    onClick = viewModel::onToggleAngleMode,
                    label = { Text(if (state.angleMode == AngleMode.DEGREES) "DEG" else "RAD") }
                )
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                if (state.expressionPreview.isNotEmpty()) {
                    Text(
                        text = state.expressionPreview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val decimalPlaces by remember(context) {
                    com.calculatorlife.app.data.SettingsRepository(context).decimalPlaces
                }.collectAsState(initial = 2)
                val displayText = if (state.isError) stringResource(R.string.error_expression)
                    else if (state.justEvaluated) formatWithDecimalPlacesSci(state.expression, decimalPlaces)
                    else state.expression.ifEmpty { "0" }
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = if (displayText.length > 14) 26.sp else 36.sp,
                    fontWeight = FontWeight.Light,
                    color = if (state.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (state.livePreview.isNotEmpty() && !state.isError && !state.justEvaluated) {
                    Text(
                        text = "= ${state.livePreview}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }
            }

            ScientificKeypad(viewModel)
        }
    }
}

@Composable
private fun ScientificKeypad(viewModel: ScientificCalculatorViewModel) {
    data class Key(val label: String, val style: KeyStyle, val insert: String = "")

    val rows = listOf(
        listOf(Key("sin", KeyStyle.FUNCTION, "sin("), Key("cos", KeyStyle.FUNCTION, "cos("), Key("tan", KeyStyle.FUNCTION, "tan("), Key("(", KeyStyle.FUNCTION, "("), Key(")", KeyStyle.FUNCTION, ")")),
        listOf(Key("log", KeyStyle.FUNCTION, "log("), Key("ln", KeyStyle.FUNCTION, "ln("), Key("√", KeyStyle.FUNCTION, "sqrt("), Key("^", KeyStyle.FUNCTION, "^"), Key("π", KeyStyle.FUNCTION, "pi")),
        listOf(Key("7", KeyStyle.NUMBER, "7"), Key("8", KeyStyle.NUMBER, "8"), Key("9", KeyStyle.NUMBER, "9"), Key("÷", KeyStyle.OPERATOR, "/"), Key("C", KeyStyle.FUNCTION)),
        listOf(Key("4", KeyStyle.NUMBER, "4"), Key("5", KeyStyle.NUMBER, "5"), Key("6", KeyStyle.NUMBER, "6"), Key("×", KeyStyle.OPERATOR, "*"), Key("⌫", KeyStyle.FUNCTION)),
        listOf(Key("1", KeyStyle.NUMBER, "1"), Key("2", KeyStyle.NUMBER, "2"), Key("3", KeyStyle.NUMBER, "3"), Key("−", KeyStyle.OPERATOR, "-"), Key("e", KeyStyle.FUNCTION, "e")),
        listOf(Key("0", KeyStyle.NUMBER, "0"), Key(".", KeyStyle.NUMBER, "."), Key("%", KeyStyle.FUNCTION, "%"), Key("+", KeyStyle.OPERATOR, "+"), Key("=", KeyStyle.EQUALS))
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    CalculatorKey(label = key.label, style = key.style, modifier = Modifier.weight(1f)) {
                        when (key.label) {
                            "C" -> viewModel.onClear()
                            "⌫" -> viewModel.onBackspace()
                            "=" -> viewModel.onEquals()
                            else -> viewModel.onInsert(key.insert)
                        }
                    }
                }
            }
        }
    }
}

/** Applies the user's Settings decimal-place preference to a finished result; falls back to the raw string if it isn't a plain number. */
private fun formatWithDecimalPlacesSci(raw: String, places: Int): String = try {
    java.math.BigDecimal(raw).setScale(places, java.math.RoundingMode.HALF_UP).toPlainString()
} catch (e: NumberFormatException) {
    raw
}
