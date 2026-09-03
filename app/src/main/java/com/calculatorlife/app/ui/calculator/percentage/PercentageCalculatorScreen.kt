package com.calculatorlife.app.ui.calculator.percentage

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
fun PercentageCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: PercentageCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val modes = PercentageMode.entries
    val modeLabels = listOf(
        stringResource(R.string.percentage_mode_value_of),
        stringResource(R.string.percentage_mode_what_percent),
        stringResource(R.string.percentage_mode_increase),
        stringResource(R.string.percentage_mode_decrease),
        stringResource(R.string.percentage_mode_percent_change)
    )

    CalculatorScaffold(title = stringResource(R.string.calc_percentage), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeSelector(
                options = modeLabels,
                selectedIndex = modes.indexOf(state.mode),
                onSelect = { viewModel.onModeSelected(modes[it]) }
            )

            NumberField(
                label = stringResource(R.string.percentage_label_x),
                value = state.xInput,
                onValueChange = viewModel::onXChanged
            )
            NumberField(
                label = stringResource(R.string.percentage_label_y),
                value = state.yInput,
                onValueChange = viewModel::onYChanged
            )

            state.result?.let { result ->
                ResultCard(
                    title = result.explanation,
                    value = result.result.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(),
                )
            }
            if (state.error) {
                Text(text = stringResource(R.string.error_divide_by_zero))
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
