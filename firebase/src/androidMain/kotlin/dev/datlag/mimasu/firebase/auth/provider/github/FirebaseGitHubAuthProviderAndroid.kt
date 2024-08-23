package dev.datlag.mimasu.firebase.auth.provider.github

import android.app.Activity
import com.google.android.gms.tasks.Task
import com.google.firebase.internal.api.FirebaseNoSignedInUserException
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import com.google.firebase.auth.AuthResult as AndroidAuthResult
import dev.gitlive.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseUser as AndroidFirebaseUser
import dev.gitlive.firebase.auth.auth
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
        val authResult = currentUser?.startActivityForLinkWithProvider(params, provider.android)?.linkOrSignInUser(currentUser)
            ?: auth.startActivityForSignInWithProvider(params, provider.android).linkOrSignInUser(currentUser)

        authResult?.let {
            Firebase.auth(app).android.updateCurrentUser(it).await()
        }

        return Firebase.auth(app).currentUser?.let {
            Result.success(it)
        } ?: Result.failure(FirebaseNoSignedInUserException("Not able to link or sign in through GitHub"))
    }

    private suspend fun Task<AndroidAuthResult?>.linkOrSignInUser(existing: AndroidFirebaseUser?): AndroidFirebaseUser? = suspendCoroutine { continuation ->
        this.addOnSuccessListener {
            val credential = it?.credential

            if (existing != null && credential != null) {
                existing.linkWithCredential(credential).addOnSuccessListener { link ->
                    continuation.resume(link?.user)
                }.addOnFailureListener { _ ->
                    continuation.resume(it.user)
                }
            } else {
                continuation.resume(it?.user)
            }
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }
}