package com.calculatorlife.app.ui.calculator.percentage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class PercentageUiState(
    val mode: PercentageMode = PercentageMode.VALUE_OF,
    val xInput: String = "",
    val yInput: String = "",
    val result: PercentageResult? = null,
    val error: Boolean = false
)

class PercentageCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(PercentageUiState())
    val state: StateFlow<PercentageUiState> = _state.asStateFlow()

    fun onModeSelected(mode: PercentageMode) {
        _state.value = _state.value.copy(mode = mode, result = null, error = false)
    }

    fun onXChanged(value: String) {
        _state.value = _state.value.copy(xInput = value)
        recompute()
    }

    fun onYChanged(value: String) {
        _state.value = _state.value.copy(yInput = value)
        recompute()
    }

    fun onReset() {
        _state.value = PercentageUiState()
    }

    private fun recompute() {
        val current = _state.value
        val x = current.xInput.toBigDecimalOrNull()
        val y = current.yInput.toBigDecimalOrNull()
        if (x == null || y == null) {
            _state.value = current.copy(result = null, error = false)
            return
        }
        val result = PercentageEngine.compute(current.mode, x, y)
        _state.value = current.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank() || this == "-" || this == ".") null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
