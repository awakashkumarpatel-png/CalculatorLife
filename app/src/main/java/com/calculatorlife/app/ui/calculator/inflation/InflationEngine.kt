package com.calculatorlife.app.ui.calculator.inflation

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

enum class InflationMode { FUTURE_COST, REAL_VALUE }

data class InflationResult(val adjustedValue: BigDecimal)

/** Pure logic, no Android dependency — directly unit-testable. */
object InflationEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    /** What will [presentValue] cost after [years] at [inflationRatePercent]? FV = PV(1+r)^t */
    fun futureCost(presentValue: BigDecimal, inflationRatePercent: BigDecimal, years: Int): InflationResult? {
        if (presentValue.signum() < 0 || years <= 0 || years > 200 || inflationRatePercent.signum() < 0) return null
        val r = inflationRatePercent.divide(BigDecimal(100), mc)
        val factor = BigDecimal.ONE.add(r).pow(years, mc)
        return InflationResult(presentValue.multiply(factor, mc))
    }

    /** What is [futureValue] worth today, after [years] of inflation at [inflationRatePercent]? PV = FV / (1+r)^t */
    fun realValue(futureValue: BigDecimal, inflationRatePercent: BigDecimal, years: Int): InflationResult? {
        if (futureValue.signum() < 0 || years <= 0 || years > 200 || inflationRatePercent.signum() < 0) return null
        val r = inflationRatePercent.divide(BigDecimal(100), mc)
        val factor = BigDecimal.ONE.add(r).pow(years, mc)
        if (factor.signum() == 0) return null
        return InflationResult(futureValue.divide(factor, mc))
    }
}
