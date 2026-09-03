package com.calculatorlife.app.ui.calculator.fd

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class FdUiState(
    val principal: String = "",
    val annualRatePercent: String = "",
    val tenureYears: String = "",
    val frequency: CompoundingFrequency = CompoundingFrequency.QUARTERLY,
    val result: FdResult? = null,
    val error: Boolean = false
)

class FdCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(FdUiState())
    val state: StateFlow<FdUiState> = _state.asStateFlow()

    fun onPrincipalChanged(v: String) { _state.value = _state.value.copy(principal = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(annualRatePercent = v); recompute() }
    fun onTenureChanged(v: String) { _state.value = _state.value.copy(tenureYears = v); recompute() }
    fun onFrequencySelected(f: CompoundingFrequency) { _state.value = _state.value.copy(frequency = f); recompute() }
    fun onReset() { _state.value = FdUiState() }

    private fun recompute() {
        val s = _state.value
        val principal = s.principal.toBigDecimalOrNull()
        val rate = s.annualRatePercent.toBigDecimalOrNull()
        val years = s.tenureYears.toBigDecimalOrNull()
        if (principal == null || rate == null || years == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = FdEngine.compute(principal, rate, years, s.frequency)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
