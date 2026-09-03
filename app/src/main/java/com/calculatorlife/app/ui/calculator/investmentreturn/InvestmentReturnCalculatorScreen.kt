package com.calculatorlife.app.ui.calculator.investmentreturn

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
fun InvestmentReturnCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: InvestmentReturnCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = stringResource(R.string.calc_investment_return), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberField(stringResource(R.string.invret_initial_value), state.initialValue, viewModel::onInitialChanged, allowNegative = false)
            NumberField(stringResource(R.string.invret_final_value), state.finalValue, viewModel::onFinalChanged, allowNegative = false)
            NumberField(stringResource(R.string.invret_years), state.years, viewModel::onYearsChanged, allowNegative = false)

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.invret_cagr),
                    value = "${result.cagrPercent.setScale(2, RoundingMode.HALF_UP).toPlainString()}%",
                    subtitle = "${stringResource(R.string.invret_absolute_gain)}: ${result.absoluteGain.setScale(2, RoundingMode.HALF_UP).toPlainString()} " +
                        "(${result.absoluteGainPercent.setScale(2, RoundingMode.HALF_UP).toPlainString()}%)"
                )
            }
            if (state.error) {
                Text(text = stringResource(R.string.error_expression))
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
