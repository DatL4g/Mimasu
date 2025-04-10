package dev.datlag.mimasu.firebase.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

data class FirebaseAuthService(
    private val app: FirebaseApp = Firebase.app
) {

    val auth: FirebaseAuth
        get() = Firebase.auth(app)

    @OptIn(ExperimentalCoroutinesApi::class)
    val user: Flow<User?> = auth.authStateChanged.mapLatest { it?.let(::User) }

    val currentUser: User?
        get() = auth.currentUser?.let(::User)

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
}
