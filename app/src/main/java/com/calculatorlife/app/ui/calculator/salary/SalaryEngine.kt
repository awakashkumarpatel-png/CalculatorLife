package com.calculatorlife.app.ui.calculator.salary

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class SalaryResult(val grossSalary: BigDecimal, val totalDeductions: BigDecimal, val netSalary: BigDecimal)

/** Pure logic, no Android dependency — directly unit-testable. */
object SalaryEngine {
    private val mc = MathContext(15, RoundingMode.HALF_UP)

    fun compute(basic: BigDecimal, hra: BigDecimal, otherAllowances: BigDecimal, deductions: BigDecimal): SalaryResult? {
        if (basic.signum() < 0 || hra.signum() < 0 || otherAllowances.signum() < 0 || deductions.signum() < 0) return null
        val gross = basic.add(hra, mc).add(otherAllowances, mc)
        val net = gross.subtract(deductions, mc)
        return SalaryResult(gross, deductions, net)
    }
}
