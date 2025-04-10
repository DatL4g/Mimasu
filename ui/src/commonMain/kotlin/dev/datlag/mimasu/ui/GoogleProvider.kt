package dev.datlag.mimasu.ui

import dev.datlag.mimasu.firebase.auth.provider.google.FirebaseGoogleAuthProvider

sealed interface GoogleProvider {

    fun getOrNull(): FirebaseGoogleAuthProvider? = when (this) {
        is Present -> provider
        else -> null
    }

    data class Present(
        val provider: FirebaseGoogleAuthProvider
    ) : GoogleProvider

    data object Null : GoogleProvider

    companion object {
        fun <T> basedOn(value: T?, block: (T & Any) -> FirebaseGoogleAuthProvider): GoogleProvider = when (value) {
            null -> Null
            else -> Present(block(value))
        }
    }
}