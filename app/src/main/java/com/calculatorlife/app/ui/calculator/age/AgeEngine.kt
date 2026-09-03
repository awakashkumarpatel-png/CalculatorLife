package com.calculatorlife.app.ui.calculator.age

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long,
    val totalMonths: Long
)

/** Pure logic, no Android dependency — directly unit-testable. */
object AgeEngine {
    fun compute(birthDate: LocalDate, asOf: LocalDate): AgeResult? {
        if (birthDate.isAfter(asOf)) return null
        val period = Period.between(birthDate, asOf)
        return AgeResult(
            years = period.years,
            months = period.months,
            days = period.days,
            totalDays = ChronoUnit.DAYS.between(birthDate, asOf),
            totalWeeks = ChronoUnit.DAYS.between(birthDate, asOf) / 7,
            totalMonths = ChronoUnit.MONTHS.between(birthDate, asOf)
        )
    }

    /** Returns null if the day/month/year combination is not a real calendar date. */
    fun toLocalDateOrNull(day: Int, month: Int, year: Int): LocalDate? =
        try { LocalDate.of(year, month, day) } catch (e: Exception) { null }
}
