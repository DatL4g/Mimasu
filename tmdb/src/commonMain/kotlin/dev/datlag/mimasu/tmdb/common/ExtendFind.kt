package dev.datlag.mimasu.tmdb.common

import dev.datlag.mimasu.tmdb.api.Find
import dev.datlag.mimasu.tmdb.model.FindSource

/**
 * Find data by IMDb ID's.
 */
suspend fun Find.imdb(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.IMDb.value,
    language = language
)

/**
 * Find data by Facebook ID's.
 */
suspend fun Find.facebook(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.Facebook.value,
    language = language
)

/**
 * Find data by Instagram ID's.
 */
suspend fun Find.instagram(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.Instagram.value,
    language = language
)

/**
 * Find data by TVDB ID's.
 */
suspend fun Find.tvdb(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.TVDB.value,
    language = language
)


/**
 * Find data by TikTok ID's.
 */
suspend fun Find.tiktok(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.TikTok.value,
    language = language
)

/**
 * Find data by Twitter ID's.
 */
suspend fun Find.twitter(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.Twitter.value,
    language = language
)

/**
 * Find data by Wikidata ID's.
 */
suspend fun Find.wikidata(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.Wikidata.value,
    language = language
)

/**
 * Find data by YouTube ID's.
 */
suspend fun Find.youtube(
    apiKey: String,
    id: String,
    language: String,
) = find(
    apiKey = apiKey,
    id = id,
    source = FindSource.YouTube.value,
    language = language
)