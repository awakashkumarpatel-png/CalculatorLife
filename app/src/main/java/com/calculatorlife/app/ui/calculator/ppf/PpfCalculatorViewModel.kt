package com.calculatorlife.app.ui.calculator.ppf

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class PpfUiState(
    val annualDeposit: String = "",
    val annualRatePercent: String = "",
    val tenureYears: String = "",
    val result: PpfResult? = null,
    val error: Boolean = false
)

class PpfCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(PpfUiState())
    val state: StateFlow<PpfUiState> = _state.asStateFlow()

    fun onDepositChanged(v: String) { _state.value = _state.value.copy(annualDeposit = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(annualRatePercent = v); recompute() }
    fun onTenureChanged(v: String) { _state.value = _state.value.copy(tenureYears = v); recompute() }
    fun onReset() { _state.value = PpfUiState() }

    private fun recompute() {
        val s = _state.value
        val deposit = s.annualDeposit.toBigDecimalOrNull()
        val rate = s.annualRatePercent.toBigDecimalOrNull()
        val years = s.tenureYears.toIntOrNull()
        if (deposit == null || rate == null || years == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = PpfEngine.compute(deposit, rate, years)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
