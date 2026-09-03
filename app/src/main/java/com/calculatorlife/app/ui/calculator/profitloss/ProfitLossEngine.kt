package com.calculatorlife.app.ui.calculator.profitloss

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class ProfitLossResult(val amount: BigDecimal, val percent: BigDecimal, val isProfit: Boolean)

/** Pure logic, no Android dependency — directly unit-testable. */
object ProfitLossEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(costPrice: BigDecimal, sellingPrice: BigDecimal): ProfitLossResult? {
        if (costPrice.signum() <= 0 || sellingPrice.signum() < 0) return null
        val diff = sellingPrice.subtract(costPrice, mc)
        val percent = diff.abs().multiply(BigDecimal(100), mc).divide(costPrice, mc)
        return ProfitLossResult(diff.abs(), percent, isProfit = diff.signum() >= 0)
    }
}
