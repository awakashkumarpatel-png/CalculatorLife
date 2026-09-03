package com.calculatorlife.app.ui.calculator.sip

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class SipResult(
    val maturityValue: BigDecimal,
    val totalInvested: BigDecimal,
    val estimatedGains: BigDecimal
)

/**
 * Future value of a monthly SIP with monthly compounding:
 *   FV = P * [((1+i)^n − 1) / i] * (1 + i)
 * where i is the monthly rate and n the number of monthly installments.
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object SipEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(monthlyAmount: BigDecimal, annualRatePercent: BigDecimal, tenureMonths: Int): SipResult? {
        if (monthlyAmount.signum() <= 0 || tenureMonths <= 0 || annualRatePercent.signum() < 0) return null

        val i = annualRatePercent.divide(BigDecimal(1200), mc)
        val totalInvested = monthlyAmount.multiply(BigDecimal(tenureMonths), mc)

        val maturityValue = if (i.signum() == 0) {
            totalInvested
        } else {
            val onePlusI = BigDecimal.ONE.add(i)
            val pow = onePlusI.pow(tenureMonths, mc)
            val bracket = pow.subtract(BigDecimal.ONE).divide(i, mc)
            monthlyAmount.multiply(bracket, mc).multiply(onePlusI, mc)
        }

        return SipResult(
            maturityValue = maturityValue,
            totalInvested = totalInvested,
            estimatedGains = maturityValue.subtract(totalInvested, mc)
        )
    }
}
