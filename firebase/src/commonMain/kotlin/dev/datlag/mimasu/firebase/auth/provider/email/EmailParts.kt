package dev.datlag.mimasu.firebase.auth.provider.email

import kotlinx.serialization.Serializable

@Serializable
internal data class EmailParts(
    val username: String,
    val plusTag: String,
    val hostname: String
) {
    companion object
}

internal expect operator fun EmailParts.Companion.invoke(email: String): EmailParts