package com.calculatorlife.app.ui.calculator.ratio

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.math.BigInteger

enum class RatioMode { SIMPLIFY, SOLVE_PROPORTION }

data class RatioUiState(
    val mode: RatioMode = RatioMode.SIMPLIFY,
    val a: String = "",
    val b: String = "",
    val c: String = "",
    val simplified: SimplifiedRatio? = null,
    val solvedX: BigDecimal? = null,
    val error: Boolean = false
)

class RatioCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(RatioUiState())
    val state: StateFlow<RatioUiState> = _state.asStateFlow()

    fun onModeSelected(mode: RatioMode) { _state.value = _state.value.copy(mode = mode, error = false); recompute() }
    fun onAChanged(v: String) { _state.value = _state.value.copy(a = v); recompute() }
    fun onBChanged(v: String) { _state.value = _state.value.copy(b = v); recompute() }
    fun onCChanged(v: String) { _state.value = _state.value.copy(c = v); recompute() }
    fun onReset() { _state.value = RatioUiState() }

    private fun recompute() {
        val s = _state.value
        when (s.mode) {
            RatioMode.SIMPLIFY -> {
                val a = s.a.toBigIntegerOrNull()
                val b = s.b.toBigIntegerOrNull()
                if (a == null || b == null) {
                    _state.value = s.copy(simplified = null, error = false)
                    return
                }
                val result = RatioEngine.simplify(a, b)
                _state.value = s.copy(simplified = result, solvedX = null, error = result == null)
            }
            RatioMode.SOLVE_PROPORTION -> {
                val a = s.a.toBigDecimalOrNull()
                val b = s.b.toBigDecimalOrNull()
                val c = s.c.toBigDecimalOrNull()
                if (a == null || b == null || c == null) {
                    _state.value = s.copy(solvedX = null, error = false)
                    return
                }
                val result = RatioEngine.solveProportion(a, b, c)
                _state.value = s.copy(solvedX = result, simplified = null, error = result == null)
            }
        }
    }

    private fun String.toBigIntegerOrNull(): BigInteger? =
        if (isBlank() || this == "-") null else try { BigInteger(this) } catch (e: NumberFormatException) { null }

    private fun String.toBigDecimalOrNull(): BigDecimal? =
        if (isBlank() || this == "-" || this == ".") null else try { BigDecimal(this) } catch (e: NumberFormatException) { null }
}
