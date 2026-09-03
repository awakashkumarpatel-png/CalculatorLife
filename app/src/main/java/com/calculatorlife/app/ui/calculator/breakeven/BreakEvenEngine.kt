package com.calculatorlife.app.ui.calculator.breakeven

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class BreakEvenResult(val breakEvenUnits: BigDecimal, val breakEvenRevenue: BigDecimal, val contributionMargin: BigDecimal)

/**
 * Break-even point:
 *   contribution margin = price per unit − variable cost per unit
 *   break-even units = fixed costs / contribution margin
 *   break-even revenue = break-even units × price per unit
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object BreakEvenEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(fixedCosts: BigDecimal, pricePerUnit: BigDecimal, variableCostPerUnit: BigDecimal): BreakEvenResult? {
        if (fixedCosts.signum() < 0 || pricePerUnit.signum() <= 0 || variableCostPerUnit.signum() < 0) return null
        val contributionMargin = pricePerUnit.subtract(variableCostPerUnit, mc)
        if (contributionMargin.signum() <= 0) return null // price must exceed variable cost to ever break even

        val units = fixedCosts.divide(contributionMargin, mc)
        val revenue = units.multiply(pricePerUnit, mc)
        return BreakEvenResult(units, revenue, contributionMargin)
    }
}
