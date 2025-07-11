package dev.datlag.mimasu.tmdb.common

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.model.HasBackdrop
import dev.datlag.mimasu.tmdb.model.HasPoster
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import dev.datlag.mimasu.tmdb.model.TV as CommonShow

fun HasBackdrop?.backdrops(fallback: CommonShow?): SerializableImmutableSet<String> = setOfNotNull(
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
).toImmutableSet()

fun HasPoster?.posters(fallbackShow: CommonShow?): SerializableImmutableSet<String> = setOfNotNull(
    this?.poster,
    this?.posterW500,
    this?.posterW400,
    this?.posterW300,
    this?.posterW200,
    this?.posterSource,

    fallbackShow?.poster,
    fallbackShow?.posterW500,
    fallbackShow?.posterW400,
    fallbackShow?.posterW300,
    fallbackShow?.posterW200,
    fallbackShow?.posterSource
).toImmutableSet()