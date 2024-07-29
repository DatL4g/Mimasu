package dev.datlag.mimasu.tmdb.model

import kotlinx.serialization.Serializable

@Serializable
sealed class FindSource : CharSequence {

    abstract val value: String

    override val length: Int
        get() = value.length

    override operator fun get(index: Int): Char {
        return value[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return value.subSequence(startIndex, endIndex)
    }

    override fun toString(): String {
        return value
    }

    @Serializable
    data object IMDb : FindSource() {
        override val value: String = "imdb_id"
    }

    @Serializable
    data object Facebook : FindSource() {
        override val value: String = "facebook_id"
    }

    @Serializable
    data object Instagram : FindSource() {
        override val value: String = "instagram_id"
    }

    @Serializable
    data object TVDB : FindSource() {
        override val value: String = "tvdb_id"
    }

    @Serializable
    data object TikTok : FindSource() {
        override val value: String = "tiktok_id"
    }

    @Serializable
    data object Twitter : FindSource() {
        override val value: String = "twitter_id"
    }

    @Serializable
    data object Wikidata : FindSource() {
        override val value: String = "wikidata_id"
    }

    @Serializable
    data object YouTube : FindSource() {
        override val value: String = "youtube_id"
    }
}