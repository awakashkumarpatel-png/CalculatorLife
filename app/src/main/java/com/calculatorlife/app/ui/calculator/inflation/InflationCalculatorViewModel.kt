package com.calculatorlife.app.ui.calculator.inflation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class InflationUiState(
    val mode: InflationMode = InflationMode.FUTURE_COST,
    val amount: String = "",
    val ratePercent: String = "",
    val years: String = "",
    val result: InflationResult? = null,
    val error: Boolean = false
)

class InflationCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(InflationUiState())
    val state: StateFlow<InflationUiState> = _state.asStateFlow()

    fun onModeSelected(mode: InflationMode) { _state.value = _state.value.copy(mode = mode); recompute() }
    fun onAmountChanged(v: String) { _state.value = _state.value.copy(amount = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(ratePercent = v); recompute() }
    fun onYearsChanged(v: String) { _state.value = _state.value.copy(years = v); recompute() }
    fun onReset() { _state.value = InflationUiState() }

    private fun recompute() {
        val s = _state.value
        val amount = s.amount.toBigDecimalOrNull()
        val rate = s.ratePercent.toBigDecimalOrNull()
        val years = s.years.toIntOrNull()
        if (amount == null || rate == null || years == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = when (s.mode) {
            InflationMode.FUTURE_COST -> InflationEngine.futureCost(amount, rate, years)
            InflationMode.REAL_VALUE -> InflationEngine.realValue(amount, rate, years)
        }
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
