package dev.datlag.mimasu.firebase.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.firestore
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.pearx.kasechange.toTitleCase
import net.pearx.kasechange.universalWordSplitter

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
    )
) {
    private val document: Flow<DocumentSnapshot> = Firebase.firestore.collection("user").document(firebase.uid).snapshots

    val info = document.map {
        it.data(Info.serializer())
    }

    data class GitHub(
        val linked: Boolean,
        val uid: String?,
        val name: String?
    )

    @Serializable
    data class Info(
        @SerialName("adult") val adult: Boolean = false,
        @SerialName("premium") val premium: Boolean = false
    )

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
