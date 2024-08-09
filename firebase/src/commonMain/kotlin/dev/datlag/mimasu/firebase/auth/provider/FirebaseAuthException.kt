package dev.datlag.mimasu.firebase.auth.provider

sealed interface FirebaseAuthException {

    data class UnknownUser(val provider: FirebaseProvider) : IllegalStateException("Firebase User is null for provider: $provider")

    sealed interface Google : FirebaseAuthException {

        data object Unknown : IllegalStateException("Unknown error occurred during Google Sign In")

        data object UnknownCredential : IllegalArgumentException("Unexpected type of credential")
    }
}