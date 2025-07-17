package dev.datlag.mimasu.firebase.auth.datasource

import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.User
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow

class FirebaseAuthDataSource(
    private val firebaseAuthService: FirebaseAuthService
) {

    val auth: FirebaseAuth
        get() = firebaseAuthService.auth

    val user: Flow<User?> = firebaseAuthService.user

    val app: FirebaseApp
        get() = firebaseAuthService.app

    val currentUser: User?
        get() = firebaseAuthService.currentUser

    /**
     * Authenticates a user with a Google ID token.
     *
     * @param idToken The Google ID token used for authentication.
     * @return The authenticated FirebaseUser, or null if authentication fails.
     */
    suspend fun authenticateWithGoogleIdToken(idToken: String, accessToken: String? = null): User? {
        val firebaseCredential = GoogleAuthProvider.credential(idToken = idToken, accessToken = accessToken)
        return firebaseAuthService.signIn(credential = firebaseCredential)
    }

    /**
     * Authenticates a user with an email and password.
     *
     * @param email The email used for authentication.
     * @param password The password used for authentication.
     * @return The authenticated FirebaseUser, or null if authentication fails.
     */
    suspend fun authenticateWithEmailAndPassword(
        email: String,
        password: String
    ): User? = firebaseAuthService.signInWithEmailAndPassword(email = email, password = password)

    /**
     * Creates a new user with the given email and password.
     *
     * @param email The email address used for creating the new user.
     * @param password The password used for creating the new user.
     * @return The created FirebaseUser, or null if creation fails.
     */
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): User? = firebaseAuthService.createUserWithEmailAndPassword(email = email, password = password)

    suspend fun updateCurrentUser(user: FirebaseUser) {
        firebaseAuthService.updateCurrentUser(user)
    }

    suspend fun updateCurrentUser(user: User) = updateCurrentUser(user.firebase)

    suspend fun signOut() {
        firebaseAuthService.signOut()
    }
}