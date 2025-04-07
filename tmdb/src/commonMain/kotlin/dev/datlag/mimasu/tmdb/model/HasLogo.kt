package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.tmdb.TMDB

interface HasLogo {

    val logoSource: String?

    val logo: String?
        get() = logoSource?.ifBlank { null }?.let { "${TMDB.ORIGINAL_IMAGE}$it" }

    val logoW500: String?
        get() = logoSource?.ifBlank { null }?.let { "${TMDB.W500_IMAGE}$it" }

    val logoW400: String?
        get() = logoSource?.ifBlank { null }?.let { "${TMDB.W400_IMAGE}$it" }

    val logoW300: String?
        get() = logoSource?.ifBlank { null }?.let { "${TMDB.W300_IMAGE}$it" }

    val logoW200: String?
        get() = logoSource?.ifBlank { null }?.let { "${TMDB.W200_IMAGE}$it" }

    val hasLogo: Boolean
        get() = logoSource?.isNotBlank() == true
                || logo?.isNotBlank() == true
                || logoW500?.isNotBlank() == true
                || logoW400?.isNotBlank() == true
                || logoW300?.isNotBlank() == true
                || logoW200?.isNotBlank() == true
}