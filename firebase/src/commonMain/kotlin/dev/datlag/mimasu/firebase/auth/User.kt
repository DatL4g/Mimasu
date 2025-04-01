package dev.datlag.mimasu.firebase.auth

import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import net.pearx.kasechange.toTitleCase
import net.pearx.kasechange.universalWordSplitter

data class User(
    private val firebase: FirebaseUser,
    val name: String? = firebase.displayName?.ifBlank { null }
        ?: firebase.email?.let(::emailToName)
        ?: firebase.providerData.firstNotNullOfOrNull {
            it.displayName?.ifBlank { null }
        }
        ?: firebase.providerData.firstNotNullOfOrNull {
            it.email?.let(::emailToName)
        },
    val email: String? = firebase.email?.ifBlank { null } ?: firebase.providerData.firstNotNullOfOrNull {
        it.email?.ifBlank { null }
    },
    val profilePictures: ImmutableSet<String> = firebase.providerData.mapNotNull {
        it.photoURL?.ifBlank { null }
    }.toImmutableSet()
) {

    companion object {
        internal fun emailToName(mail: String): String? {
            if (mail.isBlank()) {
                return null
            }

            val part = mail.substringBefore(delimiter = '@', missingDelimiterValue = "")
            if (part.isBlank()) {
                return null
            }

            val formatted = part.toTitleCase(from = universalWordSplitter(treatDigitsAsUppercase = true))
            return formatted.replace("[0-9]+".toRegex(), "").trim().substringBefore(' ').trim().ifBlank { null }
        }
    }
}
