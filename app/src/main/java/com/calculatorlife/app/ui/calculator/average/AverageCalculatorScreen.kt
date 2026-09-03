package com.calculatorlife.app.ui.calculator.average

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.calculatorlife.app.ui.common.ResultCard
import java.math.RoundingMode

@Composable
fun AverageCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: AverageCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = stringResource(R.string.calc_average), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::onInputChanged,
                label = { Text(stringResource(R.string.average_input_hint)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.average_mean),
                    value = result.mean.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
                )
                ResultCard(
                    title = stringResource(R.string.average_sum),
                    value = result.sum.stripTrailingZeros().toPlainString(),
                    subtitle = "${stringResource(R.string.average_count)}: ${result.count} · " +
                        "${stringResource(R.string.average_min)}: ${result.min.stripTrailingZeros().toPlainString()} · " +
                        "${stringResource(R.string.average_max)}: ${result.max.stripTrailingZeros().toPlainString()}"
                )
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
