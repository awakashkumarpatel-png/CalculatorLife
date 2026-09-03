package com.calculatorlife.app.ui.calculator.interest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.calculatorlife.app.ui.calculator.fd.CompoundingFrequency
import com.calculatorlife.app.ui.common.CalculatorScaffold
import com.calculatorlife.app.ui.common.ModeSelector
import com.calculatorlife.app.ui.common.NumberField
import com.calculatorlife.app.ui.common.ResultCard
import java.math.RoundingMode

@Composable
fun CompoundInterestCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: CompoundInterestCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val frequencies = CompoundingFrequency.entries
    val frequencyLabels = listOf(
        stringResource(R.string.fd_frequency_yearly),
        stringResource(R.string.fd_frequency_half_yearly),
        stringResource(R.string.fd_frequency_quarterly),
        stringResource(R.string.fd_frequency_monthly)
    )

    CalculatorScaffold(title = stringResource(R.string.calc_compound_interest), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberField(stringResource(R.string.interest_principal), state.principal, viewModel::onPrincipalChanged, allowNegative = false)
            NumberField(stringResource(R.string.interest_rate), state.annualRatePercent, viewModel::onRateChanged, allowNegative = false)
            NumberField(stringResource(R.string.interest_years), state.years, viewModel::onYearsChanged, allowNegative = false)

            Text(stringResource(R.string.fd_frequency), style = MaterialTheme.typography.titleMedium)
            ModeSelector(
                options = frequencyLabels,
                selectedIndex = frequencies.indexOf(state.frequency),
                onSelect = { viewModel.onFrequencySelected(frequencies[it]) }
            )

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.interest_total_amount),
                    value = result.maturityValue.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${stringResource(R.string.interest_amount)}: ${result.totalInterest.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
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
