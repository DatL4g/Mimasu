package dev.datlag.mimasu.firebase.auth.provider.google

import android.content.Context
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime

class FirebaseGoogleAuthProviderAndroid(
    firebaseAuthDataSource: FirebaseAuthDataSource,
    serverClientId: String,
    private val filterByAuthorizedAccounts: Boolean = false
) : FirebaseGoogleAuthProvider(
    firebaseAuthDataSource = firebaseAuthDataSource,
    serverClientId = serverClientId
) {

    fun credentialManager(context: Context) = CredentialManager.create(context)

    override suspend fun signIn(params: GoogleAuthParams): Result<User> = suspendCatching {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = suspendCatching {
            credentialManager(params.context).getCredential(params.context, request)
        }.onFailure {
            if (it is NoCredentialException && !params.isRetrying) {
                delay(1000) // Wait 1 second and try again as it's sometimes buggy
                return@suspendCatching signIn(params.copy(isRetrying = true)).getOrThrow()
            }
        }

        handleSignInResponse(result.getOrThrow()).getOrThrow()
    }

    override suspend fun link(params: GoogleAuthParams): Result<User> = suspendCatching {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = suspendCatching {
            credentialManager(params.context).getCredential(params.context, request)
        }.onFailure {
            if (it is NoCredentialException && !params.isRetrying) {
                delay(1000) // Wait 1 second and try again as it's sometimes buggy
                return@suspendCatching link(params.copy(isRetrying = true)).getOrThrow()
            }
        }

        handleSignInResponse(result.getOrThrow(), linkOnly = true).getOrThrow()
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun handleSignInResponse(
        result: GetCredentialResponse,
        linkOnly: Boolean = false
    ): Result<User> = suspendCatching {
        when (val credential = result.credential) {
            is CustomCredential -> {
                when {
                    credential.type.equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL, ignoreCase = true) -> {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        val currentUser = firebaseAuthDataSource.currentUser

                        if (linkOnly) {
                            return@suspendCatching currentUser?.firebase?.linkWithCredential(
                                credential = GoogleAuthProvider.credential(idToken, null)
                            )?.user?.let(::User) ?: throw FirebaseAuthException.Google.Unknown()
                        } else {
                            currentUser?.firebase?.linkWithCredential(
                                credential = GoogleAuthProvider.credential(idToken, null)
                            )?.user?.let(::User)?.let {
                                return@suspendCatching it
                            }
                        }

                        val authResult = firebaseAuthDataSource.authenticateWithGoogleIdToken(
                            idToken = idToken
                        )?.also { firebaseAuthDataSource.updateCurrentUser(it) }

                        authResult
                            ?: firebaseAuthDataSource.currentUser
                            ?: throw FirebaseAuthException.UnknownUser(FirebaseProvider.Google)
                    }
                    else -> throw FirebaseAuthException.Google.UnknownCredential()
                }
            }
            else -> throw FirebaseAuthException.Google.UnknownCredential()
        }
    }
}