package dev.datlag.mimasu.tmdb.model.trending

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Response {
    @SerialName("id") val id: Int
    @SerialName("media_type") val mediaType: String?
}