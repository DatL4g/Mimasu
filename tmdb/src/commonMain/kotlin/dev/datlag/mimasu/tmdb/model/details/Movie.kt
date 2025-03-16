package dev.datlag.mimasu.tmdb.model.details

import dev.datlag.mimasu.tmdb.model.HasBackdrop
import dev.datlag.mimasu.tmdb.model.HasPoster
import dev.datlag.mimasu.tmdb.model.HasLogo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("adult") val adult: Boolean = true,
    @SerialName("backdrop_path") override val backdropSource: String? = null,
    @SerialName("belongs_to_collection") val belongsToCollection: String? = null,
    @SerialName("budget") val budget: Int = 0,
    @SerialName("genres") val genres: Set<Genre> = emptySet(),
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("id") val id: Int,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("popularity") val popularity: Float = 0F,
    @SerialName("poster_path") override val posterSource: String? = null,
    @SerialName("production_companies") val productionCompanies: Set<ProductionCompany> = emptySet(),
    @SerialName("production_countries") val productionCountries: Set<ProductionCountries> = emptySet(),
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("revenue") val revenue: Int = 0,
    @SerialName("runtime") val runtime: Int = 0,
    @SerialName("spoken_languages") val spokenLanguages: Set<SpokenLanguage> = emptySet(),
    @SerialName("status") val status: String? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("title") val title: String,
    @SerialName("video") val video: Boolean = true,
    @SerialName("vote_average") val voteAverage: Float = 0F,
    @SerialName("vote_count") val voteCount: Int = 0
) : HasBackdrop, HasPoster {

    @Serializable
    data class Genre(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String
    )

    @Serializable
    data class ProductionCompany(
        @SerialName("id") val id: Int,
        @SerialName("logo_path") override val logoSource: String? = null,
        @SerialName("name") val name: String,
        @SerialName("origin_country") val originCountry: String? = null
    ): HasLogo

    @Serializable
    data class ProductionCountries(
        @SerialName("iso_3166_1") val iso: String? = null,
        @SerialName("name") val name: String? = null
    )

    @Serializable
    data class SpokenLanguage(
        @SerialName("english_name") val englishName: String? = null,
        @SerialName("iso_639_1") val iso: String? = null,
        @SerialName("name") val name: String
    )

}
