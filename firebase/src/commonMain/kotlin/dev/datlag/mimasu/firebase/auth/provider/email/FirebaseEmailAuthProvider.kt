package dev.datlag.mimasu.firebase.auth.provider.email

import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.auth.EmailAuthProvider

class FirebaseEmailAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource
) : FirebaseAuthProvider<EmailAuthParams>(firebaseAuthDataSource) {

    override suspend fun signIn(
        params: EmailAuthParams
    ): Result<User> = suspendCatching {
        val currentUser = firebaseAuthDataSource.currentUser

        currentUser?.firebase?.linkWithCredential(
            EmailAuthProvider.credential(params.email, params.password)
        )?.user?.let(::User)?.let {
            return@suspendCatching it
        }

        val signInResult = suspendCatching {
            firebaseAuthDataSource.authenticateWithEmailAndPassword(
                email = params.email,
                password = params.password
            )
        }.onSuccess { user ->
            user?.let { return@suspendCatching it }
        }

        val signUpResult = suspendCatching {
            firebaseAuthDataSource.createUserWithEmailAndPassword(
                email = params.email,
                password = params.password
            )
        }.onSuccess { user ->
            user?.let { return@suspendCatching it }
        }

        signInResult.getOrNull() ?: signUpResult.getOrNull() ?: firebaseAuthDataSource.currentUser ?: run {
            throw signInResult.exceptionOrNull()
                ?: signUpResult.exceptionOrNull()
                ?: FirebaseAuthException.UnknownUser(FirebaseProvider.Email)
        }
    }
}