package dev.datlag.mimasu.firebase.auth.provider.google

import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider

abstract class FirebaseGoogleAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource,
    protected val serverClientId: String
) : FirebaseAuthProvider<Any>(firebaseAuthDataSource = firebaseAuthDataSource)