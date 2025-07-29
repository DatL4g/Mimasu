package dev.datlag.mimasu.firebase.auth.model

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleDoHResponse(
    @SerialName("Authority") val authorities: SerializableImmutableSet<Answer>? = persistentSetOf(),
    @SerialName("Answer") val answers: SerializableImmutableSet<Answer>? = persistentSetOf(),
) {

    fun hasAuthorityType(type: Int): Boolean {
        return authorities?.any { a -> a.type == type } ?: false
    }

    fun hasAnswerType(type: Int): Boolean {
        return answers?.any { a -> a.type == type } ?: false
    }

    fun hasAuthorityOrAnswerType(type: Int): Boolean {
        return hasAuthorityType(type) || hasAnswerType(type)
    }

    @Serializable
    data class Answer(
        @SerialName("name") val name: String,
        @SerialName("type") val type: Int = 1,
        @SerialName("data") val data: String? = null
    )
}
