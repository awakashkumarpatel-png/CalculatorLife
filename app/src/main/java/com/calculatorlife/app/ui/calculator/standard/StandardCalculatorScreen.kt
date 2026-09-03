package com.calculatorlife.app.ui.calculator.standard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.calculatorlife.app.ui.common.CalculatorScaffold

@Composable
fun StandardCalculatorScreen(
    onOpenMenu: () -> Unit,
    onVaultPinMatched: ((String) -> Boolean)? = null,
    onVaultUnlocked: () -> Unit = {},
    viewModel: StandardCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val calculatorTitle = stringResource(R.string.calc_standard)
    val context = LocalContext.current

    // Record only the completed calculation shown right after '=' — not
    // every intermediate keystroke.
    LaunchedEffect(state.justEvaluated, state.currentInput) {
        if (state.justEvaluated && !state.isError) {
            HistoryRecorder.record(context, calculatorTitle, "${state.expressionPreview} ${state.currentInput}".trim(), null)
        }
    }

    CalculatorScaffold(title = calculatorTitle, onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            DisplayArea(state = state, modifier = Modifier.weight(1f))
            Keypad(
                viewModel = viewModel,
                onEqualsLongPress = {
                    // The spec's "unlock the vault from the calculator's secret
                    // PIN" gesture: long-press '=' checks whatever's currently
                    // typed against the vault PIN, entirely separately from
                    // normal calculation. A wrong/no PIN here does nothing —
                    // there's no error shown, so a bystander watching the
                    // screen sees nothing unusual happen.
                    if (onVaultPinMatched?.invoke(state.currentInput) == true) onVaultUnlocked()
                }
            )
        }
    }
}

@Composable
private fun DisplayArea(state: CalculatorState, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val decimalPlaces by remember(context) {
        com.calculatorlife.app.data.SettingsRepository(context).decimalPlaces
    }.collectAsState(initial = 2)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        if (state.expressionPreview.isNotEmpty()) {
            Text(
                text = state.expressionPreview,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        val displayText = if (state.isError) {
            if (state.errorMessageIsDivideByZero) {
                stringResource(R.string.error_divide_by_zero)
            } else {
                stringResource(R.string.error_expression)
            }
        } else if (state.justEvaluated) {
            // Only the settled result gets the fixed decimal-place format —
            // reformatting mid-typing would fight the user's own keystrokes.
            formatWithDecimalPlaces(state.currentInput, decimalPlaces)
        } else {
            state.currentInput
        }
        Text(
            text = displayText,
            style = MaterialTheme.typography.displayLarge,
            fontSize = if (displayText.length > 9) 40.sp else 56.sp,
            fontWeight = FontWeight.Light,
            color = if (state.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun Keypad(viewModel: StandardCalculatorViewModel, onEqualsLongPress: () -> Unit) {
    val rows: List<List<Pair<String, KeyStyle>>> = listOf(
        listOf("C" to KeyStyle.FUNCTION, "±" to KeyStyle.FUNCTION, "%" to KeyStyle.FUNCTION, "÷" to KeyStyle.OPERATOR),
        listOf("7" to KeyStyle.NUMBER, "8" to KeyStyle.NUMBER, "9" to KeyStyle.NUMBER, "×" to KeyStyle.OPERATOR),
        listOf("4" to KeyStyle.NUMBER, "5" to KeyStyle.NUMBER, "6" to KeyStyle.NUMBER, "−" to KeyStyle.OPERATOR),
        listOf("1" to KeyStyle.NUMBER, "2" to KeyStyle.NUMBER, "3" to KeyStyle.NUMBER, "+" to KeyStyle.OPERATOR),
        listOf("0" to KeyStyle.NUMBER, "." to KeyStyle.NUMBER, "⌫" to KeyStyle.FUNCTION, "=" to KeyStyle.EQUALS)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { (label, style) ->
                    CalculatorKey(
                        label = label,
                        style = style,
                        modifier = Modifier.weight(1f),
                        onLongClick = if (label == "=") onEqualsLongPress else null
                    ) {
                        handleKey(label, viewModel)
                    }
                }
            }
        }
    }
}

private fun handleKey(label: String, viewModel: StandardCalculatorViewModel) {
    when (label) {
        "C" -> viewModel.onClear()
        "±" -> viewModel.onToggleSign()
        "%" -> viewModel.onPercent()
        "÷" -> viewModel.onOperator(Operator.DIVIDE)
        "×" -> viewModel.onOperator(Operator.MULTIPLY)
        "−" -> viewModel.onOperator(Operator.SUBTRACT)
        "+" -> viewModel.onOperator(Operator.ADD)
        "=" -> viewModel.onEquals()
        "⌫" -> viewModel.onBackspace()
        "." -> viewModel.onDecimal()
        else -> if (label.length == 1 && label[0].isDigit()) viewModel.onDigit(label[0])
    }
}

/** Applies the user's Settings decimal-place preference to a finished result; falls back to the raw string if it isn't a plain number. */
private fun formatWithDecimalPlaces(raw: String, places: Int): String = try {
    java.math.BigDecimal(raw).setScale(places, java.math.RoundingMode.HALF_UP).toPlainString()
} catch (e: NumberFormatException) {
    raw
}
