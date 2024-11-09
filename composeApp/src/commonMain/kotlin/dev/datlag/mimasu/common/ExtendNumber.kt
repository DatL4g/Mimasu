package dev.datlag.mimasu.common

fun Long.toDuration(): String {
    fun doubleDigit(digit: Int): String {
        return if (digit < 10) {
            "0$digit"
        } else {
            "$digit"
        }
    }

    val duration = this / 1000
    val hours = duration / 3600
    val minutes = (duration - hours * 3600) / 60
    val seconds = duration - (hours * 3600 + minutes * 60)
    return if (hours > 0) {
        "${doubleDigit(hours.toInt())}:${doubleDigit(minutes.toInt())}:${doubleDigit(seconds.toInt())}"
    } else {
        "${doubleDigit(minutes.toInt())}:${doubleDigit(seconds.toInt())}"
    }
}