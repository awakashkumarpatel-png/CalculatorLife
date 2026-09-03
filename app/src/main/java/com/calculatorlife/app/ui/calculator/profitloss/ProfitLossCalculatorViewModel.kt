package com.calculatorlife.app.ui.calculator.profitloss

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class ProfitLossUiState(
    val costPrice: String = "",
    val sellingPrice: String = "",
    val result: ProfitLossResult? = null,
    val error: Boolean = false
)

class ProfitLossCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProfitLossUiState())
    val state: StateFlow<ProfitLossUiState> = _state.asStateFlow()

    fun onCostPriceChanged(v: String) { _state.value = _state.value.copy(costPrice = v); recompute() }
    fun onSellingPriceChanged(v: String) { _state.value = _state.value.copy(sellingPrice = v); recompute() }
    fun onReset() { _state.value = ProfitLossUiState() }

    private fun recompute() {
        val s = _state.value
        val cp = s.costPrice.toBigDecimalOrNull()
        val sp = s.sellingPrice.toBigDecimalOrNull()
        if (cp == null || sp == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = ProfitLossEngine.compute(cp, sp)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
