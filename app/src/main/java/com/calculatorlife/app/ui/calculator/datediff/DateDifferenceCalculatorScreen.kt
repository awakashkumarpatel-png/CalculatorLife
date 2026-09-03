package com.calculatorlife.app.ui.calculator.datediff

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
import com.calculatorlife.app.ui.common.DateFieldsRow
import com.calculatorlife.app.ui.common.ResultCard

@Composable
fun DateDifferenceCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: DateDifferenceCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val dayLabel = stringResource(R.string.date_day)
    val monthLabel = stringResource(R.string.date_month)
    val yearLabel = stringResource(R.string.date_year)

    CalculatorScaffold(title = stringResource(R.string.calc_date_difference), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.datediff_start_date), style = MaterialTheme.typography.titleMedium)
            DateFieldsRow(
                dayLabel = dayLabel, monthLabel = monthLabel, yearLabel = yearLabel,
                day = state.startDay, month = state.startMonth, year = state.startYear,
                onDayChange = viewModel::onStartDayChanged,
                onMonthChange = viewModel::onStartMonthChanged,
                onYearChange = viewModel::onStartYearChanged
            )

            Text(stringResource(R.string.datediff_end_date), style = MaterialTheme.typography.titleMedium)
            DateFieldsRow(
                dayLabel = dayLabel, monthLabel = monthLabel, yearLabel = yearLabel,
                day = state.endDay, month = state.endMonth, year = state.endYear,
                onDayChange = viewModel::onEndDayChanged,
                onMonthChange = viewModel::onEndMonthChanged,
                onYearChange = viewModel::onEndYearChanged
            )

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.datediff_result),
                    value = stringResource(R.string.age_years_months_days, result.years, result.months, result.days),
                    subtitle = "${stringResource(R.string.age_total_days)}: ${result.totalDays} · " +
                        "${stringResource(R.string.age_total_weeks)}: ${result.totalWeeks}"
                )
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
