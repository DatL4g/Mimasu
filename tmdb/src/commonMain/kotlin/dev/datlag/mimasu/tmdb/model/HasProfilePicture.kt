package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.tmdb.TMDB

interface HasProfilePicture {

    val profileSource: String?

    val profile: String?
        get() = profileSource?.ifBlank { null }?.let { "${TMDB.ORIGINAL_IMAGE}$it" }

    val profileW500: String?
        get() = profileSource?.ifBlank { null }?.let { "${TMDB.W500_IMAGE}$it" }

    val hasProfilePicture: Boolean
        get() = profileSource?.isNotBlank() == true
                || profile?.isNotBlank() == true
                || profileW500?.isNotBlank() == true
}