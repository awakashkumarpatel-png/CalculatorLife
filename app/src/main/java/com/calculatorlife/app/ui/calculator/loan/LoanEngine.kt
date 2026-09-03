package com.calculatorlife.app.ui.calculator.loan

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class LoanResult(
    val emi: BigDecimal,
    val totalPayment: BigDecimal,
    val totalInterest: BigDecimal
)

/**
 * Standard reducing-balance EMI formula. Used identically by the EMI, Loan,
 * Home Loan, Personal Loan, and Car Loan calculators — they are the same
 * math under different menu entries, as is normal for finance apps (a Home
 * Loan and a Personal Loan both amortize the same way; what differs in real
 * banking is the rate/tenure the user enters, not the formula).
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object LoanEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun computeEmi(principal: BigDecimal, annualRatePercent: BigDecimal, tenureMonths: Int): LoanResult? {
        if (principal.signum() <= 0 || tenureMonths <= 0 || annualRatePercent.signum() < 0) return null

        val monthlyRate = annualRatePercent.divide(BigDecimal(1200), mc)
        val emi: BigDecimal = if (monthlyRate.signum() == 0) {
            principal.divide(BigDecimal(tenureMonths), mc)
        } else {
            val onePlusR = BigDecimal.ONE.add(monthlyRate)
            val pow = onePlusR.pow(tenureMonths, mc)
            val numerator = principal.multiply(monthlyRate, mc).multiply(pow, mc)
            val denominator = pow.subtract(BigDecimal.ONE)
            if (denominator.signum() == 0) return null
            numerator.divide(denominator, mc)
        }

        val totalPayment = emi.multiply(BigDecimal(tenureMonths), mc)
        val totalInterest = totalPayment.subtract(principal, mc)
        return LoanResult(emi, totalPayment, totalInterest)
    }
}
