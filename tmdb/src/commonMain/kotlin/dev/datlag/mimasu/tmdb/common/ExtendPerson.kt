package dev.datlag.mimasu.tmdb.common

import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.model.HasLogo
import dev.datlag.mimasu.tmdb.model.People
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tmdb.model.details.Person
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet

fun HasLogo?.logos(
    people: People? = null,
    cast: Movie.Credits.Cast? = null,
    crew: Movie.Credits.Crew? = null
): SerializableImmutableSet<String> = setOfNotNull(
    this?.logo,
    this?.logoW500,
    this?.logoW400,
    this?.logoW300,
    this?.logoW200,
    this?.logoSource,

    people?.logo,
    people?.logoW500,
    people?.logoW400,
    people?.logoW300,
    people?.logoW200,
    people?.logoSource,

    cast?.logo,
    cast?.logoW500,
    cast?.logoW400,
    cast?.logoW300,
    cast?.logoW200,
    cast?.logoSource,

    crew?.logo,
    crew?.logoW500,
    crew?.logoW400,
    crew?.logoW300,
    crew?.logoW200,
    crew?.logoSource,
).toImmutableSet()