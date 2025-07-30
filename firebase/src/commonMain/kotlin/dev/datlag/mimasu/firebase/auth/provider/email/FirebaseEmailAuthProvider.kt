package dev.datlag.mimasu.firebase.auth.provider.email

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.firebase.auth.User
import dev.datlag.mimasu.firebase.auth.api.DisposableDebounce
import dev.datlag.mimasu.firebase.auth.api.GoogleDoH
import dev.datlag.mimasu.firebase.auth.datasource.FirebaseAuthDataSource
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthException
import dev.datlag.mimasu.firebase.auth.provider.FirebaseAuthProvider
import dev.datlag.mimasu.firebase.auth.provider.FirebaseProvider
import dev.datlag.tooling.async.suspendCatching
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class FirebaseEmailAuthProvider(
    firebaseAuthDataSource: FirebaseAuthDataSource,
    private val googleDoH: GoogleDoH?,
    private val disposableDebounce: DisposableDebounce?
) : FirebaseAuthProvider<EmailAuthParams>(firebaseAuthDataSource) {

    private var disposableDomainsTimestamp = 0L
    @OptIn(ExperimentalTime::class)
    private var disposableDomains: SerializableImmutableSet<String> = persistentSetOf()
        set(value) {
            field = value.also {
                if (it.isNotEmpty()) {
                    disposableDomainsTimestamp = Clock.System.now().epochSeconds
                }
            }.ifEmpty { field }
        }
    private val disposableDomainsMutex = Mutex()

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

    suspend fun emailValidity(email: String): Boolean = coroutineScope {
        val parts = EmailParts(email)

        val serverAvailable = async { emailServerAvailable(parts) }
        val disposable = async { emailDisposable(email, parts) }

        return@coroutineScope serverAvailable.await() && !disposable.await()
    }

    private suspend fun emailServerAvailable(parts: EmailParts): Boolean {
        val doh = googleDoH ?: return true

        val response = suspendCatching {
            doh.resolve(
                name = parts.hostname,
                type = TYPE_MX
            )
        }.getOrNull() ?: return true

        return response.hasAuthorityOrAnswerType(TYPE_ID_MX)
                || response.hasAuthorityOrAnswerType(TYPE_ID_RP)
                || response.hasAuthorityOrAnswerType(TYPE_ID_SOA)
    }

    private suspend fun emailDisposable(email: String, parts: EmailParts): Boolean {
        val domains = suspendCatching { retrieveDisposableDomains() }.getOrNull() ?: disposableDomains

        return if (domains.isNotEmpty()) {
            domains.contains(parts.hostname)
        } else {
            suspendCatching {
                disposableDebounce?.checkDisposable(email = email)
            }.getOrNull()?.disposable ?: false
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun retrieveDisposableDomains() = disposableDomainsMutex.withLock {
        val domains = getCurrentDisposableDomains().also {
            if (it.isNotEmpty()) {
                return@withLock it
            }
        }

        disposableDomains = disposableDebounce?.disposableDomains()?.toImmutableSet() ?: domains
        return@withLock disposableDomains
    }

    @OptIn(ExperimentalTime::class)
    private fun getCurrentDisposableDomains(): SerializableImmutableSet<String> {
        return disposableDomains.let {
            if (Clock.System.now().minus(12.hours).epochSeconds > disposableDomainsTimestamp) {
                persistentSetOf()
            } else {
                it
            }
        }
    }

    companion object {
        private const val TYPE_MX = "MX"
        private const val TYPE_ID_MX = 15
        private const val TYPE_ID_RP = 17
        private const val TYPE_ID_SOA = 6
    }
}