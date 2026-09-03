package com.calculatorlife.app.ui.calculator.commission

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class CommissionUiState(
    val saleAmount: String = "",
    val commissionPercent: String = "",
    val result: CommissionResult? = null,
    val error: Boolean = false
)

class CommissionCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(CommissionUiState())
    val state: StateFlow<CommissionUiState> = _state.asStateFlow()

    fun onSaleAmountChanged(v: String) { _state.value = _state.value.copy(saleAmount = v); recompute() }
    fun onCommissionPercentChanged(v: String) { _state.value = _state.value.copy(commissionPercent = v); recompute() }
    fun onReset() { _state.value = CommissionUiState() }

    private fun recompute() {
        val s = _state.value
        val sale = s.saleAmount.toBigDecimalOrNull()
        val pct = s.commissionPercent.toBigDecimalOrNull()
        if (sale == null || pct == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = CommissionEngine.compute(sale, pct)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
