package dev.datlag.mimasu.tmdb.common

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.model.HasBackdrop
import dev.datlag.mimasu.tmdb.model.HasPoster
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie
import dev.datlag.mimasu.tmdb.model.details.Movie
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

fun HasBackdrop?.backdrops(fallbackMovie: CommonMovie?): SerializableImmutableSet<String> = setOfNotNull(
    this?.backdrop,
    this?.backdropW500,
    this?.backdropW400,
    this?.backdropW300,
    this?.backdropW200,
    this?.backdropSource,

    fallbackMovie?.backdrop,
    fallbackMovie?.backdropW500,
    fallbackMovie?.backdropW400,
    fallbackMovie?.backdropW300,
    fallbackMovie?.backdropW200,
    fallbackMovie?.backdropSource
).toImmutableSet()

fun HasPoster?.posters(fallbackMovie: CommonMovie?): SerializableImmutableSet<String> = setOfNotNull(
    this?.poster,
    this?.posterW500,
    this?.posterW400,
    this?.posterW300,
    this?.posterW200,
    this?.posterSource,

    fallbackMovie?.poster,
    fallbackMovie?.posterW500,
    fallbackMovie?.posterW400,
    fallbackMovie?.posterW300,
    fallbackMovie?.posterW200,
    fallbackMovie?.posterSource
).toImmutableSet()