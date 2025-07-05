package dev.datlag.mimasu.firebase.auth.provider.google

import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.datlag.sekret.Secret

abstract class FirebaseGoogleAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource,
    @Secret protected val serverClientId: String
) : FirebaseAuthProvider<GoogleAuthParams>(firebaseAuthDataSource = firebaseAuthDataSource) {

    abstract suspend fun link(params: GoogleAuthParams): Result<User>
}