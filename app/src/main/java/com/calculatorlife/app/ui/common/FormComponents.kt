package com.calculatorlife.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.calculatorlife.app.data.HistoryRecorder
import kotlinx.coroutines.delay

/** A numeric input field that only lets through digits, one leading '-', and one '.'. */
@Composable
fun NumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    allowNegative: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val filtered = buildString {
                var seenDot = false
                raw.forEachIndexed { index, c ->
                    when {
                        c.isDigit() -> append(c)
                        c == '.' && !seenDot -> { append(c); seenDot = true }
                        c == '-' && allowNegative && index == 0 -> append(c)
                    }
                }
            }
            onValueChange(filtered)
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.fillMaxWidth()
    )
}

/** Three side-by-side numeric fields for Day / Month / Year entry. */
@Composable
fun DateFieldsRow(
    dayLabel: String,
    monthLabel: String,
    yearLabel: String,
    day: String,
    month: String,
    year: String,
    onDayChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        NumberField(dayLabel, day, onDayChange, modifier = Modifier.weight(1f), allowNegative = false)
        NumberField(monthLabel, month, onMonthChange, modifier = Modifier.weight(1f), allowNegative = false)
        NumberField(yearLabel, year, onYearChange, modifier = Modifier.weight(1.3f), allowNegative = false)
    }
}

/** Three side-by-side numeric fields for Hours / Minutes / Seconds entry. */
@Composable
fun HmsFieldsRow(
    hoursLabel: String,
    minutesLabel: String,
    secondsLabel: String,
    hours: String,
    minutes: String,
    seconds: String,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onSecondsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        NumberField(hoursLabel, hours, onHoursChange, modifier = Modifier.weight(1f), allowNegative = false)
        NumberField(minutesLabel, minutes, onMinutesChange, modifier = Modifier.weight(1f), allowNegative = false)
        NumberField(secondsLabel, seconds, onSecondsChange, modifier = Modifier.weight(1f), allowNegative = false)
    }
}

/**
 * Standard card used to show a calculator's computed result + a short
 * explanation line. Also the app's single History-recording hook: since
 * nearly every calculator screen renders its result through this
 * component, recording here wires History into all of them without
 * touching each screen individually. Debounced 800ms so a user typing a
 * multi-digit number doesn't create one history entry per keystroke —
 * only once the value stops changing.
 */
@Composable
fun ResultCard(title: String, value: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    val calculatorTitle = LocalCalculatorTitle.current
    val context = LocalContext.current
    LaunchedEffect(calculatorTitle, title, value, subtitle) {
        if (value.isNotBlank()) {
            delay(800)
            HistoryRecorder.record(context, calculatorTitle.ifBlank { title }, "$title: $value", subtitle)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

/** Single-choice segmented control used for calculator "mode" pickers. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onSelect(index) },
                selected = index == selectedIndex
            ) {
                Text(label)
            }
        }
    }
}
