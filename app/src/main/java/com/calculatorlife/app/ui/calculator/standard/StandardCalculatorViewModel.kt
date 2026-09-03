package com.calculatorlife.app.ui.calculator.standard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Thin MVVM wrapper around [CalculatorEngine]. The engine holds all the
 * actual math; this class only owns the StateFlow and translates UI events
 * into engine events. Saving results to History (Phase 5) will hook into
 * [onEquals] once the History repository exists.
 */
class StandardCalculatorViewModel : ViewModel() {

    private val engine = CalculatorEngine()

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    fun onDigit(digit: Char) = dispatch(CalculatorEvent.Digit(digit))
    fun onDecimal() = dispatch(CalculatorEvent.Decimal)
    fun onOperator(operator: Operator) = dispatch(CalculatorEvent.Op(operator))
    fun onEquals() = dispatch(CalculatorEvent.Equals)
    fun onClear() = dispatch(CalculatorEvent.Clear)
    fun onBackspace() = dispatch(CalculatorEvent.Backspace)
    fun onToggleSign() = dispatch(CalculatorEvent.ToggleSign)
    fun onPercent() = dispatch(CalculatorEvent.Percent)

    private fun dispatch(event: CalculatorEvent) {
        _state.value = engine.reduce(_state.value, event)
    }
}
