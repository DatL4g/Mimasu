package dev.datlag.mimasu.ui.common

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import app.rive.runtime.kotlin.core.RendererType
import app.rive.runtime.kotlin.core.Rive
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams
import dev.datlag.mimasu.ui.other.ArchUtils
import dev.datlag.sekret.NativeLoader
import dev.datlag.tooling.scopeCatching
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toJavaLocalDate
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

@Composable
actual fun rememberGitHubAuthParams(): GitHubAuthParams? {
    val context = LocalContext.current
    return remember(context) {
        context.findActivity()
    } ?: context.findActivity()
}

@Composable
actual fun rememberGoogleAuthParams(): GoogleAuthParams {
    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()
    } ?: context.findActivity()

    return remember(activity) {
        GoogleAuthParams(context = activity ?: context, isRetrying = false)
    }
}

private fun Context.isRunningInTestLab(): Boolean {
    val testLabSetting = Settings.System.getString(contentResolver, "firebase.test.lab") ?: return false
    return when {
        testLabSetting.equals("true", ignoreCase = true) -> true
        testLabSetting == "1" -> true
        else -> testLabSetting.toBoolean()
    }
}

@Suppress("DEPRECATION")
private fun isRunningInTestHarness(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ActivityManager.isRunningInUserTestHarness()
    } else {
        ActivityManager.isRunningInTestHarness()
    }
}

private fun Context.isLowRam(): Boolean {
    val activityManager = scopeCatching {
        this.getSystemService<ActivityManager>()
    }.getOrNull() ?: ContextCompat.getSystemService(this, ActivityManager::class.java)

    return activityManager?.isLowRamDevice ?: false
}

fun Context.supportsRive(): Boolean {
    return !isRunningInTestLab() && !isRunningInTestHarness() && !isLowRam() && ArchUtils.supportsRive()
}

fun Rive.initSafely(
    context: Context,
    defaultRenderer: RendererType = defaultRendererType
): Boolean {
    if (!context.supportsRive()) {
        return false
    }

    val riveClass = "app.rive.runtime.kotlin.core.Rive"
    val libName = scopeCatching {
        val clazz = Class.forName(riveClass)
        val field = clazz.getDeclaredField("RIVE_ANDROID")
        field.isAccessible = true
        field.get(null) as? String
    }.getOrNull()?.trim()?.ifBlank { null } ?: "rive-android"

    val libLoaded = NativeLoader.loadLibrary(context, libName)
    val rendererSet = this.defaultRendererType == defaultRenderer || scopeCatching {
        val clazz = Class.forName(riveClass)
        val field = clazz.getDeclaredField("defaultRendererType")
        field.isAccessible = true
        field.set(clazz, defaultRenderer)
    }.isSuccess

    return if (libLoaded && rendererSet) {
        initializeCppEnvironment()
        true
    } else {
        init(context, defaultRenderer)
        false
    }
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