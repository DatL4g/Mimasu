@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package dev.datlag.mimasu.common

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.currency_billion
import dev.datlag.mimasu.composeapp.generated.resources.currency_million
import dev.datlag.mimasu.composeapp.generated.resources.currency_thousand
import io.tolgee.common.sprintf
import io.tolgee.stringResource
import kotlinx.serialization.Serializable
import kotlin.math.abs

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