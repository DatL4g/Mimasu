package dev.datlag.mimasu.firebase.auth.provider.email

import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.auth.FirebaseUser

class FirebaseEmailAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource
) : FirebaseAuthProvider<EmailAuthParams>(firebaseAuthDataSource) {

    override suspend fun signIn(
        params: EmailAuthParams
    ): Result<FirebaseUser> = suspendCatching {
        firebaseAuthDataSource.authenticateWithEmailAndPassword(
            email = params.email,
            password = params.password
        ) ?: throw FirebaseAuthException.UnknownUser(provider = FirebaseProvider.Email)
    }

    suspend fun signIn(
        email: String,
        password: String
    ): Result<FirebaseUser> = signIn(EmailAuthParams(email, password))

    suspend fun signUp(params: EmailAuthParams): Result<FirebaseUser> = suspendCatching {
        firebaseAuthDataSource.createUserWithEmailAndPassword(
            email = params.email,
            password = params.password
        ) ?: throw FirebaseAuthException.UnknownUser(provider = FirebaseProvider.Email)
    }

    suspend fun signUp(
        email: String,
        password: String
    ): Result<FirebaseUser> = signUp(EmailAuthParams(email, password))

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