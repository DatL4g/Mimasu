package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.tmdb.TMDB

interface HasPoster {

    val posterSource: String?

    val poster: String?
        get() = posterSource?.ifBlank { null }?.let { "${TMDB.ORIGINAL_IMAGE}$it" }

    val posterW500: String?
        get() = posterSource?.ifBlank { null }?.let { "${TMDB.W500_IMAGE}$it" }

    val hasPoster: Boolean
        get() = posterSource?.isNotBlank() == true
                || poster?.isNotBlank() == true
                || posterW500?.isNotBlank() == true
}