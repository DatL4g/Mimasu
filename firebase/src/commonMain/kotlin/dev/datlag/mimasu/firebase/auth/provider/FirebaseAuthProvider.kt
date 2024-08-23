package dev.datlag.mimasu.firebase.auth.provider

import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

abstract class FirebaseAuthProvider<SignInParams>(
    protected val firebaseAuthDataSource: FirebaseAuthDataSource
) {

    val user: Flow<FirebaseUser?> = firebaseAuthDataSource.user

    abstract suspend fun signIn(params: SignInParams & Any): Result<FirebaseUser>

    suspend fun signOut() {
        firebaseAuthDataSource.signOut()
    }
}