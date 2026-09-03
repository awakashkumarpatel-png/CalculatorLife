package com.calculatorlife.app.ui.calculator.fraction

import java.math.BigInteger

enum class FractionOperator(val symbol: String) { ADD("+"), SUBTRACT("−"), MULTIPLY("×"), DIVIDE("÷") }

data class Fraction(val numerator: BigInteger, val denominator: BigInteger) {
    fun reduced(): Fraction {
        if (numerator.signum() == 0) return Fraction(BigInteger.ZERO, BigInteger.ONE)
        val g = numerator.gcd(denominator)
        var n = numerator / g
        var d = denominator / g
        if (d.signum() < 0) { n = -n; d = -d }
        return Fraction(n, d)
    }

    fun toDecimalString(): String {
        val bd = java.math.BigDecimal(numerator).divide(java.math.BigDecimal(denominator), 10, java.math.RoundingMode.HALF_UP)
        return bd.stripTrailingZeros().let { if (it.scale() < 0) it.setScale(0) else it }.toPlainString()
    }

    override fun toString(): String = "$numerator/$denominator"
}

/** Pure logic, no Android dependency — directly unit-testable. */
object FractionEngine {
    fun compute(a: Fraction, operator: FractionOperator, b: Fraction): Fraction? {
        if (a.denominator.signum() == 0 || b.denominator.signum() == 0) return null
        val result = when (operator) {
            FractionOperator.ADD -> Fraction(
                a.numerator * b.denominator + b.numerator * a.denominator,
                a.denominator * b.denominator
            )
            FractionOperator.SUBTRACT -> Fraction(
                a.numerator * b.denominator - b.numerator * a.denominator,
                a.denominator * b.denominator
            )
            FractionOperator.MULTIPLY -> Fraction(a.numerator * b.numerator, a.denominator * b.denominator)
            FractionOperator.DIVIDE -> {
                if (b.numerator.signum() == 0) return null
                Fraction(a.numerator * b.denominator, a.denominator * b.numerator)
            }
        }
        return result.reduced()
    }
}
