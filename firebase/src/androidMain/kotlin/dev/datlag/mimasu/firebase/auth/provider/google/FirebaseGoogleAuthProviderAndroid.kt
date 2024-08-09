package dev.datlag.mimasu.firebase.auth.provider.google

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.datlag.mimasu.firebase.auth.FirebaseAuthService
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.FirebaseUser

class FirebaseGoogleAuthProviderAndroid(
    app: FirebaseApp = Firebase.app,
    firebaseAuthDataSource: FirebaseAuthDataSource = FirebaseAuthDataSource(
        firebaseAuthService = FirebaseAuthService(app)
    ),
    serverClientId: String,
    private val context: Context,
    private val filterByAuthorizedAccounts: Boolean = false
) : FirebaseGoogleAuthProvider(
    firebaseAuthDataSource = firebaseAuthDataSource,
    serverClientId = serverClientId
) {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun signIn(params: Any): Result<FirebaseUser> = suspendCatching {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(context, request)

        handleSignInResponse(result).getOrThrow()
    }

    private suspend fun handleSignInResponse(
        result: GetCredentialResponse
    ): Result<FirebaseUser> = suspendCatching {
        when (val credential = result.credential) {
            is CustomCredential -> {
                when (credential.type) {
                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        firebaseAuthDataSource.authenticateWithGoogleIdToken(
                            idToken = idToken
                        ) ?: throw FirebaseAuthException.UnknownUser(provider = FirebaseProvider.Google)
                    }
                    else -> {
                        throw FirebaseAuthException.Google.UnknownCredential
                    }
                }
            }
            else -> {
                throw FirebaseAuthException.Google.UnknownCredential
            }
        }
    }
}