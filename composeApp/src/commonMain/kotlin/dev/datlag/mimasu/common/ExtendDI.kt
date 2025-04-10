package dev.datlag.mimasu.common

import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import org.kodein.di.DirectDI
import org.kodein.di.instanceOrNull

fun DirectDI.firebaseAuthService(): FirebaseAuthService {
    return instanceOrNull<FirebaseAuthService>() ?: FirebaseAuthService()
}

fun DirectDI.firebaseDataSource(): FirebaseAuthDataSource {
    return instanceOrNull<FirebaseAuthDataSource>() ?: FirebaseAuthDataSource(firebaseAuthService())
}