package dev.datlag.mimasu.firebase.auth.provider.google

import android.content.Context

actual data class GoogleAuthParams(
    val context: Context,
    actual val isRetrying: Boolean
)