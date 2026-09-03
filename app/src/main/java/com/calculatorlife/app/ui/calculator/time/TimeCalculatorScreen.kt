package com.calculatorlife.app.ui.calculator.time

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
import com.calculatorlife.app.ui.common.CalculatorScaffold
import com.calculatorlife.app.ui.common.HmsFieldsRow
import com.calculatorlife.app.ui.common.ModeSelector
import com.calculatorlife.app.ui.common.ResultCard

@Composable
fun TimeCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: TimeCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val modes = TimeMode.entries
    val operators = TimeOperator.entries
    val hLabel = stringResource(R.string.time_hours)
    val mLabel = stringResource(R.string.time_minutes)
    val sLabel = stringResource(R.string.time_seconds)

    CalculatorScaffold(title = stringResource(R.string.calc_time), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeSelector(
                options = listOf(stringResource(R.string.time_mode_add_subtract), stringResource(R.string.time_mode_difference)),
                selectedIndex = modes.indexOf(state.mode),
                onSelect = { viewModel.onModeSelected(modes[it]) }
            )

            if (state.mode == TimeMode.ADD_SUBTRACT) {
                ModeSelector(
                    options = listOf("+", "−"),
                    selectedIndex = operators.indexOf(state.operator),
                    onSelect = { viewModel.onOperatorSelected(operators[it]) }
                )
            }

            Text(
                text = if (state.mode == TimeMode.ADD_SUBTRACT) stringResource(R.string.time_first) else stringResource(R.string.time_start),
                style = MaterialTheme.typography.titleMedium
            )
            HmsFieldsRow(
                hoursLabel = hLabel, minutesLabel = mLabel, secondsLabel = sLabel,
                hours = state.h1, minutes = state.m1, seconds = state.s1,
                onHoursChange = viewModel::onH1Changed,
                onMinutesChange = viewModel::onM1Changed,
                onSecondsChange = viewModel::onS1Changed
            )

            Text(
                text = if (state.mode == TimeMode.ADD_SUBTRACT) stringResource(R.string.time_second) else stringResource(R.string.time_end),
                style = MaterialTheme.typography.titleMedium
            )
            HmsFieldsRow(
                hoursLabel = hLabel, minutesLabel = mLabel, secondsLabel = sLabel,
                hours = state.h2, minutes = state.m2, seconds = state.s2,
                onHoursChange = viewModel::onH2Changed,
                onMinutesChange = viewModel::onM2Changed,
                onSecondsChange = viewModel::onS2Changed
            )

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.time_result),
                    value = stringResource(
                        R.string.time_hms_format,
                        result.hours.toInt(), result.minutes.toInt(), result.seconds.toInt()
                    )
                )
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
