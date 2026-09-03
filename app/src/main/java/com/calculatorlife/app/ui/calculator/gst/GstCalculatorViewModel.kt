package com.calculatorlife.app.ui.calculator.gst

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

data class GstUiState(
    val mode: GstMode = GstMode.ADD_GST,
    val amount: String = "",
    val ratePercent: String = "",
    val result: GstResult? = null,
    val error: Boolean = false
)

class GstCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(GstUiState())
    val state: StateFlow<GstUiState> = _state.asStateFlow()

    fun onModeSelected(mode: GstMode) { _state.value = _state.value.copy(mode = mode); recompute() }
    fun onAmountChanged(v: String) { _state.value = _state.value.copy(amount = v); recompute() }
    fun onRateChanged(v: String) { _state.value = _state.value.copy(ratePercent = v); recompute() }
    fun onReset() { _state.value = GstUiState() }

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
