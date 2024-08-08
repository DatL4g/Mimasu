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

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class Popularity(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "popularity"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class Revenue(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "revenue"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class PrimaryReleaseDate(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "primary_release_date"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class Title(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "title"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class VoteAverage(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "vote_average"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class VoteCount(
            override val direction: Direction?
        ): Movie() {
            override val label: String = "vote_count"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
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

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class Name(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "name"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class OriginalName(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "original_name"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class Popularity(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "popularity"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class VoteAverage(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "vote_average"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
        }

        @Serializable
        data class VoteCount(
            override val direction: Direction?
        ) : Tv() {
            override val label: String = "vote_count"

            override fun toString(): String = when (val current = direction) {
                null -> label
                else -> "$label.$current"
            }
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