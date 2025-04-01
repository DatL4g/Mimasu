package dev.datlag.mimasu.core

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt

fun Float.round(decimals: Int): Float {
    val factor = 10F.pow(decimals)
    return (this * factor).roundToInt() / factor
}

fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}

fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)
fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timeZone).date
fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timeZone).time

fun LocalDate.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()) = this.atTime(time = LocalTime.now(timeZone))

fun LocalDate.toEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    val timeNow = LocalDateTime.now(timeZone = timeZone)
    return this.atTime(hour = timeNow.hour, minute = timeNow.minute).toInstant(timeZone = timeZone).toEpochMilliseconds()
}

fun LocalDateTime.toEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.toInstant(timeZone = timeZone).toEpochMilliseconds()
}