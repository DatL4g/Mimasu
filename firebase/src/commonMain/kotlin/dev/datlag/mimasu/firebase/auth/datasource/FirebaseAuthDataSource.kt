package dev.datlag.mimasu.firebase.auth.datasource

import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.coroutines.flow.Flow

class FirebaseAuthDataSource(
    private val firebaseAuthService: FirebaseAuthService
) {

    /**
     * Retrieves the currently logged-in user.
     *
     * @return A Flow emitting the currently logged-in FirebaseUser, or null if no user is logged in.
     */
    val user: Flow<FirebaseUser?> = firebaseAuthService.user

    val currentUser: FirebaseUser?
        get() = firebaseAuthService.currentUser

    /**
     * Authenticates a user with a Google ID token.
     *
     * @param idToken The Google ID token used for authentication.
     * @return The authenticated FirebaseUser, or null if authentication fails.
     */
    suspend fun authenticateWithGoogleIdToken(idToken: String, accessToken: String? = null): FirebaseUser? {
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
    ): FirebaseUser? = firebaseAuthService.signInWithEmailAndPassword(email = email, password = password)

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
    ): FirebaseUser? = firebaseAuthService.createUserWithEmailAndPassword(email = email, password = password)

    /**
     * Sends a password reset email with specified settings.
     *
     * @param email The email address to send the password reset email to.
     * @param url The URL for the password reset page.
     * @param iOSBundleId The iOS bundle ID for the app. Default is null.
     * @param androidPackageName The Android package name for the app. Default is null.
     * @param installIfNotAvailable Whether to install the Android app if not available. Default is true.
     * @param minimumVersion The minimum Android version of the app required. Default is null.
     * @param canHandleCodeInApp Whether the app can handle the code in app. Default is false.
     */
    suspend fun sendPasswordResetEmail(
        email: String,
        url: String,
        iOSBundleId: String? = null,
        androidPackageName: String? = null,
        installIfNotAvailable: Boolean = true,
        minimumVersion: String? = null,
        canHandleCodeInApp: Boolean = false,
    ) {
        firebaseAuthService.sendPasswordResetEmail(
            email = email,
            url = url,
            iOSBundleId = iOSBundleId,
            androidPackageName = androidPackageName,
            installIfNotAvailable = installIfNotAvailable,
            minimumVersion = minimumVersion,
            canHandleCodeInApp = canHandleCodeInApp
        )
    }

    suspend fun updateCurrentUser(user: FirebaseUser) {
        firebaseAuthService.updateCurrentUser(user)
    }

    suspend fun fetchSignInMethodsForEmail(email: String): ImmutableCollection<String> {
        return firebaseAuthService.fetchSignInMethodsForEmail(email)
    }

    /**
     * Signs out the currently logged-in user.
     */
    suspend fun signOut() {
        firebaseAuthService.signOut()
    }
}