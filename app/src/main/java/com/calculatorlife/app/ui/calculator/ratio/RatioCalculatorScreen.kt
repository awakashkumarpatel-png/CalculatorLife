package com.calculatorlife.app.ui.calculator.ratio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
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
fun RatioCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: RatioCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val modes = RatioMode.entries
    val modeLabels = listOf(
        stringResource(R.string.ratio_mode_simplify),
        stringResource(R.string.ratio_mode_proportion)
    )

    CalculatorScaffold(title = stringResource(R.string.calc_ratio), onOpenMenu = onOpenMenu) { padding ->
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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(stringResource(R.string.ratio_label_a), state.a, viewModel::onAChanged, modifier = Modifier.weight(1f))
                NumberField(stringResource(R.string.ratio_label_b), state.b, viewModel::onBChanged, modifier = Modifier.weight(1f))
            }
            if (state.mode == RatioMode.SOLVE_PROPORTION) {
                NumberField(stringResource(R.string.ratio_label_c), state.c, viewModel::onCChanged)
            }

            state.simplified?.let {
                ResultCard(title = stringResource(R.string.action_result), value = it.toString())
            }
            state.solvedX?.let {
                ResultCard(
                    title = "X",
                    value = it.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
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
