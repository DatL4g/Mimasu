package dev.datlag.mimasu.firebase.auth.provider.github

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthUserCollisionException as AndroidCollisionException
import com.google.firebase.auth.AuthResult as AndroidAuthResult
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.android
import com.google.firebase.auth.FirebaseUser as AndroidFirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual typealias GitHubAuthParams = Activity

class FirebaseGitHubAuthProviderAndroid(
    private val app: FirebaseApp = Firebase.app,
    firebaseAuthDataSource: FirebaseAuthDataSource = FirebaseAuthDataSource(
        firebaseAuthService = FirebaseAuthService(app)
    )
) : FirebaseGitHubAuthProvider(firebaseAuthDataSource) {

    override suspend fun signIn(params: GitHubAuthParams): Result<FirebaseUser> {
        val auth = Firebase.auth(app).android

        val currentUser = Firebase.auth(app).currentUser?.android

        // Link with current user or sign in
        var collisionEmail: String? = null
        val linkAuthResult = currentUser?.startActivityForLinkWithProvider(
            params,
            provider.android
        )?.linkOrSignInUser(currentUser)?.onFailure {
            if (it is FirebaseAuthUserCollisionException || it is AndroidCollisionException) {
                it.email?.ifBlank { null }?.let { m -> collisionEmail = m }
            }
        }?.getOrNull()

        val authResult = linkAuthResult ?: auth.startActivityForSignInWithProvider(
            params,
            provider.android
        ).linkOrSignInUser(currentUser).onFailure {
            if (it is FirebaseAuthUserCollisionException || it is AndroidCollisionException) {
                it.email?.ifBlank { null }?.let { m -> collisionEmail = m }
            }
        }.getOrNull()

        authResult?.let {
            Firebase.auth(app).android.updateCurrentUser(it).await()
        }

        return Firebase.auth(app).currentUser?.let {
            Result.success(it)
        } ?: run {
            val email = collisionEmail
            if (!email.isNullOrBlank()) {
                Result.failure(
                    FirebaseAuthService.CollisionException(
                        email = email,
                        provider = Firebase.auth(app).fetchSignInMethodsForEmail(email).toImmutableSet()
                    )
                )
            } else {
                Result.failure(FirebaseNoSignedInUserException("Not able to link or sign in through GitHub"))
            }
        }
    }

    private suspend fun Task<AndroidAuthResult?>.linkOrSignInUser(existing: AndroidFirebaseUser?): Result<AndroidFirebaseUser> = suspendCoroutine { continuation ->
        this.addOnSuccessListener {
            val credential = it?.credential
            it?.user?.email

            if (existing != null && credential != null) {
                existing.linkWithCredential(credential).addOnSuccessListener { link ->
                    continuation.resume(Result.success(link?.user ?: existing))
                }.addOnFailureListener { _ ->
                    continuation.resume(Result.success(it.user ?: existing))
                }
            } else {
                continuation.resume((it?.user ?: existing)?.let { u -> Result.success(u) } ?: Result.failure(IllegalStateException()))
            }
        }.addOnFailureListener {
            continuation.resume(Result.failure(it))
        }
    }
}