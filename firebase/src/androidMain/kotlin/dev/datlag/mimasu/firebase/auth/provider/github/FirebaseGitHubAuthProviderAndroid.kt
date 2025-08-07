package dev.datlag.mimasu.firebase.auth.provider.github

import com.google.android.gms.tasks.Task
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
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
        val currentUser = suspendCatching {
            firebaseAuthDataSource.currentUser?.firebase?.android
        }.getOrNull()

        val linkAuthResult = suspendCatching {
            currentUser?.startActivityForLinkWithProvider(
                params,
                provider.android
            )?.linkOrSignInUser(currentUser)?.getOrThrow()
        }.getOrNull()

        val authResult = linkAuthResult ?: suspendCatching {
            firebaseAuthDataSource.auth.android.startActivityForSignInWithProvider(
                params,
                provider.android
            ).linkOrSignInUser(currentUser).getOrThrow()
        }.getOrNull()

        authResult?.let {
            firebaseAuthDataSource.auth.android.updateCurrentUser(it).await()
        }

        return suspendCatching {
            firebaseAuthDataSource.currentUser
                ?: firebaseAuthDataSource.user.firstOrNull()
                ?: throw FirebaseAuthException.GitHub.Unknown()
        }
    }

    override suspend fun link(params: GitHubAuthParams): Result<User> {
        val currentUser = suspendCatching {
            firebaseAuthDataSource.currentUser?.firebase?.android
        }.getOrNull()

        val linkAuthResult = suspendCatching {
            currentUser?.startActivityForLinkWithProvider(
                params,
                provider.android
            )?.linkOrSignInUser(currentUser)?.getOrThrow()
        }

        return suspendCatching {
            val linkedUser = linkAuthResult.getOrThrow() ?: throw FirebaseAuthException.UnknownUser(FirebaseProvider.GitHub)

            firebaseAuthDataSource.auth.android.updateCurrentUser(linkedUser).await()

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