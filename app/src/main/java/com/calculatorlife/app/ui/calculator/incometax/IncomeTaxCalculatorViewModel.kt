package com.calculatorlife.app.ui.calculator.incometax

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class IncomeTaxUiState(
    val annualIncome: String = "",
    val result: IncomeTaxResult? = null,
    val error: Boolean = false
)

class IncomeTaxCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(IncomeTaxUiState())
    val state: StateFlow<IncomeTaxUiState> = _state.asStateFlow()

    fun onIncomeChanged(v: String) { _state.value = _state.value.copy(annualIncome = v); recompute() }
    fun onReset() { _state.value = IncomeTaxUiState() }

    private fun recompute() {
        val s = _state.value
        val income = s.annualIncome.toBigDecimalOrNull()
        if (income == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = IncomeTaxEngine.compute(income)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
