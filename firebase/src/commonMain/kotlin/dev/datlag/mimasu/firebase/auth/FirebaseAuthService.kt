package dev.datlag.mimasu.firebase.auth

import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.scopeCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AndroidPackageName
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class FirebaseAuthService(
    val app: FirebaseApp = Firebase.app
) {

    val auth: FirebaseAuth
        get() = Firebase.auth(app)

    val user = combine(
        triggerUserReload,
        auth.authStateChanged,
        auth.idTokenChanged
    ) { reloadRequested, authState, idToken ->
        val reloadedUser = if (reloadRequested) {
            currentUser
        } else {
            null
        }

        val authUser = suspendCatching {
            authState?.let(::User)
        }.getOrNull()

        val idTokenUser = suspendCatching {
            idToken?.let(::User)
        }.getOrNull()

        listOfNotNull(
            reloadedUser,
            authUser,
            idTokenUser
        ).fold<User, User?>(null) { left, right ->
            left?.plus(right) ?: right
        }.also { user ->
            if (reloadRequested) {
                clearUserReload()

                user?.let { auth.updateCurrentUser(it.firebase) }
            }
        }
    }

    val currentUser: User?
        get() = scopeCatching {
            auth.currentUser?.let(::User)
        }.getOrNull()

    suspend fun signIn(
        credential: AuthCredential
    ): User? = auth
        .signInWithCredential(credential)
        .user?.let(::User)

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): User? = auth
        .createUserWithEmailAndPassword(email, password)
        .user?.let(::User)

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): User? = auth
        .signInWithEmailAndPassword(email, password)
        .user?.let(::User)

    suspend fun updateCurrentUser(user: FirebaseUser) {
        auth.updateCurrentUser(user)
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email, ActionCodeSettings(
            url = URL,
            androidPackageName = AndroidPackageName(
                packageName = PACKAGE_NAME,
                installIfNotAvailable = true
            ),
            canHandleCodeInApp = true
        ))
    }

    suspend fun verifyPasswordResetCode(code: String): String {
        return auth.verifyPasswordResetCode(code)
    }

    suspend fun changePassword(code: String, newPassword: String) {
        return auth.confirmPasswordReset(code, newPassword)
    }

    suspend fun verifyEmail(code: String) {
        return auth.applyActionCode(code)
    }

    companion object {
        private val _triggerUserReload = MutableStateFlow(false)
        val triggerUserReload = _triggerUserReload.asStateFlow()

        fun forceReload() = _triggerUserReload.compareAndSet(
            expect = false,
            update = true
        )

        private fun clearUserReload() = _triggerUserReload.compareAndSet(
            expect = true,
            update = false
        )

        internal const val URL = "https://mimasu.datlag.dev"
        internal const val PACKAGE_NAME = "dev.datlag.mimasu"
    }
}
