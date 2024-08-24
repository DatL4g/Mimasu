package dev.datlag.mimasu.firebase.auth.provider.email

import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.mimasu.firebase.common.commonEmail
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.auth.EmailAuthProvider
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseUser

class FirebaseEmailAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource
) : FirebaseAuthProvider<EmailAuthParams>(firebaseAuthDataSource) {

    override suspend fun signIn(
        params: EmailAuthParams
    ): Result<FirebaseUser> = suspendCatching {
        val currentUser = firebaseAuthDataSource.currentUser

        var collision: Boolean = false
        var collisionEmail: String? = null
        var invalidException: Exception? = null
        val linkAuthResult = suspendCatching {
            currentUser?.linkWithCredential(
                EmailAuthProvider.credential(params.email, params.password)
            )
        }.onFailure {
            if (it is FirebaseAuthUserCollisionException) {
                collision = true
                it.commonEmail?.ifBlank { null }?.let { m -> collisionEmail = m }
            }
        }.getOrNull()

        val signInAuthResult = linkAuthResult?.user ?: suspendCatching {
            firebaseAuthDataSource.authenticateWithEmailAndPassword(
                email = params.email,
                password = params.password
            )
        }.onFailure {
            if (it is FirebaseAuthUserCollisionException) {
                collision = true
                it.commonEmail?.ifBlank { null }?.let { m -> collisionEmail = m }
            } else if (it is FirebaseAuthInvalidCredentialsException) {
                invalidException = it
            }
        }.getOrNull()

        val signUpAuthResult = signInAuthResult ?: suspendCatching {
            firebaseAuthDataSource.createUserWithEmailAndPassword(
                email = params.email,
                password = params.password
            )
        }.onFailure {
            if (it is FirebaseAuthUserCollisionException) {
                collision = true
                it.commonEmail?.ifBlank { null }?.let { m -> collisionEmail = m }
            }
        }.getOrNull()

        signUpAuthResult ?: firebaseAuthDataSource.currentUser ?: run {
            val email = collisionEmail?.ifBlank { null } ?: if (collision) params.email else null
            if (!email.isNullOrBlank()) {
                throw FirebaseAuthService.CollisionException(
                    email = email,
                    provider = firebaseAuthDataSource.fetchSignInMethodsForEmail(email)
                )
            } else {
                throw invalidException ?: FirebaseAuthException.UnknownUser(provider = FirebaseProvider.Email)
            }
        }
    }

    suspend fun sendPasswordResetEmail(
        params: ForgotPasswordParams
    ): Result<Unit> = suspendCatching {
        firebaseAuthDataSource.sendPasswordResetEmail(
            email = params.email,
            url = params.url,
            iOSBundleId = params.iosParams?.iOSBundleId,
            androidPackageName = params.androidParams?.androidPackageName,
            installIfNotAvailable = params.androidParams?.installIfNotAvailable ?: true,
            minimumVersion = params.androidParams?.minimumVersion,
            canHandleCodeInApp = params.canHandleCodeInApp
        )
    }
}