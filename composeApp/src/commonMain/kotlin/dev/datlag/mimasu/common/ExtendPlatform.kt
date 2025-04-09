package dev.datlag.mimasu.common

import androidx.compose.runtime.Composable
import dev.datlag.tooling.Platform
import dev.datlag.tooling.Platform.isIOS
import dev.datlag.tooling.Platform.isMacOS
import dev.datlag.tooling.Platform.isTVOS
import dev.datlag.tooling.Platform.isWatchOS
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val Platform.isApple: Boolean by lazy {
    isIOS || isTVOS || isWatchOS || isMacOS
}

@Composable
expect fun LocalDate?.formatMedium(fallbackFormat: String): String?

@Composable
fun LocalDate?.formatMedium(
    fallbackFormat: String,
    fallbackValue: String?
): String? = this.formatMedium(fallbackFormat)?.ifBlank { null } ?: fallbackValue?.ifBlank { null }

@Composable
fun LocalDate?.formatMedium(
    fallbackFormat: StringResource,
    fallbackValue: String?
): String? = this.formatMedium(
    fallbackFormat = stringResource(fallbackFormat),
    fallbackValue = fallbackValue
)