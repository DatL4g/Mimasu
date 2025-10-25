@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package dev.datlag.mimasu.common

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.currency_billion
import dev.datlag.mimasu.composeapp.generated.resources.currency_million
import dev.datlag.mimasu.composeapp.generated.resources.currency_thousand
import dev.datlag.mimasu.composeapp.generated.resources.space_bytes_format
import dev.datlag.mimasu.composeapp.generated.resources.space_bytes_format_sizes
import io.tolgee.common.sprintf
import io.tolgee.stringArrayResource
import io.tolgee.stringResource
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToLong

@Composable
fun Int.formatCurrencyShort(): String {
    val abs = abs(this)
    val type = when {
        abs >= 1_000_000_000 -> CurrencyValue.Billion
        abs >= 1_000_000 -> CurrencyValue.Million
        abs >= 1_000 -> CurrencyValue.Thousand
        else -> CurrencyValue.None
    }

    val (value, suffix) = when (type) {
        is CurrencyValue.Billion -> (this / 1_000_000_000.0) to stringResource(Res.string.currency_billion)
        is CurrencyValue.Million -> (this / 1_000_000.0) to stringResource(Res.string.currency_million)
        is CurrencyValue.Thousand -> (this / 1_000.0) to stringResource(Res.string.currency_thousand)
        is CurrencyValue.None -> this.toDouble() to ""
    }

    val rounded = if (value % 1 <= 0.0) {
        value.toInt().toString()
    } else {
        "%.1f".sprintf(value)
    }

    return "$rounded $suffix"
}

@Composable
fun Long.formatBytesHumanReadable(): String {
    val suffix = stringArrayResource(Res.array.space_bytes_format_sizes)
    return log2(coerceAtLeast(1).toDouble()).toInt().div(10).let {
        val precision = when (it) {
            0 -> 0
            1 -> 1
            else -> 2
        }
        val fallbackSuffix = suffix.ifEmpty { null } ?: listOf("B", "KB", "MB", "GB", "TB", "PB")
        val num = toDouble() / 2.0.pow(it * 10.0)
        val finalNum = if (precision == 0) {
            num.roundToLong()
        } else {
            val scale = 10.0.pow(precision)
            val roundedNum = (num * scale).roundToLong() / scale

            roundedNum
        }

        stringResource(Res.string.space_bytes_format, finalNum, fallbackSuffix[it])
    }
}

@Serializable
private sealed interface CurrencyValue {
    @Serializable
    object None : CurrencyValue

    @Serializable
    object Thousand : CurrencyValue

    @Serializable
    object Million : CurrencyValue

    @Serializable
    object Billion : CurrencyValue
}