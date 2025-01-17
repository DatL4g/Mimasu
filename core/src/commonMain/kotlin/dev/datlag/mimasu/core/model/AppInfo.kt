package dev.datlag.mimasu.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AppInfo(
    val packageName: String,
    val name: String?,
    @Transient val logo: Any? = null
)