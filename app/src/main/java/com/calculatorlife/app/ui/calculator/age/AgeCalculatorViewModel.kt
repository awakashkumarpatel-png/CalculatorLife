package com.calculatorlife.app.ui.calculator.age

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class AgeUiState(
    val day: String = "",
    val month: String = "",
    val year: String = "",
    val useToday: Boolean = true,
    val asOfDay: String = "",
    val asOfMonth: String = "",
    val asOfYear: String = "",
    val result: AgeResult? = null,
    val invalidDate: Boolean = false
)

class AgeCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(AgeUiState())
    val state: StateFlow<AgeUiState> = _state.asStateFlow()

    fun onDayChanged(v: String) { _state.value = _state.value.copy(day = v); recompute() }
    fun onMonthChanged(v: String) { _state.value = _state.value.copy(month = v); recompute() }
    fun onYearChanged(v: String) { _state.value = _state.value.copy(year = v); recompute() }
    fun onUseTodayChanged(v: Boolean) { _state.value = _state.value.copy(useToday = v); recompute() }
    fun onAsOfDayChanged(v: String) { _state.value = _state.value.copy(asOfDay = v); recompute() }
    fun onAsOfMonthChanged(v: String) { _state.value = _state.value.copy(asOfMonth = v); recompute() }
    fun onAsOfYearChanged(v: String) { _state.value = _state.value.copy(asOfYear = v); recompute() }
    fun onReset() { _state.value = AgeUiState() }

    private fun recompute() {
        val s = _state.value
        val birth = toDate(s.day, s.month, s.year)
        val asOf = if (s.useToday) LocalDate.now() else toDate(s.asOfDay, s.asOfMonth, s.asOfYear)
        if (birth == null || asOf == null) {
            _state.value = s.copy(result = null, invalidDate = false)
            return
        }
        val result = AgeEngine.compute(birth, asOf)
        _state.value = s.copy(result = result, invalidDate = result == null)
    }

    private fun toDate(day: String, month: String, year: String): LocalDate? {
        val d = day.toIntOrNull() ?: return null
        val m = month.toIntOrNull() ?: return null
        val y = year.toIntOrNull() ?: return null
        return AgeEngine.toLocalDateOrNull(d, m, y)
    }
}
