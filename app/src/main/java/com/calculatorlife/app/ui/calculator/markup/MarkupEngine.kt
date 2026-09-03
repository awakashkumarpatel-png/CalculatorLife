package com.calculatorlife.app.ui.calculator.markup

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class MarkupMode { FROM_PRICES, FROM_MARKUP_PERCENT }

data class MarkupResult(val costPrice: BigDecimal, val sellingPrice: BigDecimal, val markupAmount: BigDecimal, val markupPercent: BigDecimal)

/**
 * Markup is expressed as a percentage of COST price:
 *   markup% = (SP − CP) / CP × 100
 * Distinct from Margin, which is expressed as a percentage of selling
 * price — kept as a separate calculator to avoid the two being confused.
 *
 * Pure logic, no Android dependency — directly unit-testable.
 */
object MarkupEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun fromPrices(costPrice: BigDecimal, sellingPrice: BigDecimal): MarkupResult? {
        if (costPrice.signum() <= 0 || sellingPrice.signum() < 0) return null
        val markupAmount = sellingPrice.subtract(costPrice, mc)
        val markupPercent = markupAmount.multiply(BigDecimal(100), mc).divide(costPrice, mc)
        return MarkupResult(costPrice, sellingPrice, markupAmount, markupPercent)
    }

    fun fromMarkupPercent(costPrice: BigDecimal, markupPercent: BigDecimal): MarkupResult? {
        if (costPrice.signum() <= 0 || markupPercent.signum() < 0) return null
        val markupAmount = costPrice.multiply(markupPercent, mc).divide(BigDecimal(100), mc)
        return MarkupResult(costPrice, costPrice.add(markupAmount, mc), markupAmount, markupPercent)
    }
}
