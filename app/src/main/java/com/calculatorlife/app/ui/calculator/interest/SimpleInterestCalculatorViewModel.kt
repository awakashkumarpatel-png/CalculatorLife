package com.calculatorlife.app.ui.calculator.interest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class SimpleInterestUiState(
    val principal: String = "",
    val annualRatePercent: String = "",
    val years: String = "",
    val result: SimpleInterestResult? = null,
    val error: Boolean = false
)

class SimpleInterestCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(SimpleInterestUiState())
    val state: StateFlow<SimpleInterestUiState> = _state.asStateFlow()

    fun onPrincipalChanged(v: String) { _state.value = _state.value.copy(principal = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(annualRatePercent = v); recompute() }
    fun onYearsChanged(v: String) { _state.value = _state.value.copy(years = v); recompute() }
    fun onReset() { _state.value = SimpleInterestUiState() }

    private fun recompute() {
        val s = _state.value
        val principal = s.principal.toBigDecimalOrNull()
        val rate = s.annualRatePercent.toBigDecimalOrNull()
        val years = s.years.toBigDecimalOrNull()
        if (principal == null || rate == null || years == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = SimpleInterestEngine.compute(principal, rate, years)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
