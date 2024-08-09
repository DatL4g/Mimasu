package dev.datlag.mimasu.firebase.auth.provider

sealed interface FirebaseProvider {

    data object Google : FirebaseProvider

    data object Email : FirebaseProvider
}