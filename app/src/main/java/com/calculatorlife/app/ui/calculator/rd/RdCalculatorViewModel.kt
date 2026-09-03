package com.calculatorlife.app.ui.calculator.rd

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class RdUiState(
    val monthlyDeposit: String = "",
    val annualRatePercent: String = "",
    val tenureMonths: String = "",
    val result: RdResult? = null,
    val error: Boolean = false
)

class RdCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(RdUiState())
    val state: StateFlow<RdUiState> = _state.asStateFlow()

    fun onDepositChanged(v: String) { _state.value = _state.value.copy(monthlyDeposit = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(annualRatePercent = v); recompute() }
    fun onTenureChanged(v: String) { _state.value = _state.value.copy(tenureMonths = v); recompute() }
    fun onReset() { _state.value = RdUiState() }

    private fun recompute() {
        val s = _state.value
        val deposit = s.monthlyDeposit.toBigDecimalOrNull()
        val rate = s.annualRatePercent.toBigDecimalOrNull()
        val months = s.tenureMonths.toIntOrNull()
        if (deposit == null || rate == null || months == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = RdEngine.compute(deposit, rate, months)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
