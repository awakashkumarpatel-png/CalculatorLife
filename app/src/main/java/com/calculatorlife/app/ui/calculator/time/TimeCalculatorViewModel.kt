package com.calculatorlife.app.ui.calculator.time

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime

enum class TimeMode { ADD_SUBTRACT, DIFFERENCE }

data class TimeUiState(
    val mode: TimeMode = TimeMode.ADD_SUBTRACT,
    val h1: String = "", val m1: String = "", val s1: String = "",
    val h2: String = "", val m2: String = "", val s2: String = "",
    val operator: TimeOperator = TimeOperator.ADD,
    val result: DurationBreakdown? = null,
    val invalid: Boolean = false
)

class TimeCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimeUiState())
    val state: StateFlow<TimeUiState> = _state.asStateFlow()

    fun onModeSelected(mode: TimeMode) { _state.value = _state.value.copy(mode = mode); recompute() }
    fun onOperatorSelected(op: TimeOperator) { _state.value = _state.value.copy(operator = op); recompute() }
    fun onH1Changed(v: String) { _state.value = _state.value.copy(h1 = v); recompute() }
    fun onM1Changed(v: String) { _state.value = _state.value.copy(m1 = v); recompute() }
    fun onS1Changed(v: String) { _state.value = _state.value.copy(s1 = v); recompute() }
    fun onH2Changed(v: String) { _state.value = _state.value.copy(h2 = v); recompute() }
    fun onM2Changed(v: String) { _state.value = _state.value.copy(m2 = v); recompute() }
    fun onS2Changed(v: String) { _state.value = _state.value.copy(s2 = v); recompute() }
    fun onReset() { _state.value = TimeUiState() }

    private fun recompute() {
        val s = _state.value
        when (s.mode) {
            TimeMode.ADD_SUBTRACT -> {
                val h1 = s.h1.toLongOrNull() ?: 0L
                val m1 = s.m1.toLongOrNull() ?: 0L
                val sec1 = s.s1.toLongOrNull() ?: 0L
                val h2 = s.h2.toLongOrNull() ?: 0L
                val m2 = s.m2.toLongOrNull() ?: 0L
                val sec2 = s.s2.toLongOrNull() ?: 0L
                val result = TimeEngine.addOrSubtract(h1, m1, sec1, s.operator, h2, m2, sec2)
                _state.value = s.copy(result = result, invalid = false)
            }
            TimeMode.DIFFERENCE -> {
                val start = toTime(s.h1, s.m1, s.s1)
                val end = toTime(s.h2, s.m2, s.s2)
                if (start == null || end == null) {
                    _state.value = s.copy(result = null, invalid = false)
                    return
                }
                _state.value = s.copy(result = TimeEngine.difference(start, end), invalid = false)
            }
        }
    }

    private fun toTime(h: String, m: String, sec: String): LocalTime? {
        val hh = h.toIntOrNull() ?: return null
        val mm = m.toIntOrNull() ?: return null
        val ss = sec.toIntOrNull() ?: 0
        return try { LocalTime.of(hh, mm, ss) } catch (e: Exception) { null }
    }
}
