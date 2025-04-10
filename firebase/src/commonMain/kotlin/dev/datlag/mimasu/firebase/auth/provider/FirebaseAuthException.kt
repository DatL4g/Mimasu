package dev.datlag.mimasu.firebase.auth.provider

sealed interface FirebaseAuthException {

    data class UnknownUser(val provider: FirebaseProvider) : IllegalStateException("Firebase User is null for provider: $provider"), FirebaseAuthException

    sealed interface Google : FirebaseAuthException {

        class Unknown : IllegalStateException("Unknown error occurred during Google Sing In"), Google

        class UnknownCredential : IllegalArgumentException("Unexpected type of credentials"), Google
    }
}