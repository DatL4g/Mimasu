package dev.datlag.mimasu.firebase.auth.provider

sealed interface FirebaseAuthException {

    data class UnknownUser(val provider: FirebaseProvider) : IllegalStateException("Firebase User is null for provider: $provider"), FirebaseAuthException

    sealed interface Google : FirebaseAuthException {

        class Unknown : IllegalStateException("Unknown error occurred during Google Sign In"), Google

        class UnknownCredential : IllegalArgumentException("Unexpected type of credentials"), Google
    }

    sealed interface GitHub : FirebaseAuthException {

        class Unknown : IllegalStateException("Unknown error occurred during GitHub Sign In")
    }
}