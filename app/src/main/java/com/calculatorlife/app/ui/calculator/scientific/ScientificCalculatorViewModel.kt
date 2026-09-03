package com.calculatorlife.app.ui.calculator.scientific

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class ScientificUiState(
    val expression: String = "",
    val livePreview: String = "",
    val expressionPreview: String = "",
    val angleMode: AngleMode = AngleMode.DEGREES,
    val isError: Boolean = false,
    val justEvaluated: Boolean = false
)

class ScientificCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(ScientificUiState())
    val state: StateFlow<ScientificUiState> = _state.asStateFlow()

    fun onInsert(token: String) {
        val s = _state.value
        val startsFresh = s.justEvaluated && (token[0].isDigit() || token == ".")
        val base = if (startsFresh) "" else s.expression
        val preview = if (startsFresh) "" else s.expressionPreview
        _state.value = s.copy(expression = base + token, expressionPreview = preview, isError = false, justEvaluated = false)
        updateLivePreview()
    }

    fun onClear() { _state.value = ScientificUiState(angleMode = _state.value.angleMode) }

    fun onBackspace() {
        val s = _state.value
        if (s.justEvaluated) { onClear(); return }
        _state.value = s.copy(expression = s.expression.dropLast(1), isError = false)
        updateLivePreview()
    }

    fun onToggleAngleMode() {
        _state.value = _state.value.copy(
            angleMode = if (_state.value.angleMode == AngleMode.DEGREES) AngleMode.RADIANS else AngleMode.DEGREES
        )
        updateLivePreview()
    }

    fun onEquals() {
        val s = _state.value
        if (s.expression.isBlank()) return
        try {
            val evaluator = ExpressionEvaluator(s.angleMode)
            val result = evaluator.evaluate(s.expression)
            _state.value = s.copy(
                expression = formatResult(result),
                expressionPreview = "${s.expression} =",
                livePreview = "",
                isError = false,
                justEvaluated = true
            )
        } catch (e: Exception) {
            _state.value = s.copy(isError = true, livePreview = "")
        }
    }

    private fun updateLivePreview() {
        val s = _state.value
        val preview = try {
            formatResult(ExpressionEvaluator(s.angleMode).evaluate(s.expression))
        } catch (e: Exception) {
            ""
        }
        _state.value = _state.value.copy(livePreview = preview)
    }

    private fun formatResult(value: Double): String {
        val bd = BigDecimal(value, MathContext(10, RoundingMode.HALF_UP))
        return bd.stripTrailingZeros().toPlainString()
    }
}
