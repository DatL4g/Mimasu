package dev.datlag.mimasu.ui.common

import androidx.compose.runtime.Composable
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun rememberGitHubAuthParams(): GitHubAuthParams?

@Composable
expect fun rememberGoogleAuthParams(): GoogleAuthParams

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