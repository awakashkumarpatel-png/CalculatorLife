package com.calculatorlife.app.ui.calculator.discount

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class DiscountMode { BY_PERCENT, BY_FINAL_PRICE }

data class DiscountResult(val discountAmount: BigDecimal, val discountPercent: BigDecimal, val finalPrice: BigDecimal)

/** Pure logic, no Android dependency — directly unit-testable. */
object DiscountEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    /** MRP + discount% → discount amount and final price. */
    fun computeByPercent(mrp: BigDecimal, discountPercent: BigDecimal): DiscountResult? {
        if (mrp.signum() <= 0 || discountPercent.signum() < 0) return null
        val discountAmount = mrp.multiply(discountPercent, mc).divide(BigDecimal(100), mc)
        return DiscountResult(discountAmount, discountPercent, mrp.subtract(discountAmount, mc))
    }

    /** MRP + final price → discount amount and percentage. */
    fun computeByFinalPrice(mrp: BigDecimal, finalPrice: BigDecimal): DiscountResult? {
        if (mrp.signum() <= 0 || finalPrice.signum() < 0 || finalPrice > mrp) return null
        val discountAmount = mrp.subtract(finalPrice, mc)
        val discountPercent = discountAmount.multiply(BigDecimal(100), mc).divide(mrp, mc)
        return DiscountResult(discountAmount, discountPercent, finalPrice)
    }
}
