package com.calculatorlife.app.ui.calculator.interest

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class SimpleInterestResult(val interest: BigDecimal, val totalAmount: BigDecimal)

/** SI = P × R × T / 100. Pure logic, no Android dependency — directly unit-testable. */
object SimpleInterestEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(principal: BigDecimal, annualRatePercent: BigDecimal, years: BigDecimal): SimpleInterestResult? {
        if (principal.signum() <= 0 || years.signum() <= 0 || annualRatePercent.signum() < 0) return null
        val interest = principal.multiply(annualRatePercent, mc).multiply(years, mc).divide(BigDecimal(100), mc)
        return SimpleInterestResult(interest, principal.add(interest, mc))
    }
}
