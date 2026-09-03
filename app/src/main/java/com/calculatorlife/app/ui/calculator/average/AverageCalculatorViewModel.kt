package com.calculatorlife.app.ui.calculator.average

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AverageUiState(
    val input: String = "",
    val result: AverageResult? = null
)

class AverageCalculatorViewModel : ViewModel() {
    private val _state = MutableStateFlow(AverageUiState())
    val state: StateFlow<AverageUiState> = _state.asStateFlow()

    fun onInputChanged(value: String) {
        val numbers = AverageEngine.parseNumbers(value)
        _state.value = AverageUiState(input = value, result = AverageEngine.compute(numbers))
    }

    fun onReset() { _state.value = AverageUiState() }
}
