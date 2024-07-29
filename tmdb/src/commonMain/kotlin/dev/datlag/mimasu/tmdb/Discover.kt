package dev.datlag.mimasu.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

// ToDo("replace with [DiscoverSorting] and [DiscoverType]")
interface Discover {

    /**
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
        @Header("Authorization") authorization: String,
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

    @GET("discover/tv")
    suspend fun tv(
        @Header("Authorization") authorization: String,
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
}