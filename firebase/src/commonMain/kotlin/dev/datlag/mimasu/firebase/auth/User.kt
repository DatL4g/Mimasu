package dev.datlag.mimasu.firebase.auth

import dev.datlag.tooling.async.scopeCatching
import dev.datlag.tooling.async.suspendCatching
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.app
import dev.gitlive.firebase.auth.ActionCodeSettings
import dev.gitlive.firebase.auth.AndroidPackageName
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import net.pearx.kasechange.toTitleCase
import net.pearx.kasechange.universalWordSplitter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class User(
    internal val firebase: FirebaseUser,
    val name: String? = firebase.displayName?.ifBlank { null }?.let(::normalizeName)
        ?: firebase.providerData.firstNotNullOfOrNull {
            it.displayName?.ifBlank { null }?.let(::normalizeName)
        }
        ?: firebase.email?.let(::emailToName)
        ?: firebase.providerData.firstNotNullOfOrNull {
            it.email?.let(::emailToName)
        },
    val email: String? = firebase.email?.ifBlank { null } ?: firebase.providerData.firstNotNullOfOrNull {
        it.email?.ifBlank { null }
    },
    val profilePictures: ImmutableSet<String> = firebase.providerData.mapNotNull {
        it.photoURL?.ifBlank { null }
    }.toImmutableSet(),
    val linkedGoogle: Boolean = firebase.providerData.any {
        it.providerId.equals("google", ignoreCase = true) || it.providerId.equals("google.com", ignoreCase = true)
    },
    val github: GitHub = GitHub(
        linked = firebase.providerData.any {
            it.providerId.equals("github", ignoreCase = true) || it.providerId.equals("github.com", ignoreCase = true)
        },
        uid = firebase.providerData.filter {
            it.providerId.equals("github", ignoreCase = true) || it.providerId.equals("github.com", ignoreCase = true)
        }.firstNotNullOfOrNull { it.uid.ifBlank { null } },
        name = firebase.providerData.filter {
            it.providerId.equals("github", ignoreCase = true) || it.providerId.equals("github.com", ignoreCase = true)
        }.firstNotNullOfOrNull { it.displayName?.ifBlank { null } }
    ),
    val isVerified: Boolean = firebase.isEmailVerified,
    val creationDate: Instant? = scopeCatching {
        firebase.metaData?.creationTime?.toLong()?.let(Instant::fromEpochMilliseconds)
    }.getOrNull(),
    val uid: String = firebase.uid
) {

    private val signInTime: Double
        get() = firebase.metaData?.lastSignInTime ?: 0.0

    suspend fun sendVerification() {
        firebase.sendEmailVerification(ActionCodeSettings(
            url = FirebaseAuthService.URL,
            androidPackageName = AndroidPackageName(
                packageName = FirebaseAuthService.PACKAGE_NAME,
                installIfNotAvailable = true
            ),
            canHandleCodeInApp = true
        ))
    }

    suspend fun reload() {
        firebase.reload()
        FirebaseAuthService.forceReload()
    }

    suspend fun delete() {
        firebase.delete()
    }

    operator fun plus(other: User): User = this.copy(
        firebase = if (this.signInTime < other.signInTime) {
            other.firebase
        } else {
            this.firebase
        },
        name = this.name?.ifBlank { null } ?: other.name,
        email = this.email?.ifBlank { null } ?: other.email,
        profilePictures = (this.profilePictures + other.profilePictures).toImmutableSet(),
        linkedGoogle = this.linkedGoogle || other.linkedGoogle,
        github = this.github + other.github,
        isVerified = this.isVerified || other.isVerified,
        creationDate = this.creationDate ?: other.creationDate
    )

    data class GitHub(
        val linked: Boolean,
        val uid: String?,
        val name: String?
    ) {

        operator fun plus(other: GitHub): GitHub = this.copy(
            linked = this.linked || other.linked,
            uid = this.uid?.ifBlank { null } ?: other.uid,
            name = this.name?.ifBlank { null } ?: other.name
        )
    }

    companion object {
        internal fun emailToName(mail: String): String? {
            if (mail.isBlank()) {
                return null
            }

            val part = mail.substringBefore(delimiter = '@', missingDelimiterValue = "")
            if (part.isBlank()) {
                return null
            }

            return normalizeName(part)
        }

        internal fun normalizeName(name: String): String {
            val formatted = name.toTitleCase(from = universalWordSplitter(treatDigitsAsUppercase = true))
            return formatted.replace("[0-9]+".toRegex(), "").trim().substringBefore(' ').trim().ifBlank { name }.takeIf { it.length >= 3 } ?: name
        }
    }
}
