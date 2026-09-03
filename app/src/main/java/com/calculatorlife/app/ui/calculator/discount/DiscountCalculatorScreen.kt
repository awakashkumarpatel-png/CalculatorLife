package com.calculatorlife.app.ui.calculator.discount

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
fun DiscountCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: DiscountCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val modes = DiscountMode.entries

    CalculatorScaffold(title = stringResource(R.string.calc_discount), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeSelector(
                options = listOf(stringResource(R.string.discount_mode_by_percent), stringResource(R.string.discount_mode_by_final_price)),
                selectedIndex = modes.indexOf(state.mode),
                onSelect = { viewModel.onModeSelected(modes[it]) }
            )

            NumberField(stringResource(R.string.discount_mrp), state.mrp, viewModel::onMrpChanged, allowNegative = false)
            if (state.mode == DiscountMode.BY_PERCENT) {
                NumberField(stringResource(R.string.discount_percent), state.discountPercent, viewModel::onDiscountPercentChanged, allowNegative = false)
            } else {
                NumberField(stringResource(R.string.discount_final_price), state.finalPrice, viewModel::onFinalPriceChanged, allowNegative = false)
            }

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.discount_result_final_price),
                    value = result.finalPrice.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${stringResource(R.string.discount_amount)}: ${result.discountAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()} · " +
                        "${result.discountPercent.setScale(2, RoundingMode.HALF_UP).toPlainString()}%"
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
