package dev.datlag.mimasu.core

import dev.datlag.tooling.safeSubList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun Float.round(decimals: Int): Float {
    val factor = 10F.pow(decimals)
    return (this * factor).roundToInt() / factor
}

fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)
fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timeZone).date
fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timeZone).time

fun LocalDate.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()) = this.atTime(time = LocalTime.now(timeZone))

@OptIn(ExperimentalTime::class)
fun LocalDate.toEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    val timeNow = LocalDateTime.now(timeZone = timeZone)
    return this.atTime(hour = timeNow.hour, minute = timeNow.minute).toInstant(timeZone = timeZone).toEpochMilliseconds()
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.toEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.toInstant(timeZone = timeZone).toEpochMilliseconds()
}

fun <T> MutableList<T>.addSafely(index: Int, value: T) {
    if (this.size > index && index >= 0) {
        add(index, value)
    } else {
        add(value)
    }
}

fun <T> Collection<T>.findAroundPositionOrNull(value: Int, predicate: (T) -> Int): T? {
    if (value > this.size - 1) {
        return this.lastOrNull { predicate(it) == value }
    }
    if (value !in this.indices) {
        return this.firstOrNull { predicate(it) == value }
    }

    this.elementAtOrNull(value)?.let {
        val predicateValue = predicate(it)
        if (predicateValue == value) {
            return it
        } else {
            val lower = safeSubList(0, value)
            val higher = safeSubList(value, size)

            return if (predicateValue > value) {
                lower.lastOrNull { l -> predicate(l) == value } ?: higher.firstOrNull { h -> predicate(h) == value }
            } else {
                higher.firstOrNull { h -> predicate(h) == value } ?: lower.lastOrNull { l -> predicate(l) == value }
            }
        }
    }

    return this.firstOrNull { predicate(it) == value }
}