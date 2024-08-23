package dev.datlag.mimasu.core.common

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Returns the current LocalDateTime in the specified time zone.
 *
 * @param timeZone The time zone to use. Defaults to the system's current time zone.
 * @return The current LocalDateTime in the specified time zone.
 */
fun LocalDateTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) = Clock.System.now().toLocalDateTime(timeZone)

/**
 * Returns the current LocalDate in the specified time zone.
 *
 * @param timeZone The time zone to use. Defaults to the system's current time zone.
 * @return The current LocalDate in the specified time zone.
 */
fun LocalDate.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timeZone).date

/**
 * Returns the current LocalTime in the specified time zone.
 *
 * @param timeZone The time zone to use. Defaults to the system's current time zone.
 * @return The current LocalTime in the specified time zone.
 */
fun LocalTime.Companion.now(timeZone: TimeZone = TimeZone.currentSystemDefault()) = LocalDateTime.now(timeZone).time

/**
 * Converts this LocalDate to a LocalDateTime at the current time in the specified time zone.
 *
 * @param timeZone The time zone to use. Defaults to the system's current time zone.
 * @return The LocalDateTime corresponding to this LocalDate at the current time.
 */
fun LocalDate.toLocalDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()) = this.atTime(time = LocalTime.now(timeZone))

/**
 * Converts this LocalDate to the number of milliseconds since the epoch at the current time in the specified time zone.
 *
 * @param timeZone The time zone to use. Defaults to the system's current time zone.
 * @return The number of milliseconds since the epoch for this LocalDate at the current time.
 */
fun LocalDate.toEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    val timeNow = LocalDateTime.now(timeZone = timeZone)
    return this.atTime(hour = timeNow.hour, minute = timeNow.minute).toInstant(timeZone = timeZone).toEpochMilliseconds()
}

/**
 * Converts this LocalDateTime to the number of milliseconds since the epoch in the specified time zone.
 *
 * @param timeZone The time zone to use. Defaults to the system's current time zone.
 * @return The number of milliseconds since the epoch for this LocalDateTime.
 */
fun LocalDateTime.toEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    return this.toInstant(timeZone = timeZone).toEpochMilliseconds()
}

/**
 * Converts the given number of milliseconds since the epoch to a LocalDateTime in the specified time zone.
 *
 * @param timeZone The time zone to use. Defaults to the system's current time zone.
 * @return The LocalDateTime corresponding to the given number of milliseconds since the epoch.
 */
fun Long.fromEpochMilliseconds(timeZone: TimeZone = TimeZone.currentSystemDefault()) = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)