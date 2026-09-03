package com.calculatorlife.app.ui.calculator.breakeven

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.calculatorlife.app.ui.common.NumberField
import com.calculatorlife.app.ui.common.ResultCard
import java.math.RoundingMode

@Composable
fun BreakEvenCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: BreakEvenCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = stringResource(R.string.calc_break_even), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberField(stringResource(R.string.breakeven_fixed_costs), state.fixedCosts, viewModel::onFixedCostsChanged, allowNegative = false)
            NumberField(stringResource(R.string.breakeven_price_per_unit), state.pricePerUnit, viewModel::onPriceChanged, allowNegative = false)
            NumberField(stringResource(R.string.breakeven_variable_cost), state.variableCostPerUnit, viewModel::onVariableCostChanged, allowNegative = false)

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.breakeven_units),
                    value = result.breakEvenUnits.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${stringResource(R.string.breakeven_revenue)}: ${result.breakEvenRevenue.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
                )
            }
            if (state.error) {
                Text(text = stringResource(R.string.error_breakeven_impossible))
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
