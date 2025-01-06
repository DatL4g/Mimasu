package dev.datlag.mimasu.core.common

import kotlinx.datetime.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

@Throws(IllegalArgumentException::class)
actual fun Any.convertToInstant(): Instant = when (this) {
    is java.time.LocalDateTime -> Instant.fromEpochSeconds(
        this.atZone(ZoneId.systemDefault()).toEpochSecond()
    )
    is ZonedDateTime -> Instant.fromEpochSeconds(this.toEpochSecond())
    is java.time.Instant -> Instant.fromEpochMilliseconds(this.toEpochMilli())
    is Date -> Instant.fromEpochMilliseconds(this.time)
    else -> throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}