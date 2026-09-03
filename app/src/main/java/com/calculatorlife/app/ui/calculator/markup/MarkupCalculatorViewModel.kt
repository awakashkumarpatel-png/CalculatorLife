package com.calculatorlife.app.ui.calculator.markup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class MarkupUiState(
    val mode: MarkupMode = MarkupMode.FROM_PRICES,
    val costPrice: String = "",
    val sellingPrice: String = "",
    val markupPercent: String = "",
    val result: MarkupResult? = null,
    val error: Boolean = false
)

class MarkupCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(MarkupUiState())
    val state: StateFlow<MarkupUiState> = _state.asStateFlow()

    fun onModeSelected(mode: MarkupMode) { _state.value = _state.value.copy(mode = mode, error = false); recompute() }
    fun onCostPriceChanged(v: String) { _state.value = _state.value.copy(costPrice = v); recompute() }
    fun onSellingPriceChanged(v: String) { _state.value = _state.value.copy(sellingPrice = v); recompute() }
    fun onMarkupPercentChanged(v: String) { _state.value = _state.value.copy(markupPercent = v); recompute() }
    fun onReset() { _state.value = MarkupUiState() }

    private fun recompute() {
        val s = _state.value
        val cp = s.costPrice.toBigDecimalOrNull() ?: run {
            _state.value = s.copy(result = null, error = false); return
        }
        val result = when (s.mode) {
            MarkupMode.FROM_PRICES -> {
                val sp = s.sellingPrice.toBigDecimalOrNull() ?: run {
                    _state.value = s.copy(result = null, error = false); return
                }
                MarkupEngine.fromPrices(cp, sp)
            }
            MarkupMode.FROM_MARKUP_PERCENT -> {
                val pct = s.markupPercent.toBigDecimalOrNull() ?: run {
                    _state.value = s.copy(result = null, error = false); return
                }
                MarkupEngine.fromMarkupPercent(cp, pct)
            }
        }
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
