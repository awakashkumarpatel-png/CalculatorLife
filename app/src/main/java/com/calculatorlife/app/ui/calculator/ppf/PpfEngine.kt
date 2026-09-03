package com.calculatorlife.app.ui.calculator.ppf

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class PpfResult(val maturityValue: BigDecimal, val totalDeposited: BigDecimal, val estimatedInterest: BigDecimal)

/**
 * PPF maturity value: a fixed amount deposited once a year, compounding
 * annually at the given rate, for [years] years — the standard PPF model.
 * Computed by explicit year-by-year accumulation for clarity.
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object PpfEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(annualDeposit: BigDecimal, annualRatePercent: BigDecimal, years: Int): PpfResult? {
        if (annualDeposit.signum() <= 0 || years <= 0 || years > 100 || annualRatePercent.signum() < 0) return null

        val r = annualRatePercent.divide(BigDecimal(100), mc)
        val onePlusR = BigDecimal.ONE.add(r)

        var maturityValue = BigDecimal.ZERO
        for (k in 1..years) {
            val yearsCompounding = years - k + 1
            maturityValue = maturityValue.add(annualDeposit.multiply(onePlusR.pow(yearsCompounding, mc), mc), mc)
        }

        val totalDeposited = annualDeposit.multiply(BigDecimal(years), mc)
        return PpfResult(maturityValue, totalDeposited, maturityValue.subtract(totalDeposited, mc))
    }
}
