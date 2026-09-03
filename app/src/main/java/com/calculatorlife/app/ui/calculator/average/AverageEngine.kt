package com.calculatorlife.app.ui.calculator.average

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class AverageResult(
    val mean: BigDecimal,
    val sum: BigDecimal,
    val count: Int,
    val min: BigDecimal,
    val max: BigDecimal
)

/** Pure logic, no Android dependency — directly unit-testable. */
object AverageEngine {
    fun compute(numbers: List<BigDecimal>): AverageResult? {
        if (numbers.isEmpty()) return null
        val sum = numbers.reduce { a, b -> a.add(b) }
        val mean = sum.divide(BigDecimal(numbers.size), MathContext(12, RoundingMode.HALF_UP))
        return AverageResult(
            mean = mean,
            sum = sum,
            count = numbers.size,
            min = numbers.min(),
            max = numbers.max()
        )
    }

    /** Parses a comma/space separated list of numbers, ignoring blank tokens. */
    fun parseNumbers(raw: String): List<BigDecimal> =
        raw.split(",", " ", "\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { token -> try { BigDecimal(token) } catch (e: NumberFormatException) { null } }
}
