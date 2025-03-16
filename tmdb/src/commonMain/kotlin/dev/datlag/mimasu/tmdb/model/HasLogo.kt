package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.tmdb.TMDB

interface HasLogo {

    val logoSource: String?

    val logo: String?
        get() = logoSource?.ifBlank { null }?.let { "${TMDB.ORIGINAL_IMAGE}$it" }

    val logoW500: String?
        get() = logoSource?.ifBlank { null }?.let { "${TMDB.W500_IMAGE}$it" }

    val hasLogo: Boolean
        get() = logoSource?.isNotBlank() == true
                || logo?.isNotBlank() == true
                || logoW500?.isNotBlank() == true
}