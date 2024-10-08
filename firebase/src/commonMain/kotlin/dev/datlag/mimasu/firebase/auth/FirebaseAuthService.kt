package dev.datlag.mimasu.firebase.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AndroidPackageName
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow

/**
 * FirebaseAuthService class for handling Firebase authentication operations.
 *
 * This class provides methods to get the current user, check if a user is logged in,
 * sign in with credentials, sign in with email and password, create a new user with email and password,
 * send a password reset email, and sign out users.
 */
class FirebaseAuthService(
    private val app: FirebaseApp = Firebase.app
) {

    /**
     * Retrieves the currently logged-in user.
     *
     * @return A Flow emitting the currently logged-in FirebaseUser, or null if no user is logged in.
     */
    val user: Flow<FirebaseUser?> = Firebase.auth(app).authStateChanged

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the currently logged-in FirebaseUser, or null if no user is logged in.
     */
    val currentUser: FirebaseUser?
        get() = Firebase.auth(app).currentUser

    /**
     * Signs in a user with the given authentication credentials.
     *
     * @param credential The authentication credentials used for sign-in.
     * @return The authenticated FirebaseUser, or null if authentication fails.
     */
    suspend fun signIn(
        credential: AuthCredential
    ) : FirebaseUser? = Firebase
        .auth(app)
        .signInWithCredential(authCredential = credential)
        .user

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
    ): FirebaseUser? = Firebase
        .auth(app)
        .createUserWithEmailAndPassword(email = email, password = password)
        .user

    /**
     * Signs in a user with the given email and password.
     *
     * @param email The email address used for sign-in.
     * @param password The password used for sign-in.
     * @return The authenticated FirebaseUser, or null if authentication fails.
     */
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser? = Firebase
        .auth(app)
        .signInWithEmailAndPassword(email = email, password = password)
        .user

    /**
     * Sends a password reset email with specified settings.
     *
     * @param email The email address to send the password reset email to.
     * @param url The URL for the password reset page.
     * @param iOSBundleId The iOS bundle ID for the app.
     * @param androidPackageName The Android package name for the app.
     * @param installIfNotAvailable Whether to install the Android app if not available.
     * @param minimumVersion The minimum Android version of the app required.
     * @param canHandleCodeInApp Whether the app can handle the code in app.
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
        val actionCodeSettings = ActionCodeSettings(
            url = url,
            androidPackageName = androidPackageName?.let {
                AndroidPackageName(
                    packageName = it,
                    installIfNotAvailable = installIfNotAvailable,
                    minimumVersion = minimumVersion
                )
            },
            iOSBundleId = iOSBundleId,
            canHandleCodeInApp = canHandleCodeInApp
        )

        Firebase.auth(app).sendPasswordResetEmail(email = email, actionCodeSettings = actionCodeSettings)
    }

    suspend fun updateCurrentUser(user: FirebaseUser) {
        Firebase.auth(app).updateCurrentUser(user)
    }

    suspend fun fetchSignInMethodsForEmail(email: String): ImmutableCollection<String> {
        return Firebase.auth(app).fetchSignInMethodsForEmail(email).toImmutableSet()
    }

    /**
     * Signs out the currently logged-in user.
     */
    suspend fun signOut() {
        Firebase.auth(app).signOut()
    }

    data class CollisionException(
        val email: String,
        val provider: ImmutableCollection<String>
    ) : Exception("Logging in with $email requires to use any of the following provider: ${provider.joinToString()}")
}