package dev.datlag.mimasu.core.common

import kotlinx.datetime.Instant

actual fun Any.convertToInstant(): Instant {
    throw IllegalArgumentException("Can not convert to LocalDateTime: $this")
}