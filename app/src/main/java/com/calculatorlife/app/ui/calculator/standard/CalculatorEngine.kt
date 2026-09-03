package com.calculatorlife.app.ui.calculator.standard

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class Operator(val symbol: String) {
    ADD("+"), SUBTRACT("−"), MULTIPLY("×"), DIVIDE("÷")
}

sealed class CalculatorEvent {
    data class Digit(val digit: Char) : CalculatorEvent()
    data object Decimal : CalculatorEvent()
    data class Op(val operator: Operator) : CalculatorEvent()
    data object Equals : CalculatorEvent()
    data object Clear : CalculatorEvent()
    data object Backspace : CalculatorEvent()
    data object ToggleSign : CalculatorEvent()
    data object Percent : CalculatorEvent()
}

data class CalculatorState(
    val currentInput: String = "0",
    val previousValue: BigDecimal? = null,
    val pendingOperator: Operator? = null,
    val expressionPreview: String = "",
    val isError: Boolean = false,
    val errorMessageIsDivideByZero: Boolean = false,
    /** True right after '=' — the next digit typed should start a fresh number. */
    val justEvaluated: Boolean = false
)

/**
 * Sequential (chain) calculator engine — the same model used by standard
 * OS calculators: 12 + 5 + 3 = evaluates left-to-right as each operator or
 * '=' is pressed, rather than applying operator precedence. Precedence-
 * aware expression evaluation belongs to the Scientific calculator (Phase 2).
 *
 * Pure Kotlin, no Android dependency, so it is directly unit-testable.
 */
class CalculatorEngine {

    private val mc = MathContext(15, RoundingMode.HALF_UP)
    private val maxDigits = 15

    fun reduce(state: CalculatorState, event: CalculatorEvent): CalculatorState {
        if (state.isError && event !is CalculatorEvent.Clear) {
            // Any input after an error clears it first, then applies the input.
            return reduce(CalculatorState(), event)
        }
        return when (event) {
            is CalculatorEvent.Digit -> onDigit(state, event.digit)
            is CalculatorEvent.Decimal -> onDecimal(state)
            is CalculatorEvent.Op -> onOperator(state, event.operator)
            is CalculatorEvent.Equals -> onEquals(state)
            is CalculatorEvent.Clear -> CalculatorState()
            is CalculatorEvent.Backspace -> onBackspace(state)
            is CalculatorEvent.ToggleSign -> onToggleSign(state)
            is CalculatorEvent.Percent -> onPercent(state)
        }
    }

    private fun onDigit(state: CalculatorState, digit: Char): CalculatorState {
        val startFresh = state.currentInput == "0" || state.justEvaluated
        val base = if (startFresh) "" else state.currentInput
        if (base.replace("-", "").replace(".", "").length >= maxDigits) return state

        val newInput = base + digit
        return state.copy(
            currentInput = newInput,
            justEvaluated = false,
            expressionPreview = if (state.justEvaluated) "" else state.expressionPreview
        )
    }

    private fun onDecimal(state: CalculatorState): CalculatorState {
        if (state.justEvaluated) {
            return state.copy(currentInput = "0.", justEvaluated = false, expressionPreview = "")
        }
        if (state.currentInput.contains(".")) return state
        return state.copy(currentInput = state.currentInput + ".")
    }

    private fun onOperator(state: CalculatorState, operator: Operator): CalculatorState {
        val current = state.currentInput.toBigDecimalOrNull() ?: return state

        // Chain: if an operation is already pending, resolve it first (left-to-right).
        val resolved = if (state.previousValue != null && !state.justEvaluated) {
            applyOperator(state.previousValue, current, requireNotNull(state.pendingOperator))
        } else current

        if (resolved == null) {
            return state.copy(isError = true, errorMessageIsDivideByZero = true)
        }

        return state.copy(
            previousValue = resolved,
            pendingOperator = operator,
            currentInput = formatResult(resolved),
            expressionPreview = "${formatResult(resolved)} ${operator.symbol}",
            justEvaluated = true // next digit starts fresh, but we keep previousValue
        )
    }

    private fun onEquals(state: CalculatorState): CalculatorState {
        val operator = state.pendingOperator ?: return state
        val previous = state.previousValue ?: return state
        val current = state.currentInput.toBigDecimalOrNull() ?: return state

        val result = applyOperator(previous, current, operator)
            ?: return state.copy(isError = true, errorMessageIsDivideByZero = true)

        return CalculatorState(
            currentInput = formatResult(result),
            previousValue = null,
            pendingOperator = null,
            expressionPreview = "${formatResult(previous)} ${operator.symbol} ${formatResult(current)} =",
            justEvaluated = true
        )
    }

    private fun onBackspace(state: CalculatorState): CalculatorState {
        if (state.justEvaluated) return CalculatorState()
        val trimmed = state.currentInput.dropLast(1)
        return state.copy(currentInput = trimmed.ifEmpty { "0" })
    }

    private fun onToggleSign(state: CalculatorState): CalculatorState {
        val value = state.currentInput.toBigDecimalOrNull() ?: return state
        if (value.signum() == 0) return state
        return state.copy(currentInput = formatResult(value.negate()))
    }

    private fun onPercent(state: CalculatorState): CalculatorState {
        val current = state.currentInput.toBigDecimalOrNull() ?: return state
        val percentValue = if (state.previousValue != null) {
            // e.g. 200 + 10% => 10% of 200
            current.multiply(state.previousValue).divide(BigDecimal(100), mc)
        } else {
            current.divide(BigDecimal(100), mc)
        }
        return state.copy(currentInput = formatResult(percentValue))
    }

    private fun applyOperator(a: BigDecimal, b: BigDecimal, operator: Operator): BigDecimal? {
        return when (operator) {
            Operator.ADD -> a.add(b, mc)
            Operator.SUBTRACT -> a.subtract(b, mc)
            Operator.MULTIPLY -> a.multiply(b, mc)
            Operator.DIVIDE -> if (b.signum() == 0) null else a.divide(b, mc)
        }
    }

    private fun formatResult(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        // Avoid Java's scientific-notation toggle for very small/large stripped values.
        val plain = stripped.toPlainString()
        return if (plain.replace("-", "").replace(".", "").length > maxDigits) {
            value.round(MathContext(maxDigits, RoundingMode.HALF_UP)).stripTrailingZeros().toPlainString()
        } else plain
    }

    private fun String.toBigDecimalOrNull(): BigDecimal? = try {
        BigDecimal(this)
    } catch (e: NumberFormatException) {
        null
    }
}
