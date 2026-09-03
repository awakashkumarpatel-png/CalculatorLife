package com.calculatorlife.app.ui.calculator.gst

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class GstMode { ADD_GST, REMOVE_GST }

data class GstResult(val baseAmount: BigDecimal, val gstAmount: BigDecimal, val totalAmount: BigDecimal)

/** Pure logic, no Android dependency — directly unit-testable. */
object GstEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(mode: GstMode, amount: BigDecimal, ratePercent: BigDecimal): GstResult? {
        if (amount.signum() <= 0 || ratePercent.signum() < 0) return null
        return when (mode) {
            GstMode.ADD_GST -> {
                val gst = amount.multiply(ratePercent, mc).divide(BigDecimal(100), mc)
                GstResult(baseAmount = amount, gstAmount = gst, totalAmount = amount.add(gst, mc))
            }
            GstMode.REMOVE_GST -> {
                val divisor = BigDecimal(100).add(ratePercent)
                if (divisor.signum() == 0) return null
                val base = amount.multiply(BigDecimal(100), mc).divide(divisor, mc)
                GstResult(baseAmount = base, gstAmount = amount.subtract(base, mc), totalAmount = amount)
            }
        }
    }
}
