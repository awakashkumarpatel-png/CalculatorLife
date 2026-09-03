package com.calculatorlife.app.ui.calculator.tax

import androidx.lifecycle.ViewModel
import com.calculatorlife.app.ui.calculator.gst.GstEngine
import com.calculatorlife.app.ui.calculator.gst.GstMode
import com.calculatorlife.app.ui.calculator.gst.GstResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class TaxUiState(
    val mode: GstMode = GstMode.ADD_GST,
    val amount: String = "",
    val ratePercent: String = "",
    val result: GstResult? = null,
    val error: Boolean = false
)

/**
 * Generic business tax — add a tax rate to an amount, or extract it from a
 * tax-inclusive total. Same math as the GST calculator (`GstEngine`), just
 * presented with generic labels instead of GST-specific ones, so the
 * formula isn't duplicated.
 */
class TaxCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(TaxUiState())
    val state: StateFlow<TaxUiState> = _state.asStateFlow()

    fun onModeSelected(mode: GstMode) { _state.value = _state.value.copy(mode = mode); recompute() }
    fun onAmountChanged(v: String) { _state.value = _state.value.copy(amount = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(ratePercent = v); recompute() }
    fun onReset() { _state.value = TaxUiState() }

    private fun recompute() {
        val s = _state.value
        val amount = s.amount.toBigDecimalOrNull()
        val rate = s.ratePercent.toBigDecimalOrNull()
        if (amount == null || rate == null) {
            _state.value = s.copy(result = null, error = false)
            return
        }
        val result = GstEngine.compute(s.mode, amount, rate)
        _state.value = s.copy(result = result, error = result == null)
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank()) null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
