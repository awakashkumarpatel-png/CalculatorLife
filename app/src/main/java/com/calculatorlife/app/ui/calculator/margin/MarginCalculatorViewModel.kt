package com.calculatorlife.app.ui.calculator.margin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class MarginUiState(
    val mode: MarginMode = MarginMode.FROM_PRICES,
    val costPrice: String = "",
    val sellingPrice: String = "",
    val marginPercent: String = "",
    val result: MarginResult? = null,
    val error: Boolean = false
)

class MarginCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(MarginUiState())
    val state: StateFlow<MarginUiState> = _state.asStateFlow()

    fun onModeSelected(mode: MarginMode) { _state.value = _state.value.copy(mode = mode, error = false); recompute() }
    fun onCostPriceChanged(v: String) { _state.value = _state.value.copy(costPrice = v); recompute() }
    fun onSellingPriceChanged(v: String) { _state.value = _state.value.copy(sellingPrice = v); recompute() }
    fun onMarginPercentChanged(v: String) { _state.value = _state.value.copy(marginPercent = v); recompute() }
    fun onReset() { _state.value = MarginUiState() }

    private fun recompute() {
        val s = _state.value
        val cp = s.costPrice.toBigDecimalOrNull() ?: run {
            _state.value = s.copy(result = null, error = false); return
        }
        val result = when (s.mode) {
            MarginMode.FROM_PRICES -> {
                val sp = s.sellingPrice.toBigDecimalOrNull() ?: run {
                    _state.value = s.copy(result = null, error = false); return
                }
                MarginEngine.fromPrices(cp, sp)
            }
            MarginMode.FROM_MARGIN_PERCENT -> {
                val pct = s.marginPercent.toBigDecimalOrNull() ?: run {
                    _state.value = s.copy(result = null, error = false); return
                }
                MarginEngine.fromMarginPercent(cp, pct)
            }
        }
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
