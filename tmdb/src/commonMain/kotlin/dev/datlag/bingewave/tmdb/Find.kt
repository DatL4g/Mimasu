package dev.datlag.bingewave.tmdb

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import dev.datlag.bingewave.tmdb.model.FindSource
import io.ktor.client.statement.HttpResponse

interface Find {

    @GET("find/{id}")
    suspend fun find(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Query("external_source") source: String,
        @Query("language") language: String,
    ): HttpResponse

    suspend fun imdb(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.IMDb.value,
        language = language
    )

    suspend fun facebook(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.Facebook.value,
        language = language
    )

    suspend fun instagram(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.Instagram.value,
        language = language
    )

    suspend fun tvdb(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.TVDB.value,
        language = language
    )

    suspend fun tiktok(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.TikTok.value,
        language = language
    )

    suspend fun twitter(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.Twitter.value,
        language = language
    )

    suspend fun wikidata(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.Wikidata.value,
        language = language
    )

    suspend fun youtube(
        authorization: String,
        id: String,
        language: String,
    ) = find(
        authorization = authorization,
        id = id,
        source = FindSource.YouTube.value,
        language = language
    )
}