package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.tmdb.TMDB

interface HasBackdrop {

    val backdropSource: String?

    val backdrop: String?
        get() = backdropSource?.ifBlank { null }?.let { "${TMDB.ORIGINAL_IMAGE}$it" }

    val backdropW500: String?
        get() = backdropSource?.ifBlank { null }?.let { "${TMDB.W500_IMAGE}$it" }

    val hasBackdrop: Boolean
        get() = backdropSource?.isNotBlank() == true
                || backdrop?.isNotBlank() == true
                || backdropW500?.isNotBlank() == true
}