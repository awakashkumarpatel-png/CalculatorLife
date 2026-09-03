package com.calculatorlife.app.ui.calculator.breakeven

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class BreakEvenUiState(
    val fixedCosts: String = "",
    val pricePerUnit: String = "",
    val variableCostPerUnit: String = "",
    val result: BreakEvenResult? = null,
    val error: Boolean = false
)

class BreakEvenCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(BreakEvenUiState())
    val state: StateFlow<BreakEvenUiState> = _state.asStateFlow()

    fun onFixedCostsChanged(v: String) { _state.value = _state.value.copy(fixedCosts = v); recompute() }
    fun onPriceChanged(v: String) { _state.value = _state.value.copy(pricePerUnit = v); recompute() }
    fun onVariableCostChanged(v: String) { _state.value = _state.value.copy(variableCostPerUnit = v); recompute() }
    fun onReset() { _state.value = BreakEvenUiState() }

    private fun recompute() {
        val s = _state.value
        val fixed = s.fixedCosts.toBigDecimalOrNull()
        val price = s.pricePerUnit.toBigDecimalOrNull()
        val variableCost = s.variableCostPerUnit.toBigDecimalOrNull()
        if (fixed == null || price == null || variableCost == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = BreakEvenEngine.compute(fixed, price, variableCost)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
