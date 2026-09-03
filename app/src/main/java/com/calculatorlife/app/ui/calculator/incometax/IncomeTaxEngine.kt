package com.calculatorlife.app.ui.calculator.incometax

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class TaxSlab(val from: BigDecimal, val to: BigDecimal?, val ratePercent: BigDecimal)

data class IncomeTaxResult(val totalTax: BigDecimal, val effectiveRatePercent: BigDecimal, val netIncome: BigDecimal)

/**
 * Progressive income-tax estimate. The slab table below reflects a commonly
 * published simplified-regime structure at the time this was written — tax
 * law changes yearly, so this is explicitly labelled an ESTIMATE in the UI
 * and the user is told to verify current rates before relying on it for
 * filing. The slab table is a single list, easy to update in one place when
 * rates change.
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object IncomeTaxEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    val slabs: List<TaxSlab> = listOf(
        TaxSlab(BigDecimal(0), BigDecimal(300000), BigDecimal(0)),
        TaxSlab(BigDecimal(300000), BigDecimal(700000), BigDecimal(5)),
        TaxSlab(BigDecimal(700000), BigDecimal(1000000), BigDecimal(10)),
        TaxSlab(BigDecimal(1000000), BigDecimal(1200000), BigDecimal(15)),
        TaxSlab(BigDecimal(1200000), BigDecimal(1500000), BigDecimal(20)),
        TaxSlab(BigDecimal(1500000), null, BigDecimal(30))
    )

    fun compute(annualIncome: BigDecimal): IncomeTaxResult? {
        if (annualIncome.signum() < 0) return null

        var tax = BigDecimal.ZERO
        for (slab in slabs) {
            if (annualIncome <= slab.from) break
            val slabTop = slab.to?.min(annualIncome) ?: annualIncome
            val taxableInSlab = slabTop.subtract(slab.from, mc)
            if (taxableInSlab.signum() > 0) {
                tax = tax.add(taxableInSlab.multiply(slab.ratePercent, mc).divide(BigDecimal(100), mc), mc)
            }
        }

        val effectiveRate = if (annualIncome.signum() == 0) BigDecimal.ZERO
            else tax.multiply(BigDecimal(100), mc).divide(annualIncome, mc)

        return IncomeTaxResult(tax, effectiveRate, annualIncome.subtract(tax, mc))
    }
}
