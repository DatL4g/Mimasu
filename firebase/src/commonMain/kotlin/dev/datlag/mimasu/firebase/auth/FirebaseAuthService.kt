package dev.datlag.mimasu.firebase.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

data class FirebaseAuthService(
    private val app: FirebaseApp = Firebase.app
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val user: Flow<User?> = Firebase.auth(app).authStateChanged.mapLatest { it?.let(::User) }

    val currentUser: User?
        get() = Firebase.auth(app).currentUser?.let(::User)

    suspend fun signIn(
        credential: AuthCredential
    ): User? = Firebase
        .auth(app)
        .signInWithCredential(credential)
        .user?.let(::User)

    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): User? = Firebase
        .auth(app)
        .createUserWithEmailAndPassword(email, password)
        .user?.let(::User)

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): User? = Firebase
        .auth(app)
        .signInWithEmailAndPassword(email, password)
        .user?.let(::User)

    suspend fun updateCurrentUser(user: FirebaseUser) {
        Firebase.auth(app).updateCurrentUser(user)
    }

    suspend fun signOut() {
        Firebase.auth(app).signOut()
    }
}
