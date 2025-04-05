package dev.datlag.mimasu.tmdb.common

import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie
import dev.datlag.mimasu.tmdb.model.details.Movie
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

fun Movie?.backdrops(fallback: CommonMovie?): ImmutableList<String> = setOfNotNull(
    this?.backdrop,
    this?.backdropW500,
    this?.backdropW400,
    this?.backdropW300,
    this?.backdropW200,
    this?.backdropSource,

    fallback?.backdrop,
    fallback?.backdropW500,
    fallback?.backdropW400,
    fallback?.backdropW300,
    fallback?.backdropW200,
    fallback?.backdropSource
).toImmutableList()

fun Movie?.posters(fallback: CommonMovie?): ImmutableList<String> = setOfNotNull(
    this?.poster,
    this?.posterW500,
    this?.posterW400,
    this?.posterW300,
    this?.posterW200,
    this?.posterSource,

    fallback?.poster,
    fallback?.posterW500,
    fallback?.posterW400,
    fallback?.posterW300,
    fallback?.posterW200,
    fallback?.posterSource
).toImmutableList()