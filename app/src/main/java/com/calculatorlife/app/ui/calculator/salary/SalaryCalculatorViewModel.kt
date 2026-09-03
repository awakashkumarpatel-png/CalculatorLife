package com.calculatorlife.app.ui.calculator.salary

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class SalaryUiState(
    val basic: String = "",
    val hra: String = "",
    val otherAllowances: String = "",
    val deductions: String = "",
    val result: SalaryResult? = null,
    val error: Boolean = false
)

class SalaryCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(SalaryUiState())
    val state: StateFlow<SalaryUiState> = _state.asStateFlow()

    fun onBasicChanged(v: String) { _state.value = _state.value.copy(basic = v); recompute() }
    fun onHraChanged(v: String) { _state.value = _state.value.copy(hra = v); recompute() }
    fun onOtherAllowancesChanged(v: String) { _state.value = _state.value.copy(otherAllowances = v); recompute() }
    fun onDeductionsChanged(v: String) { _state.value = _state.value.copy(deductions = v); recompute() }
    fun onReset() { _state.value = SalaryUiState() }

    private fun recompute() {
        val s = _state.value
        val basic = s.basic.toBigDecimalOrDefaultZero()
        val hra = s.hra.toBigDecimalOrDefaultZero()
        val other = s.otherAllowances.toBigDecimalOrDefaultZero()
        val deductions = s.deductions.toBigDecimalOrDefaultZero()
        if (s.basic.isBlank()) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = SalaryEngine.compute(basic, hra, other, deductions)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrDefaultZero(): BigDecimal =
        if (isBlank()) BigDecimal.ZERO else try { BigDecimal(this) } catch (e: NumberFormatException) { BigDecimal.ZERO }
}
