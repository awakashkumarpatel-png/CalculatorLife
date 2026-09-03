package com.calculatorlife.app.ui.calculator.loan

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
fun LoanCalculatorScreen(
    title: String,
    onOpenMenu: () -> Unit,
    viewModel: LoanCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = title, onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberField(stringResource(R.string.loan_principal), state.principal, viewModel::onPrincipalChanged, allowNegative = false)
            NumberField(stringResource(R.string.loan_rate), state.annualRatePercent, viewModel::onRateChanged, allowNegative = false)
            NumberField(stringResource(R.string.loan_tenure_years), state.tenureYears, viewModel::onTenureChanged, allowNegative = false)

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.loan_emi),
                    value = result.emi.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${stringResource(R.string.loan_total_payment)}: ${result.totalPayment.setScale(2, RoundingMode.HALF_UP).toPlainString()} · " +
                        "${stringResource(R.string.loan_total_interest)}: ${result.totalInterest.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
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
