package com.calculatorlife.app.ui.calculator.datediff

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class DateDifferenceUiState(
    val startDay: String = "", val startMonth: String = "", val startYear: String = "",
    val endDay: String = "", val endMonth: String = "", val endYear: String = "",
    val result: DateDifferenceResult? = null,
    val invalidDate: Boolean = false
)

class DateDifferenceCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(DateDifferenceUiState())
    val state: StateFlow<DateDifferenceUiState> = _state.asStateFlow()

    fun onStartDayChanged(v: String) { _state.value = _state.value.copy(startDay = v); recompute() }
    fun onStartMonthChanged(v: String) { _state.value = _state.value.copy(startMonth = v); recompute() }
    fun onStartYearChanged(v: String) { _state.value = _state.value.copy(startYear = v); recompute() }
    fun onEndDayChanged(v: String) { _state.value = _state.value.copy(endDay = v); recompute() }
    fun onEndMonthChanged(v: String) { _state.value = _state.value.copy(endMonth = v); recompute() }
    fun onEndYearChanged(v: String) { _state.value = _state.value.copy(endYear = v); recompute() }
    fun onReset() { _state.value = DateDifferenceUiState() }

    private fun recompute() {
        val s = _state.value
        val start = toDate(s.startDay, s.startMonth, s.startYear)
        val end = toDate(s.endDay, s.endMonth, s.endYear)
        if (start == null || end == null) {
            _state.value = s.copy(result = null, invalidDate = false)
            return
        }
        _state.value = s.copy(result = DateDifferenceEngine.compute(start, end), invalidDate = false)
    }

    private fun toDate(day: String, month: String, year: String): LocalDate? {
        val d = day.toIntOrNull() ?: return null
        val m = month.toIntOrNull() ?: return null
        val y = year.toIntOrNull() ?: return null
        return DateDifferenceEngine.toLocalDateOrNull(d, m, y)
    }
}
