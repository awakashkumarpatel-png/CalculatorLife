package com.calculatorlife.app.ui.calculator.fraction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculatorlife.app.R
import com.calculatorlife.app.ui.common.CalculatorScaffold
import com.calculatorlife.app.ui.common.ModeSelector
import com.calculatorlife.app.ui.common.NumberField
import com.calculatorlife.app.ui.common.ResultCard

@Composable
fun FractionCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: FractionCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val operators = FractionOperator.entries

    CalculatorScaffold(title = stringResource(R.string.calc_fraction), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.fraction_first), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(stringResource(R.string.fraction_numerator), state.num1, viewModel::onNum1Changed, modifier = Modifier.weight(1f))
                NumberField(stringResource(R.string.fraction_denominator), state.den1, viewModel::onDen1Changed, modifier = Modifier.weight(1f))
            }

            ModeSelector(
                options = operators.map { it.symbol },
                selectedIndex = operators.indexOf(state.operator),
                onSelect = { viewModel.onOperatorSelected(operators[it]) }
            )

            Text(stringResource(R.string.fraction_second), style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(stringResource(R.string.fraction_numerator), state.num2, viewModel::onNum2Changed, modifier = Modifier.weight(1f))
                NumberField(stringResource(R.string.fraction_denominator), state.den2, viewModel::onDen2Changed, modifier = Modifier.weight(1f))
            }

            state.result?.let { fraction ->
                ResultCard(
                    title = stringResource(R.string.action_result),
                    value = fraction.toString(),
                    subtitle = "≈ ${fraction.toDecimalString()}"
                )
            }
            if (state.error) {
                Text(text = stringResource(R.string.error_divide_by_zero))
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
