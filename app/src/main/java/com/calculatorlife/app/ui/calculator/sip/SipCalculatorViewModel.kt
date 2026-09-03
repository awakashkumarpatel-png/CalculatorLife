package com.calculatorlife.app.ui.calculator.sip

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class SipUiState(
    val monthlyAmount: String = "",
    val annualRatePercent: String = "",
    val tenureYears: String = "",
    val result: SipResult? = null,
    val error: Boolean = false
)

class SipCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(SipUiState())
    val state: StateFlow<SipUiState> = _state.asStateFlow()

    fun onAmountChanged(v: String) { _state.value = _state.value.copy(monthlyAmount = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(annualRatePercent = v); recompute() }
    fun onTenureChanged(v: String) { _state.value = _state.value.copy(tenureYears = v); recompute() }
    fun onReset() { _state.value = SipUiState() }

    private fun recompute() {
        val s = _state.value
        val amount = s.monthlyAmount.toBigDecimalOrNull()
        val rate = s.annualRatePercent.toBigDecimalOrNull()
        val years = s.tenureYears.toBigDecimalOrNull()
        if (amount == null || rate == null || years == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val months = years.multiply(BigDecimal(12)).toInt()
        val result = SipEngine.compute(amount, rate, months)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
