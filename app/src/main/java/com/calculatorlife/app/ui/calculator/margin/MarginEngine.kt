package com.calculatorlife.app.ui.calculator.margin

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class MarginMode { FROM_PRICES, FROM_MARGIN_PERCENT }

data class MarginResult(val costPrice: BigDecimal, val sellingPrice: BigDecimal, val marginAmount: BigDecimal, val marginPercent: BigDecimal)

/**
 * Margin is expressed as a percentage of the SELLING price:
 *   margin% = (SP − CP) / SP × 100
 * This is what distinguishes it from Markup, which is expressed as a
 * percentage of COST — the two are commonly confused, so the app keeps
 * them as separate calculators.
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object MarginEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun fromPrices(costPrice: BigDecimal, sellingPrice: BigDecimal): MarginResult? {
        if (costPrice.signum() < 0 || sellingPrice.signum() <= 0) return null
        val marginAmount = sellingPrice.subtract(costPrice, mc)
        val marginPercent = marginAmount.multiply(BigDecimal(100), mc).divide(sellingPrice, mc)
        return MarginResult(costPrice, sellingPrice, marginAmount, marginPercent)
    }

    fun fromMarginPercent(costPrice: BigDecimal, marginPercent: BigDecimal): MarginResult? {
        if (costPrice.signum() < 0 || marginPercent.signum() < 0 || marginPercent >= BigDecimal(100)) return null
        val sellingPrice = costPrice.divide(BigDecimal(100).subtract(marginPercent, mc), mc).multiply(BigDecimal(100), mc)
        val marginAmount = sellingPrice.subtract(costPrice, mc)
        return MarginResult(costPrice, sellingPrice, marginAmount, marginPercent)
    }
}
