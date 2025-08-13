package dev.datlag.mimasu.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipEntry
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams
import io.tolgee.stringResource
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

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

expect fun clipEntryOf(text: String): ClipEntry