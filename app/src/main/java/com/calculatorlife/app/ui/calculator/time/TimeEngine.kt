package com.calculatorlife.app.ui.calculator.time

import java.time.Duration
import java.time.LocalTime

enum class TimeOperator { ADD, SUBTRACT }

data class DurationBreakdown(val hours: Long, val minutes: Long, val seconds: Long, val totalSeconds: Long) {
    val isNegative: Boolean get() = totalSeconds < 0
}

/** Pure logic, no Android dependency — directly unit-testable. */
object TimeEngine {
    fun addOrSubtract(
        h1: Long, m1: Long, s1: Long,
        operator: TimeOperator,
        h2: Long, m2: Long, s2: Long
    ): DurationBreakdown {
        val d1 = Duration.ofHours(h1).plusMinutes(m1).plusSeconds(s1)
        val d2 = Duration.ofHours(h2).plusMinutes(m2).plusSeconds(s2)
        val result = if (operator == TimeOperator.ADD) d1.plus(d2) else d1.minus(d2)
        return breakdown(result)
    }

    /**
     * Difference between two clock times. If [end] is earlier than [start],
     * assumes it falls on the next day (standard "time until" behaviour).
     */
    fun difference(start: LocalTime, end: LocalTime): DurationBreakdown {
        var d = Duration.between(start, end)
        if (d.isNegative) d = d.plusHours(24)
        return breakdown(d)
    }

    private fun breakdown(duration: Duration): DurationBreakdown {
        val totalSeconds = duration.seconds
        val abs = kotlin.math.abs(totalSeconds)
        val hours = abs / 3600
        val minutes = (abs % 3600) / 60
        val seconds = abs % 60
        return DurationBreakdown(
            hours = if (totalSeconds < 0) -hours else hours,
            minutes = minutes,
            seconds = seconds,
            totalSeconds = totalSeconds
        )
    }
}
