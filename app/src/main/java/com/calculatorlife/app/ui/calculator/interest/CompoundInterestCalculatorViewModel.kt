package com.calculatorlife.app.ui.calculator.interest

import androidx.lifecycle.ViewModel
import com.calculatorlife.app.ui.calculator.fd.CompoundingFrequency
import com.calculatorlife.app.ui.calculator.fd.FdEngine
import com.calculatorlife.app.ui.calculator.fd.FdResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class CompoundInterestUiState(
    val principal: String = "",
    val annualRatePercent: String = "",
    val years: String = "",
    val frequency: CompoundingFrequency = CompoundingFrequency.ANNUALLY,
    val result: FdResult? = null,
    val error: Boolean = false
)

/**
 * Generic compound interest — same math as the FD calculator
 * (`FdEngine`), just presented with generic Principal/Time labels instead
 * of deposit-specific ones. Reusing the engine avoids maintaining two
 * copies of the same formula.
 */
class CompoundInterestCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(CompoundInterestUiState())
    val state: StateFlow<CompoundInterestUiState> = _state.asStateFlow()

    fun onPrincipalChanged(v: String) { _state.value = _state.value.copy(principal = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(annualRatePercent = v); recompute() }
    fun onYearsChanged(v: String) { _state.value = _state.value.copy(years = v); recompute() }
    fun onFrequencySelected(f: CompoundingFrequency) { _state.value = _state.value.copy(frequency = f); recompute() }
    fun onReset() { _state.value = CompoundInterestUiState() }

    private fun recompute() {
        val s = _state.value
        val principal = s.principal.toBigDecimalOrNull()
        val rate = s.annualRatePercent.toBigDecimalOrNull()
        val years = s.years.toBigDecimalOrNull()
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
