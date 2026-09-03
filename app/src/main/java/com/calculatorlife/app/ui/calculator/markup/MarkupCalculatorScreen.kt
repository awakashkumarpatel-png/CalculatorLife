package com.calculatorlife.app.ui.calculator.markup

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
import com.calculatorlife.app.ui.common.ModeSelector
import com.calculatorlife.app.ui.common.NumberField
import com.calculatorlife.app.ui.common.ResultCard
import java.math.RoundingMode

@Composable
fun MarkupCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: MarkupCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val modes = MarkupMode.entries

    CalculatorScaffold(title = stringResource(R.string.calc_markup), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeSelector(
                options = listOf(stringResource(R.string.markup_mode_from_prices), stringResource(R.string.markup_mode_from_percent)),
                selectedIndex = modes.indexOf(state.mode),
                onSelect = { viewModel.onModeSelected(modes[it]) }
            )

            NumberField(stringResource(R.string.markup_cost_price), state.costPrice, viewModel::onCostPriceChanged, allowNegative = false)
            if (state.mode == MarkupMode.FROM_PRICES) {
                NumberField(stringResource(R.string.markup_selling_price), state.sellingPrice, viewModel::onSellingPriceChanged, allowNegative = false)
            } else {
                NumberField(stringResource(R.string.markup_percent), state.markupPercent, viewModel::onMarkupPercentChanged, allowNegative = false)
            }

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.markup_percent),
                    value = "${result.markupPercent.setScale(2, RoundingMode.HALF_UP).toPlainString()}%",
                    subtitle = "${stringResource(R.string.markup_amount)}: ${result.markupAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()} · " +
                        "${stringResource(R.string.markup_result_selling_price)}: ${result.sellingPrice.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
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
