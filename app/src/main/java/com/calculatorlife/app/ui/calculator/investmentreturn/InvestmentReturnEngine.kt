package com.calculatorlife.app.ui.calculator.investmentreturn

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.pow

data class InvestmentReturnResult(val cagrPercent: BigDecimal, val absoluteGain: BigDecimal, val absoluteGainPercent: BigDecimal)

/**
 * CAGR = (finalValue / initialValue)^(1/years) − 1. The fractional exponent
 * requires floating-point math (BigDecimal has no fractional-power
 * operation); the result is converted back to BigDecimal for display only.
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object InvestmentReturnEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(initialValue: BigDecimal, finalValue: BigDecimal, years: BigDecimal): InvestmentReturnResult? {
        if (initialValue.signum() <= 0 || finalValue.signum() < 0 || years.signum() <= 0) return null

        val ratio = finalValue.divide(initialValue, mc).toDouble()
        val cagr = ratio.pow(1.0 / years.toDouble()) - 1.0
        if (cagr.isNaN() || cagr.isInfinite()) return null

        val gain = finalValue.subtract(initialValue, mc)
        val gainPercent = gain.multiply(BigDecimal(100), mc).divide(initialValue, mc)

        return InvestmentReturnResult(
            cagrPercent = BigDecimal(cagr * 100, mc),
            absoluteGain = gain,
            absoluteGainPercent = gainPercent
        )
    }
}
