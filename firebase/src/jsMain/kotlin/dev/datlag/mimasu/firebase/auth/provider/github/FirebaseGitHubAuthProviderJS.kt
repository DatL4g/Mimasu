package dev.datlag.mimasu.firebase.auth.provider.github

import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.auth.GithubAuthProvider
import dev.gitlive.firebase.auth.externals.getAuth
import dev.gitlive.firebase.auth.externals.signInWithPopup
import dev.gitlive.firebase.auth.externals.updateCurrentUser
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.ExperimentalTime

class FirebaseGitHubAuthProviderJS(
    firebaseAuthDataSource: FirebaseAuthDataSource
) : FirebaseGitHubAuthProvider(firebaseAuthDataSource) {

    override suspend fun signIn(params: GitHubAuthParams): Result<User> = suspendCatching {
        val auth = getAuth(firebaseAuthDataSource.app.js)
        val userCredential = signInWithPopup(auth, provider.js).await()

        updateCurrentUser(auth, userCredential.user).await()

        firebaseAuthDataSource.currentUser
            ?: firebaseAuthDataSource.user.firstOrNull()
            ?: throw FirebaseAuthException.GitHub.Unknown()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun link(params: GitHubAuthParams): Result<User> = suspendCatching {
        val auth = getAuth(firebaseAuthDataSource.app.js)
        val userCredential = signInWithPopup(auth, provider.js).await()

        val authCredential = suspendCatching {
            GithubAuthProvider.credential(userCredential.user.getIdToken(forceRefresh = true).await())
        }.getOrNull() ?: return@suspendCatching run {
            updateCurrentUser(auth, userCredential.user).await()

            firebaseAuthDataSource.currentUser
                ?: firebaseAuthDataSource.user.firstOrNull()
                ?: throw FirebaseAuthException.GitHub.Unknown()
        }

        firebaseAuthDataSource.currentUser
            ?.firebase
            ?.linkWithCredential(authCredential)
            ?.user
            ?.let(::User)
            ?: throw FirebaseAuthException.GitHub.Unknown()
    }
}