package com.calculatorlife.app.ui.calculator.profitloss

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
fun ProfitLossCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: ProfitLossCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = stringResource(R.string.calc_profit_loss), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NumberField(stringResource(R.string.pl_cost_price), state.costPrice, viewModel::onCostPriceChanged, allowNegative = false)
            NumberField(stringResource(R.string.pl_selling_price), state.sellingPrice, viewModel::onSellingPriceChanged, allowNegative = false)

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(if (result.isProfit) R.string.pl_profit else R.string.pl_loss),
                    value = result.amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${result.percent.setScale(2, RoundingMode.HALF_UP).toPlainString()}%"
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
