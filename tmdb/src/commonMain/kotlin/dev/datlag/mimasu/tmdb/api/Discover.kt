package dev.datlag.mimasu.tmdb.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.mimasu.core.serializer.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.TMDB.Companion.ORIGINAL_IMAGE
import dev.datlag.mimasu.tmdb.TMDB.Companion.W500_IMAGE
import dev.datlag.mimasu.tmdb.api.Discover.MovieResponse.Movie
import io.ktor.client.statement.HttpResponse
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// ToDo("replace with [DiscoverSorting] and [DiscoverType]")
interface Discover {

    /**
     * Find movies using over 30 filters and sort options.
     *
     * @param certification use in conjunction with [region]
     * @param certificationGTE use in conjunction with [region]
     * @param certificationLTE use in conjunction with [region]
     * @param certificationCountry use in conjunction with the [certification], [certificationGTE]
     * and [certificationLTE] filters
     * @param watchRegion use in conjunction with [withWatchMonetizationTypes] or [withWatchProviders]
     * @param withCast can be a comma **,** or pipe **|** separated query
     * @param withCompanies can be a comma **,** or pipe **|** separated query
     * @param withCrew can be a comma **,** or pipe **|** separated query
     * @param withGenres can be a comma **,** or pipe **|** separated query
     * @param withKeywords can be a comma **,** or pipe **|** separated query
     * @param withPeople can be a comma **,** or pipe **|** separated query
     * @param withReleaseType possible values are: (1, 2, 3, 4, 5, 6)
     * @param withWatchMonetizationTypes possible values are: (flatrate, free, ads, rent, buy)
     * use in conjunction with [watchRegion] can be a comma **,** or pipe **|** separated query
     * @param withWatchProviders use in conjunction with [watchRegion]
     * can be a comma **,** or pipe **|** separated query
     */
    @GET("discover/movie")
    suspend fun movie(
        @Query("api_key") apiKey: String,
        @Query("certification") certification: String? = null,
        @Query("certification.gte") certificationGTE: String? = null,
        @Query("certification.lte") certificationLTE: String? = null,
        @Query("certification_country") certificationCountry: String? = null,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_video") includeVideo: Boolean = false,
        @Query("language") language: String,
        @Query("page") page: Int = 1,
        @Query("primary_release_year") primaryReleaseYear: Int? = null,
        @Query("primary_release_date.gte") primaryReleaseDateGTE: String? = null,
        @Query("primary_release_date.lte") primaryReleaseDateLTE: String? = null,
        @Query("region") region: String? = null,
        @Query("release_date.gte") releaseDateGTE: String? = null,
        @Query("release_date.lte") releaseDateLTE: String? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("vote_average.gte") voteAverageGTE: Float? = null,
        @Query("vote_average.lte") voteAverageLTE: Float? = null,
        @Query("vote_count.gte") voteCountGTE: Float? = null,
        @Query("vote_count.lte") voteCountLTE: Float? = null,
        @Query("watch_region") watchRegion: String? = null,
        @Query("with_cast") withCast: String? = null,
        @Query("with_companies") withCompanies: String? = null,
        @Query("with_crew") withCrew: String? = null,
        @Query("with_genres") withGenres: String? = null,
        @Query("with_keywords") withKeywords: String? = null,
        @Query("with_origin_country") withOriginCountry: String? = null,
        @Query("with_origin_language") withOriginLanguage: String? = null,
        @Query("with_people") withPeople: String? = null,
        @Query("with_release_type") withReleaseType: Int? = null,
        @Query("with_runtime.gte") withRuntimeGTE: Int? = null,
        @Query("with_runtime.lte") withRuntimeLTE: Int? = null,
        @Query("with_watch_monetization_types") withWatchMonetizationTypes: String? = null,
        @Query("with_watch_providers") withWatchProviders: String? = null,
        @Query("without_companies") withoutCompanies: String? = null,
        @Query("without_genres") withoutGenres: String? = null,
        @Query("without_keywords") withoutKeywords: String? = null,
        @Query("without_watch_providers") withoutWatchProviders: String? = null,
        @Query("year") year: Int? = null,
    ): HttpResponse

    /**
     * Find TV shows using over 30 filters and sort options.
     */
    @GET("discover/tv")
    suspend fun tv(
        @Query("api_key") apiKey: String,
        @Query("air_date.gte") airDateGTE: String? = null,
        @Query("air_date.lte") airDateLTE: String? = null,
        @Query("first_air_date_year") firstAirDateYear: Int? = null,
        @Query("first_air_date.gte") firstAirDateGTE: String? = null,
        @Query("first_air_date.lte") firstAirDateLTE: String? = null,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_null_first_air_dates") includeNullFirstAirDates: Boolean = false,
        @Query("language") language: String,
        @Query("page") page: Int = 1,
        @Query("screened_theatrically") screenedTheatrically: Boolean? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("timezone") timezone: String? = null,
        @Query("vote_average.gte") voteAverageGTE: Float? = null,
        @Query("vote_average.lte") voteAverageLTE: Float? = null,
        @Query("vote_count.gte") voteCountGTE: Float? = null,
        @Query("vote_count.lte") voteCountLTE: Float? = null,
        @Query("watch_region") watchRegion: String? = null,
        @Query("with_companies") withCompanies: String? = null,
        @Query("with_genres") withGenres: String? = null,
        @Query("with_keywords") withKeywords: String? = null,
        @Query("with_networks") withNetworks: Int? = null,
        @Query("with_origin_country") withOriginCountry: String? = null,
        @Query("with_origin_language") withOriginLanguage: String? = null,
        @Query("with_runtime.gte") withRuntimeGTE: Int? = null,
        @Query("with_runtime.lte") withRuntimeLTE: Int? = null,
        @Query("with_status") withStatus: String? = null,
        @Query("with_watch_monetization_types") withWatchMonetizationTypes: String? = null,
        @Query("with_watch_providers") withWatchProviders: String? = null,
        @Query("without_companies") withoutCompanies: String? = null,
        @Query("without_genres") withoutGenres: String? = null,
        @Query("without_keywords") withoutKeywords: String? = null,
        @Query("without_watch_providers") withoutWatchProviders: String? = null,
        @Query("with_type") withType: String? = null,
    ): HttpResponse

    @Serializable
    data class MovieResponse(
        @SerialName("page") val page: Int = 1,
        @SerialName("results") val results: SerializableImmutableSet<Movie>,
        @SerialName("total_pages") val totalPages: Int = page,
        @SerialName("total_results") val totalResults: Int = results.size,
    ) {
        @Serializable
        data class Movie(
            @SerialName("id") val id: Int,
            @SerialName("title") val title: String,
            @SerialName("original_title") val originalTitle: String? = null,
            @SerialName("backdrop_path") val backdropPath: String? = null,
            @SerialName("overview") val overview: String? = null,
            @SerialName("poster_path") val posterPath: String? = null,
            @SerialName("adult") val adult: Boolean = false,
            @SerialName("original_language") val originalLanguage: String? = null,
            @SerialName("genre_ids") val genreIds: SerializableImmutableSet<Int> = persistentSetOf(),
            @SerialName("video") val video: Boolean = false,
            @SerialName("vote_average") val voteAverage: Float = 0F,
            @SerialName("vote_count") val voteCount: Int = 0
        ) {
            @Transient
            val backdropPicture: String? = backdropPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

            @Transient
            val backdropPictureW500: String? = backdropPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

            @Transient
            val posterPicture: String? = posterPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

            @Transient
            val posterPictureW500: String? = posterPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

            @Transient
            val alternativeName: String? = if (originalTitle.equals(title, ignoreCase = true)) {
                null
            } else {
                originalTitle
            }
        }
    }

    @Serializable
    data class TVResponse(
        @SerialName("page") val page: Int = 1,
        @SerialName("results") val results: SerializableImmutableSet<TV>,
        @SerialName("total_pages") val totalPages: Int = page,
        @SerialName("total_results") val totalResults: Int = results.size,
    ) {
        @Serializable
        data class TV(
            @SerialName("id") val id: Int,
            @SerialName("name") val name: String,
            @SerialName("original_name") val originalName: String? = null,
            @SerialName("backdrop_path") val backdropPath: String? = null,
            @SerialName("overview") val overview: String? = null,
            @SerialName("poster_path") val posterPath: String? = null,
            @SerialName("adult") val adult: Boolean = false,
            @SerialName("original_language") val originalLanguage: String? = null,
            @SerialName("genre_ids") val genreIds: SerializableImmutableSet<Int> = persistentSetOf(),
            @SerialName("vote_average") val voteAverage: Float = 0F,
            @SerialName("vote_count") val voteCount: Int = 0
        ) {
            @Transient
            val backdropPicture: String? = backdropPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

            @Transient
            val backdropPictureW500: String? = backdropPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

            @Transient
            val posterPicture: String? = posterPath?.ifBlank { null }?.let { "$ORIGINAL_IMAGE$it" }

            @Transient
            val posterPictureW500: String? = posterPath?.ifBlank { null }?.let { "$W500_IMAGE$it" }

            @Transient
            val alternativeName: String? = if (originalName.equals(name, ignoreCase = true)) {
                null
            } else {
                originalName
            }
        }
    }
}