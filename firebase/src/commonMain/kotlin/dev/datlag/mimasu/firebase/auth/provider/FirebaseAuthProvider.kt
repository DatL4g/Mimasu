package dev.datlag.mimasu.firebase.auth.provider

import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import kotlinx.coroutines.flow.Flow

abstract class FirebaseAuthProvider<SignInParams>(
    protected val firebaseAuthDataSource: FirebaseAuthDataSource
) {

    val user: Flow<User?> = firebaseAuthDataSource.user

    abstract suspend fun signIn(params: SignInParams & Any): Result<User>

    suspend fun signOut() {
        firebaseAuthDataSource.signOut()
    }
}