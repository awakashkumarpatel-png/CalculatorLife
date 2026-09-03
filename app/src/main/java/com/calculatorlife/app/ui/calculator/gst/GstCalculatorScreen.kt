package com.calculatorlife.app.ui.calculator.gst

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
fun GstCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: GstCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val modes = GstMode.entries

    CalculatorScaffold(title = stringResource(R.string.calc_gst), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeSelector(
                options = listOf(stringResource(R.string.gst_mode_add), stringResource(R.string.gst_mode_remove)),
                selectedIndex = modes.indexOf(state.mode),
                onSelect = { viewModel.onModeSelected(modes[it]) }
            )

            NumberField(stringResource(R.string.gst_amount), state.amount, viewModel::onAmountChanged, allowNegative = false)
            NumberField(stringResource(R.string.gst_rate), state.ratePercent, viewModel::onRateChanged, allowNegative = false)

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.gst_total_amount),
                    value = result.totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                    subtitle = "${stringResource(R.string.gst_base_amount)}: ${result.baseAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()} · " +
                        "${stringResource(R.string.gst_amount_result)}: ${result.gstAmount.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
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
