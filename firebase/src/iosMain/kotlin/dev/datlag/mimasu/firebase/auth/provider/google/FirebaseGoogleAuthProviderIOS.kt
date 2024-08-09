package dev.datlag.mimasu.firebase.auth.provider.google

import cocoapods.GoogleSignIn.GIDSignIn
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.FirebaseUser
import platform.UIKit.UIApplication
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseGoogleAuthProviderIOS(
    app: FirebaseApp = Firebase.app,
    firebaseAuthDataSource: FirebaseAuthDataSource = FirebaseAuthDataSource(
        firebaseAuthService = FirebaseAuthService(app)
    ),
    serverClientId: String
) : FirebaseGoogleAuthProvider(
    firebaseAuthDataSource = firebaseAuthDataSource,
    serverClientId = serverClientId
) {

    override suspend fun signIn(params: Any): Result<FirebaseUser> = suspendCatching {
        val token = retrieveIdToken().getOrThrow()
        firebaseAuthDataSource.authenticateWithGoogleIdToken(
            idToken = token
        ) ?: throw FirebaseAuthException.UnknownUser(provider = FirebaseProvider.Google)
    }

    private suspend fun retrieveIdToken() = suspendCoroutine<Result<String>> { continuation ->
        UIApplication.sharedApplication.keyWindow?.rootViewController?.let { controller ->
            GIDSignIn.sharedInstance.signInWithPresentingViewController(controller) { gidSingInResult, nsError ->
                when {
                    nsError != null -> continuation.resume(Result.failure(FirebaseAuthException.Google.Unknown))

                    else -> {
                        gidSingInResult?.user?.idToken?.tokenString?.let { idToken ->
                            continuation.resume(Result.success(idToken))
                        } ?: continuation.resume(Result.failure(FirebaseAuthException.Google.Unknown))
                    }
                }
            }
        }
    }
}