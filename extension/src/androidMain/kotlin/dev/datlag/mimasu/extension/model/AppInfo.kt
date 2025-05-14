package dev.datlag.mimasu.extension.model

import android.graphics.drawable.Drawable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AppInfo(
    val packageName: String,
    val name: String,
    @Transient val logo: Drawable? = null
)
