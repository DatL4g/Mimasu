package dev.datlag.mimasu.tmdb.common

import dev.datlag.mimasu.tmdb.Find
import dev.datlag.mimasu.tmdb.model.FindSource

suspend fun Find.imdb(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.IMDb.value,
    language = language
)

suspend fun Find.facebook(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.Facebook.value,
    language = language
)

suspend fun Find.instagram(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.Instagram.value,
    language = language
)

suspend fun Find.tvdb(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.TVDB.value,
    language = language
)

suspend fun Find.tiktok(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.TikTok.value,
    language = language
)

suspend fun Find.twitter(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.Twitter.value,
    language = language
)

suspend fun Find.wikidata(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.Wikidata.value,
    language = language
)

suspend fun Find.youtube(
    authorization: String,
    id: String,
    language: String,
) = find(
    authorization = authorization,
    id = id,
    source = FindSource.YouTube.value,
    language = language
)