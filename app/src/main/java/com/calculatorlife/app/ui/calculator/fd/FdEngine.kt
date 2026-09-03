package com.calculatorlife.app.ui.calculator.fd

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class CompoundingFrequency(val periodsPerYear: Int) {
    ANNUALLY(1), HALF_YEARLY(2), QUARTERLY(4), MONTHLY(12)
}

data class FdResult(val maturityValue: BigDecimal, val totalInterest: BigDecimal)

/**
 * Standard compound-interest maturity formula: A = P(1 + r/n)^(n*t)
 * Pure logic, no Android dependency — directly unit-testable.
 */
object FdEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(principal: BigDecimal, annualRatePercent: BigDecimal, years: BigDecimal, frequency: CompoundingFrequency): FdResult? {
        if (principal.signum() <= 0 || years.signum() <= 0 || annualRatePercent.signum() < 0) return null

        val n = BigDecimal(frequency.periodsPerYear)
        val r = annualRatePercent.divide(BigDecimal(100), mc).divide(n, mc)
        val totalPeriods = years.multiply(n, mc).setScale(0, RoundingMode.HALF_UP).toInt()
        val onePlusR = BigDecimal.ONE.add(r)
        val maturityValue = principal.multiply(onePlusR.pow(totalPeriods, mc), mc)

        return FdResult(maturityValue, maturityValue.subtract(principal, mc))
    }
}
