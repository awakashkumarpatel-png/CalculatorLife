package com.calculatorlife.app.ui.calculator.commission

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class CommissionResult(val commissionAmount: BigDecimal, val netAmount: BigDecimal)

/** Pure logic, no Android dependency — directly unit-testable. */
object CommissionEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(saleAmount: BigDecimal, commissionPercent: BigDecimal): CommissionResult? {
        if (saleAmount.signum() < 0 || commissionPercent.signum() < 0) return null
        val commission = saleAmount.multiply(commissionPercent, mc).divide(BigDecimal(100), mc)
        return CommissionResult(commission, saleAmount.subtract(commission, mc))
    }
}
