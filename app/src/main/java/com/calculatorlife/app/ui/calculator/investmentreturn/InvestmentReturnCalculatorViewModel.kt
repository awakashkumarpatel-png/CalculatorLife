package com.calculatorlife.app.ui.calculator.investmentreturn

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class InvestmentReturnUiState(
    val initialValue: String = "",
    val finalValue: String = "",
    val years: String = "",
    val result: InvestmentReturnResult? = null,
    val error: Boolean = false
)

class InvestmentReturnCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(InvestmentReturnUiState())
    val state: StateFlow<InvestmentReturnUiState> = _state.asStateFlow()

    fun onInitialChanged(v: String) { _state.value = _state.value.copy(initialValue = v); recompute() }
    fun onFinalChanged(v: String) { _state.value = _state.value.copy(finalValue = v); recompute() }
    fun onYearsChanged(v: String) { _state.value = _state.value.copy(years = v); recompute() }
    fun onReset() { _state.value = InvestmentReturnUiState() }

    private fun recompute() {
        val s = _state.value
        val initial = s.initialValue.toBigDecimalOrNull()
        val final = s.finalValue.toBigDecimalOrNull()
        val years = s.years.toBigDecimalOrNull()
        if (initial == null || final == null || years == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = InvestmentReturnEngine.compute(initial, final, years)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
