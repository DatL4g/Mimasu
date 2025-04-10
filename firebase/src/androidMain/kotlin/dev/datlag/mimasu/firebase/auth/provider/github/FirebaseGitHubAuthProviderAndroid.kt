package dev.datlag.mimasu.firebase.auth.provider.github

import com.google.android.gms.tasks.Task
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.android
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.firebase.auth.AuthResult as AndroidAuthResult
import com.google.firebase.auth.FirebaseUser as AndroidFirebaseUser

class FirebaseGitHubAuthProviderAndroid(
    firebaseAuthDataSource: FirebaseAuthDataSource
) : FirebaseGitHubAuthProvider(firebaseAuthDataSource) {

    override suspend fun signIn(params: GitHubAuthParams): Result<User> {
        val currentUser = firebaseAuthDataSource.currentUser?.firebase?.android

        val linkAuthResult = currentUser?.startActivityForLinkWithProvider(
            params,
            provider.android
        )?.linkOrSignInUser(currentUser)?.getOrNull()

        val authResult = linkAuthResult ?: firebaseAuthDataSource.auth.android.startActivityForSignInWithProvider(
            params,
            provider.android
        ).linkOrSignInUser(currentUser).getOrThrow()

        authResult.let {
            firebaseAuthDataSource.auth.android.updateCurrentUser(it).await()
        }

        return suspendCatching {
            firebaseAuthDataSource.currentUser
                ?: firebaseAuthDataSource.user.firstOrNull()
                ?: throw FirebaseAuthException.GitHub.Unknown()
        }
    }

    private suspend fun Task<AndroidAuthResult?>.linkOrSignInUser(
        existing: AndroidFirebaseUser?
    ): Result<AndroidFirebaseUser> = suspendCoroutine { continuation ->
        this.addOnSuccessListener {
            val credential = it?.credential

            if (existing != null && credential != null) {
                existing.linkWithCredential(credential).addOnSuccessListener { link ->
                    continuation.resume(Result.success(link.user ?: existing))
                }.addOnFailureListener { _ ->
                    continuation.resume(Result.success(it.user ?: existing))
                }
            } else {
                continuation.resume(
                    (it?.user ?: existing)?.let { u -> Result.success(u) }
                        ?: Result.failure(FirebaseAuthException.GitHub.Unknown())
                )
            }
        }.addOnFailureListener {
            continuation.resume(Result.failure(it))
        }
    }

}