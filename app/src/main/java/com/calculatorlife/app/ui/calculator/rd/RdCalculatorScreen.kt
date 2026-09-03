package com.calculatorlife.app.ui.calculator.rd

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
fun RdCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: RdCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = stringResource(R.string.calc_rd), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberField(stringResource(R.string.rd_monthly_deposit), state.monthlyDeposit, viewModel::onDepositChanged, allowNegative = false)
            NumberField(stringResource(R.string.rd_rate), state.annualRatePercent, viewModel::onRateChanged, allowNegative = false)
            NumberField(stringResource(R.string.rd_tenure_months), state.tenureMonths, viewModel::onTenureChanged, allowNegative = false)

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.rd_maturity_value),
                    value = result.maturityValue.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${stringResource(R.string.rd_total_deposited)}: ${result.totalDeposited.setScale(2, RoundingMode.HALF_UP).toPlainString()} · " +
                        "${stringResource(R.string.rd_estimated_interest)}: ${result.estimatedInterest.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
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
