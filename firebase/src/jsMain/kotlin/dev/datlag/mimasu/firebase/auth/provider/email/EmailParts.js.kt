package dev.datlag.mimasu.firebase.auth.provider.email

internal actual operator fun EmailParts.Companion.invoke(
    email: String
): EmailParts {
    val parts = email.split('@')
    val hostname = parts.getOrNull(1)?.ifBlank { null }?.ifBlank { null } ?: email.substringAfter('@')
    val usernameParts = parts[0].split('+', limit = 2)

    return EmailParts(
        username = usernameParts[0],
        plusTag = usernameParts.getOrElse(1) { "" },
        hostname = hostname.lowercase()
    )
}