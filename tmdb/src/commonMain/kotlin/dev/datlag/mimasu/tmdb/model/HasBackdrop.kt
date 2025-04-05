package dev.datlag.mimasu.tmdb.model

import dev.datlag.mimasu.tmdb.TMDB

interface HasBackdrop {

    val backdropSource: String?

    val backdrop: String?
        get() = backdropSource?.ifBlank { null }?.let { "${TMDB.ORIGINAL_IMAGE}$it" }

    val backdropW500: String?
        get() = backdropSource?.ifBlank { null }?.let { "${TMDB.W500_IMAGE}$it" }

    val backdropW400: String?
        get() = backdropSource?.ifBlank { null }?.let { "${TMDB.W400_IMAGE}$it" }

    val backdropW300: String?
        get() = backdropSource?.ifBlank { null }?.let { "${TMDB.W300_IMAGE}$it" }

    val backdropW200: String?
        get() = backdropSource?.ifBlank { null }?.let { "${TMDB.W200_IMAGE}$it" }

    val hasBackdrop: Boolean
        get() = backdropSource?.isNotBlank() == true
                || backdrop?.isNotBlank() == true
                || backdropW500?.isNotBlank() == true
                || backdropW400?.isNotBlank() == true
                || backdropW300?.isNotBlank() == true
                || backdropW200?.isNotBlank() == true
}