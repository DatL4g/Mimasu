package dev.datlag.mimasu.firebase.auth.provider

import kotlinx.serialization.Serializable

@Serializable
sealed interface FirebaseProvider {

    @Serializable
    data object Google : FirebaseProvider

    @Serializable
    data object GitHub : FirebaseProvider

    @Serializable
    data object Email : FirebaseProvider
}