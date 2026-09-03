package com.calculatorlife.app.ui.calculator.incometax

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.calculatorlife.app.ui.common.NumberField
import com.calculatorlife.app.ui.common.ResultCard
import java.math.RoundingMode

@Composable
fun IncomeTaxCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: IncomeTaxCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = stringResource(R.string.calc_income_tax), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = stringResource(R.string.tax_disclaimer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }

            NumberField(stringResource(R.string.tax_annual_income), state.annualIncome, viewModel::onIncomeChanged, allowNegative = false)

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.tax_estimated_tax),
                    value = result.totalTax.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${stringResource(R.string.tax_effective_rate)}: ${result.effectiveRatePercent.setScale(2, RoundingMode.HALF_UP).toPlainString()}% · " +
                        "${stringResource(R.string.tax_net_income)}: ${result.netIncome.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
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
