package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.tmdb.TMDB

interface HasPoster {

    val posterSource: String?

    val poster: String?
        get() = posterSource?.ifBlank { null }?.let { "${TMDB.ORIGINAL_IMAGE}$it" }

    val posterW500: String?
        get() = posterSource?.ifBlank { null }?.let { "${TMDB.W500_IMAGE}$it" }

    val posterW400: String?
        get() = posterSource?.ifBlank { null }?.let { "${TMDB.W400_IMAGE}$it" }

    val posterW300: String?
        get() = posterSource?.ifBlank { null }?.let { "${TMDB.W300_IMAGE}$it" }

    val posterW200: String?
        get() = posterSource?.ifBlank { null }?.let { "${TMDB.W200_IMAGE}$it" }

    val hasPoster: Boolean
        get() = posterSource?.isNotBlank() == true
                || poster?.isNotBlank() == true
                || posterW500?.isNotBlank() == true
                || posterW400?.isNotBlank() == true
                || posterW300?.isNotBlank() == true
                || posterW200?.isNotBlank() == true
}