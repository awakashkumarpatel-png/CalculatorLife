package com.calculatorlife.app.ui.calculator.ratio

import java.math.BigInteger
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class SimplifiedRatio(val a: BigInteger, val b: BigInteger) {
    override fun toString(): String = "$a : $b"
}

/** Pure logic, no Android dependency — directly unit-testable. */
object RatioEngine {
    /** Reduces A:B to lowest whole-number terms. */
    fun simplify(a: BigInteger, b: BigInteger): SimplifiedRatio? {
        if (a.signum() == 0 && b.signum() == 0) return null
        val g = a.gcd(b)
        if (g.signum() == 0) return null
        return SimplifiedRatio(a / g, b / g)
    }

    /** Solves A:B = C:X for X, given A, B, C. Returns null if B is zero. */
    fun solveProportion(a: BigDecimal, b: BigDecimal, c: BigDecimal): BigDecimal? {
        if (a.signum() == 0) return null
        return c.multiply(b, MathContext(12, RoundingMode.HALF_UP))
            .divide(a, MathContext(12, RoundingMode.HALF_UP))
    }
}
