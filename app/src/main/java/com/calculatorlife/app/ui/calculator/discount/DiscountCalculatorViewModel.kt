package com.calculatorlife.app.ui.calculator.discount

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class DiscountUiState(
    val mode: DiscountMode = DiscountMode.BY_PERCENT,
    val mrp: String = "",
    val discountPercent: String = "",
    val finalPrice: String = "",
    val result: DiscountResult? = null,
    val error: Boolean = false
)

class DiscountCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(DiscountUiState())
    val state: StateFlow<DiscountUiState> = _state.asStateFlow()

    fun onModeSelected(mode: DiscountMode) { _state.value = _state.value.copy(mode = mode, error = false); recompute() }
    fun onMrpChanged(v: String) { _state.value = _state.value.copy(mrp = v); recompute() }
    fun onDiscountPercentChanged(v: String) { _state.value = _state.value.copy(discountPercent = v); recompute() }
    fun onFinalPriceChanged(v: String) { _state.value = _state.value.copy(finalPrice = v); recompute() }
    fun onReset() { _state.value = DiscountUiState() }

    private fun recompute() {
        val s = _state.value
        val mrp = s.mrp.toBigDecimalOrNull()
        if (mrp == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = when (s.mode) {
            DiscountMode.BY_PERCENT -> {
                val pct = s.discountPercent.toBigDecimalOrNull() ?: run {
                    _state.value = s.copy(result = null, error = false); return
                }
                DiscountEngine.computeByPercent(mrp, pct)
            }
            DiscountMode.BY_FINAL_PRICE -> {
                val finalPrice = s.finalPrice.toBigDecimalOrNull() ?: run {
                    _state.value = s.copy(result = null, error = false); return
                }
                DiscountEngine.computeByFinalPrice(mrp, finalPrice)
            }
        }
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
