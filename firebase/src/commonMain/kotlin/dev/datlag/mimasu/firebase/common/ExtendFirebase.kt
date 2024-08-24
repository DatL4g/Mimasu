package dev.datlag.mimasu.firebase.common

import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseUser

/**
 * Retrieves the GitHub account id of the currently logged-in user.
 *
 * @return the GitHub account id of the currently logged-in FirebaseUser, or null if no user provided or connected to GitHub
 */
fun FirebaseUser?.githubHandle(): String? {
    val current = this ?: return null
    return current.providerData.firstOrNull {
        it.providerId.equals("github", ignoreCase = true) || it.providerId.equals("github.com", ignoreCase = true)
    }?.uid?.ifBlank { null }
}

expect val FirebaseAuthUserCollisionException.commonEmail: String?