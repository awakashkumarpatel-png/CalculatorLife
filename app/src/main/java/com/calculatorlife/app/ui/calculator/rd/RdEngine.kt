package com.calculatorlife.app.ui.calculator.rd

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class RdResult(val maturityValue: BigDecimal, val totalDeposited: BigDecimal, val estimatedInterest: BigDecimal)

/**
 * Recurring Deposit maturity value. Each month's deposit is assumed to
 * compound monthly at the given annual rate until maturity — the simplest
 * defensible model, and the one this calculator documents clearly rather
 * than approximating a bank's quarterly-compounding formula with a
 * fractional exponent (which is fragile in fixed-point arithmetic). Computed
 * by explicit month-by-month accumulation rather than a closed-form
 * expression, so the logic is easy to verify by inspection.
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object RdEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(monthlyDeposit: BigDecimal, annualRatePercent: BigDecimal, tenureMonths: Int): RdResult? {
        if (monthlyDeposit.signum() <= 0 || tenureMonths <= 0 || tenureMonths > 1200 || annualRatePercent.signum() < 0) return null

        val monthlyRate = annualRatePercent.divide(BigDecimal(1200), mc)
        val onePlusR = BigDecimal.ONE.add(monthlyRate)

        var maturityValue = BigDecimal.ZERO
        // Deposit made at the start of month k compounds for (tenureMonths - k + 1) months.
        for (k in 1..tenureMonths) {
            val monthsCompounding = tenureMonths - k + 1
            maturityValue = maturityValue.add(monthlyDeposit.multiply(onePlusR.pow(monthsCompounding, mc), mc), mc)
        }

        val totalDeposited = monthlyDeposit.multiply(BigDecimal(tenureMonths), mc)
        return RdResult(maturityValue, totalDeposited, maturityValue.subtract(totalDeposited, mc))
    }
}
