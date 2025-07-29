package dev.datlag.mimasu.firebase.auth.provider.email

import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.api.GoogleDoH
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
import kotlin.time.ExperimentalTime

class FirebaseEmailAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource,
    private val googleDoH: GoogleDoH
) : FirebaseAuthProvider<EmailAuthParams>(firebaseAuthDataSource) {

    @OptIn(ExperimentalTime::class)
    override suspend fun signIn(
        params: EmailAuthParams
    ): Result<User> = suspendCatching {
        val currentUser = firebaseAuthDataSource.currentUser

        currentUser?.firebase?.linkWithCredential(
            params.asCredential()
        )?.user?.let(::User)?.let {
            return@suspendCatching it
        }

        val signInResult = suspendCatching {
            firebaseAuthDataSource.authenticateWithEmailAndPassword(
                email = params.email,
                password = params.password
            )
        }.onSuccess { user ->
            user?.let { return@suspendCatching it }
        }

        val signUpResult = suspendCatching {
            firebaseAuthDataSource.createUserWithEmailAndPassword(
                email = params.email,
                password = params.password
            )
        }.onSuccess { user ->
            user?.let { return@suspendCatching it }
        }

        signInResult.getOrNull() ?: signUpResult.getOrNull() ?: firebaseAuthDataSource.currentUser ?: run {
            throw signInResult.exceptionOrNull()
                ?: signUpResult.exceptionOrNull()
                ?: FirebaseAuthException.UnknownUser(FirebaseProvider.Email)
        }
    }

    suspend fun emailValidity(email: String): Boolean {
        val mxResponse = suspendCatching {
            googleDoH.resolve(
                name = email.substringAfter('@'),
                type = TYPE_MX
            )
        }.getOrNull() ?: return false

        val mxValid = mxResponse.hasAuthorityOrAnswerType(TYPE_ID_MX)
                || mxResponse.hasAuthorityOrAnswerType(TYPE_ID_RP)
                || mxResponse.hasAuthorityOrAnswerType(TYPE_ID_SOA)

        if (!mxValid) {
            return false
        }

        return true
    }

    companion object {
        private const val TYPE_MX = "MX"
        private const val TYPE_ID_MX = 15
        private const val TYPE_ID_RP = 17
        private const val TYPE_ID_SOA = 6
    }
}