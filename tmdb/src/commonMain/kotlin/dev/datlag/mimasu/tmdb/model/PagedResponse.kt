package dev.datlag.mimasu.tmdb.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PagedResponse<T>(
    @SerialName("page") val page: Int = 0,
    @SerialName("results") val results: List<@Contextual T>,
    @SerialName("total_pages") val totalPages: Int = page,
    @SerialName("total_results") val totalResults: Int = results.size
)
