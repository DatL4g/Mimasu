package dev.datlag.mimasu.firebase.auth.provider.google

import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.auth.externals.GoogleAuthProvider
import dev.gitlive.firebase.auth.externals.getAuth
import dev.gitlive.firebase.auth.externals.signInWithPopup
import dev.gitlive.firebase.auth.externals.updateCurrentUser
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.ExperimentalTime

class FirebaseGoogleAuthProviderJS(
    firebaseAuthDataSource: FirebaseAuthDataSource,
    serverClientId: String
) : FirebaseGoogleAuthProvider(
    firebaseAuthDataSource = firebaseAuthDataSource,
    serverClientId = serverClientId
) {

    override suspend fun signIn(params: GoogleAuthParams): Result<User> = suspendCatching {
        val auth = getAuth(firebaseAuthDataSource.app.js)
        val provider = GoogleAuthProvider()
        val userCredential = signInWithPopup(auth, provider).await()

        updateCurrentUser(auth, userCredential.user).await()

        firebaseAuthDataSource.currentUser
            ?: firebaseAuthDataSource.user.firstOrNull()
            ?: throw FirebaseAuthException.Google.Unknown()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun link(params: GoogleAuthParams): Result<User> = suspendCatching {
        val auth = getAuth(firebaseAuthDataSource.app.js)
        val provider = GoogleAuthProvider()
        val userCredential = signInWithPopup(auth, provider).await()
        val authCredential = suspendCatching {
            GoogleAuthProvider.credentialFromResult(userCredential)
        }.getOrNull() ?: return@suspendCatching run {
            updateCurrentUser(auth, userCredential.user).await()

            firebaseAuthDataSource.currentUser
                ?: firebaseAuthDataSource.user.firstOrNull()
                ?: throw FirebaseAuthException.Google.Unknown()
        }

        val googleCredential = dev.gitlive.firebase.auth.GoogleAuthProvider.credential(
            idToken = authCredential.idToken,
            accessToken = authCredential.accessToken
        )
        firebaseAuthDataSource.currentUser
            ?.firebase
            ?.linkWithCredential(googleCredential)
            ?.user
            ?.let(::User)
            ?: throw FirebaseAuthException.Google.Unknown()
    }
}