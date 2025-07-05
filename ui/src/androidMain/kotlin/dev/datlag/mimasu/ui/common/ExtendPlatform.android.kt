package dev.datlag.mimasu.ui.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.rive.runtime.kotlin.core.RendererType
import app.rive.runtime.kotlin.core.Rive
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.mimasu.firebase.auth.provider.google.GoogleAuthParams
import dev.datlag.mimasu.ui.other.ArchUtils
import dev.datlag.sekret.NativeLoader
import dev.datlag.tooling.scopeCatching

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

fun Context.isRunningInTestLab(): Boolean {
    val testLabSetting = Settings.System.getString(contentResolver, "firebase.test.lab") ?: return false
    return when {
        testLabSetting.equals("true", ignoreCase = true) -> true
        testLabSetting == "1" -> true
        else -> testLabSetting.toBoolean()
    }
}

fun Rive.initSafely(
    context: Context,
    defaultRenderer: RendererType = defaultRendererType
): Boolean {
    if (context.isRunningInTestLab() || !ArchUtils.supportsRive()) {
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