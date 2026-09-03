package com.calculatorlife.app.ui.calculator.datediff

import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

data class DateDifferenceResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalDays: Long,
    val totalWeeks: Long
)

/** Pure logic, no Android dependency — directly unit-testable. */
object DateDifferenceEngine {
    fun compute(start: LocalDate, end: LocalDate): DateDifferenceResult {
        val (from, to) = if (start.isAfter(end)) end to start else start to end
        val period = Period.between(from, to)
        return DateDifferenceResult(
            years = period.years,
            months = period.months,
            days = period.days,
            totalDays = ChronoUnit.DAYS.between(from, to),
            totalWeeks = ChronoUnit.DAYS.between(from, to) / 7
        )
    }

    fun toLocalDateOrNull(day: Int, month: Int, year: Int): LocalDate? =
        try { LocalDate.of(year, month, day) } catch (e: Exception) { null }
}
