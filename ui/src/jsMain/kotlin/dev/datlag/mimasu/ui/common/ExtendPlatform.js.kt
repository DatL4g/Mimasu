package dev.datlag.mimasu.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams
import dev.datlag.tooling.scopeCatching
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format.byUnicodePattern
import kotlin.time.ExperimentalTime
import kotlin.time.toJSDate

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryOf(text: String): ClipEntry {
    return ClipEntry.withPlainText(text)
}

@Composable
actual fun rememberGitHubAuthParams(): GitHubAuthParams? {
    return remember { GitHubAuthParams() }
}

@Composable
actual fun rememberGoogleAuthParams(): GoogleAuthParams {
    return remember { GoogleAuthParams(isRetrying = false) }
}

@OptIn(ExperimentalTime::class)
@Composable
actual fun LocalDate?.formatMedium(fallbackFormat: String): String? {
    if (this == null) {
        return null
    }

    val fallbackFormatter = remember(fallbackFormat) {
        LocalDate.Format {
            byUnicodePattern(fallbackFormat)
        }
    }

    return scopeCatching {
        this.atStartOfDayIn(
            TimeZone.currentSystemDefault()
        ).toJSDate().toLocaleDateString(
            options = dateLocaleOptions {
                year = "numeric"
                month = "short"
                day = "numeric"
            }
        )
    }.getOrNull()?.ifBlank { null } ?: scopeCatching {
        fallbackFormatter.format(this)
    }.getOrNull()?.ifBlank { null }
}