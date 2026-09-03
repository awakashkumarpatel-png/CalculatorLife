package com.calculatorlife.app.ui.calculator.percentage

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class PercentageMode {
    /** What is X% of Y */
    VALUE_OF,
    /** X is what percent of Y */
    WHAT_PERCENT,
    /** Increase Y by X% */
    INCREASE,
    /** Decrease Y by X% */
    DECREASE,
    /** Percentage change from Y to X */
    PERCENT_CHANGE
}

data class PercentageResult(val result: BigDecimal, val explanation: String)

/** Pure logic, no Android dependency — directly unit-testable. */
object PercentageEngine {
    private val mc = MathContext(12, RoundingMode.HALF_UP)

    fun compute(mode: PercentageMode, x: BigDecimal, y: BigDecimal): PercentageResult? {
        return when (mode) {
            PercentageMode.VALUE_OF -> {
                val result = x.multiply(y, mc).divide(BigDecimal(100), mc)
                PercentageResult(result, "$x% of $y")
            }
            PercentageMode.WHAT_PERCENT -> {
                if (y.signum() == 0) return null
                val result = x.multiply(BigDecimal(100), mc).divide(y, mc)
                PercentageResult(result, "$x is what % of $y")
            }
            PercentageMode.INCREASE -> {
                val result = y.add(y.multiply(x, mc).divide(BigDecimal(100), mc), mc)
                PercentageResult(result, "$y increased by $x%")
            }
            PercentageMode.DECREASE -> {
                val result = y.subtract(y.multiply(x, mc).divide(BigDecimal(100), mc), mc)
                PercentageResult(result, "$y decreased by $x%")
            }
            PercentageMode.PERCENT_CHANGE -> {
                if (y.signum() == 0) return null
                val result = x.subtract(y, mc).multiply(BigDecimal(100), mc).divide(y, mc)
                PercentageResult(result, "Change from $y to $x")
            }
        }
    }
}
