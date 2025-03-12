package dev.datlag.mimasu.tmdb.model.trending

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Response {
    val id: Int
    val mediaType: String?
}