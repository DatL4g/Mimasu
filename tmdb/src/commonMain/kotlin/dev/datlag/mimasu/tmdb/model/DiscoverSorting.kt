package dev.datlag.mimasu.tmdb.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface DiscoverSorting {

    val direction: Direction?
        get() = null

    @Serializable
    sealed class Movie : DiscoverSorting, CharSequence {

        abstract val label: String

        override val length: Int
            get() = label.length

        override fun get(index: Int): Char {
            return label[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return label.subSequence(startIndex, endIndex)
        }

        override fun toString(): String = when (val current = direction) {
            null -> label
            else -> "$label.$current"
        }

        @Serializable
        data class OriginalTitle(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "original_title"
        }

        @Serializable
        data class Popularity(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "popularity"
        }

        @Serializable
        data class Revenue(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "revenue"
        }

        @Serializable
        data class PrimaryReleaseDate(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "primary_release_date"
        }

        @Serializable
        data class Title(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "title"
        }

        @Serializable
        data class VoteAverage(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "vote_average"
        }

        @Serializable
        data class VoteCount(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "vote_count"
        }
    }

    @Serializable
    sealed class Tv : DiscoverSorting, CharSequence {

        abstract val label: String

        override val length: Int
            get() = label.length

        override fun get(index: Int): Char {
            return label[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return label.subSequence(startIndex, endIndex)
        }

        override fun toString(): String = when (val current = direction) {
            null -> label
            else -> "$label.$current"
        }

        @Serializable
        data class FirstAirDate(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "first_air_date"
        }

        @Serializable
        data class Name(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "name"
        }

        @Serializable
        data class OriginalName(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "original_name"
        }

        @Serializable
        data class Popularity(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "popularity"
        }

        @Serializable
        data class VoteAverage(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "vote_average"
        }

        @Serializable
        data class VoteCount(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "vote_count"
        }
    }

    @Serializable
    sealed class Direction {

        @Serializable
        data object Ascending : Direction() {
            override fun toString(): String = "asc"
        }

        @Serializable
        data object Descending : Direction() {
            override fun toString(): String = "desc"
        }
    }
}