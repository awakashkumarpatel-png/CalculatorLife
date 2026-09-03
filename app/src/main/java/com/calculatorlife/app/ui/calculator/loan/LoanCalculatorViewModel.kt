package com.calculatorlife.app.ui.calculator.loan

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class LoanUiState(
    val principal: String = "",
    val annualRatePercent: String = "",
    val tenureYears: String = "",
    val result: LoanResult? = null,
    val error: Boolean = false
)

/**
 * One ViewModel class backs every EMI-family screen (EMI, Loan, Home Loan,
 * Personal Loan, Car Loan) — each gets its own instance via Compose Navigation's
 * per-back-stack-entry scoping, so entering values on one doesn't affect another.
 */
class LoanCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoanUiState())
    val state: StateFlow<LoanUiState> = _state.asStateFlow()

    fun onPrincipalChanged(v: String) { _state.value = _state.value.copy(principal = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(annualRatePercent = v); recompute() }
    fun onTenureChanged(v: String) { _state.value = _state.value.copy(tenureYears = v); recompute() }
    fun onReset() { _state.value = LoanUiState() }

    private fun recompute() {
        val s = _state.value
        val principal = s.principal.toBigDecimalOrNull()
        val rate = s.annualRatePercent.toBigDecimalOrNull()
        val years = s.tenureYears.toBigDecimalOrNull()
        if (principal == null || rate == null || years == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val months = years.multiply(BigDecimal(12)).toInt()
        val result = LoanEngine.computeEmi(principal, rate, months)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
