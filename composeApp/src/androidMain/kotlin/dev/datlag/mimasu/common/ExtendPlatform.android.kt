package dev.datlag.mimasu.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.firebase.auth.provider.github.GitHubAuthParams
import dev.datlag.tooling.Platform

@Composable
actual fun Platform.githubAuthParams(): GitHubAuthParams {
    val context = LocalContext.current
    return remember(context) {
        context.findActivity()!!
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> null
    }
}