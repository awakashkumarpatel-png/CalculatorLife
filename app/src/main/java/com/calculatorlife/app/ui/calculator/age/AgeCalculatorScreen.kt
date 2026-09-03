package com.calculatorlife.app.ui.calculator.age

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculatorlife.app.R
import com.calculatorlife.app.ui.common.CalculatorScaffold
import com.calculatorlife.app.ui.common.DateFieldsRow
import com.calculatorlife.app.ui.common.ResultCard

@Composable
fun AgeCalculatorScreen(
    onOpenMenu: () -> Unit,
    viewModel: AgeCalculatorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    CalculatorScaffold(title = stringResource(R.string.calc_age), onOpenMenu = onOpenMenu) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.age_birth_date), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            DateFieldsRow(
                dayLabel = stringResource(R.string.date_day),
                monthLabel = stringResource(R.string.date_month),
                yearLabel = stringResource(R.string.date_year),
                day = state.day, month = state.month, year = state.year,
                onDayChange = viewModel::onDayChanged,
                onMonthChange = viewModel::onMonthChanged,
                onYearChange = viewModel::onYearChanged
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.age_use_today))
                Switch(checked = state.useToday, onCheckedChange = viewModel::onUseTodayChanged)
            }

            if (!state.useToday) {
                Text(stringResource(R.string.age_as_of_date), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                DateFieldsRow(
                    dayLabel = stringResource(R.string.date_day),
                    monthLabel = stringResource(R.string.date_month),
                    yearLabel = stringResource(R.string.date_year),
                    day = state.asOfDay, month = state.asOfMonth, year = state.asOfYear,
                    onDayChange = viewModel::onAsOfDayChanged,
                    onMonthChange = viewModel::onAsOfMonthChanged,
                    onYearChange = viewModel::onAsOfYearChanged
                )
            }

            state.result?.let { result ->
                ResultCard(
                    title = stringResource(R.string.age_result),
                    value = stringResource(R.string.age_years_months_days, result.years, result.months, result.days),
                    subtitle = "${stringResource(R.string.age_total_days)}: ${result.totalDays} · " +
                        "${stringResource(R.string.age_total_weeks)}: ${result.totalWeeks} · " +
                        "${stringResource(R.string.age_total_months)}: ${result.totalMonths}"
                )
            }
            if (state.invalidDate) {
                Text(text = stringResource(R.string.error_invalid_date))
            }

            OutlinedButton(onClick = viewModel::onReset) {
                Text(stringResource(R.string.action_reset))
            }
        }
    }
}
