package dev.datlag.mimasu.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import co.touchlab.kermit.Logger
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.module.PlatformModule
import dev.datlag.tooling.scopeCatching
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toJavaLocalDate
import org.chromium.net.CronetEngine
import org.kodein.di.DIAware
import org.kodein.di.DirectDI
import org.kodein.di.instanceOrNull
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> null
    }
}

tailrec fun Context.findWindow(): Window? = when (this) {
    is Activity -> window
    is ContextWrapper -> baseContext.findWindow()
    else -> null
}

fun DIAware.cronetEngine(): CronetEngine? {
    val instance by this.instanceOrNull<PlatformModule.Cronet>()
    return instance?.engine
}

fun DirectDI.cronetEngine(): CronetEngine? {
    return this.instanceOrNull<PlatformModule.Cronet>()?.engine
}

@Composable
actual fun LocalDate?.formatMedium(fallbackFormat: String): String? {
    if (this == null) {
        return null
    }
    val formatter = remember {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    }
    val fallbackFormatter = remember(fallbackFormat) {
        LocalDate.Format {
            byUnicodePattern(fallbackFormat)
        }
    }

    return scopeCatching {
        this.toJavaLocalDate().format(formatter)
    }.getOrNull()?.ifBlank { null } ?: scopeCatching {
        fallbackFormatter.format(this)
    }.getOrNull()?.ifBlank { null }
}

@Composable
actual fun rememberGitHubAuthParams(): GitHubAuthParams? {
    val context = LocalContext.current
    return remember(context) {
        context.findActivity()
    } ?: context.findActivity()
}