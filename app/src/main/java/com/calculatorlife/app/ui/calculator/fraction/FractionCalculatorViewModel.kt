package com.calculatorlife.app.ui.calculator.fraction

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigInteger

data class FractionUiState(
    val num1: String = "",
    val den1: String = "",
    val num2: String = "",
    val den2: String = "",
    val operator: FractionOperator = FractionOperator.ADD,
    val result: Fraction? = null,
    val error: Boolean = false
)

class FractionCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(FractionUiState())
    val state: StateFlow<FractionUiState> = _state.asStateFlow()

    fun onNum1Changed(v: String) { _state.value = _state.value.copy(num1 = v); recompute() }
    fun onDen1Changed(v: String) { _state.value = _state.value.copy(den1 = v); recompute() }
    fun onNum2Changed(v: String) { _state.value = _state.value.copy(num2 = v); recompute() }
    fun onDen2Changed(v: String) { _state.value = _state.value.copy(den2 = v); recompute() }
    fun onOperatorSelected(op: FractionOperator) { _state.value = _state.value.copy(operator = op); recompute() }
    fun onReset() { _state.value = FractionUiState() }

    private fun recompute() {
        val s = _state.value
        val n1 = s.num1.toBigIntegerOrNull()
        val d1 = s.den1.toBigIntegerOrNull()
        val n2 = s.num2.toBigIntegerOrNull()
        val d2 = s.den2.toBigIntegerOrNull()
        if (n1 == null || d1 == null || n2 == null || d2 == null || d1.signum() == 0 || d2.signum() == 0) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = FractionEngine.compute(Fraction(n1, d1), s.operator, Fraction(n2, d2))
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigIntegerOrNull(): BigInteger? =
        if (isBlank() || this == "-") null else try { BigInteger(this) } catch (e: NumberFormatException) { null }
}
