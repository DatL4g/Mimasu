package dev.datlag.mimasu.extension.model

import dev.datlag.tooling.scopeCatching
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
sealed interface Show {

    @Serializable
    data class Request(
        val tmdbId: Int? = null,
        val imdbId: String? = null,
        val wikidataId: String? = null,
        val title: String? = null,
        val originalTitle: String? = null,
        val firstReleaseYear: Int? = null,
        val isAnimation: Boolean? = null,
        val numberOfNormalSeasons: Int? = null,
        val hasSpecialSeason: Boolean? = null,
        val season: Int? = null,
        val appLocale: String? = null
    ) : Show {

        @OptIn(ExperimentalSerializationApi::class)
        fun toByteArray(): ByteArray {
            return protobuf.encodeToByteArray(this)
        }
    }

    @Serializable
    data class EpisodeRequest(
        val episodeNumber: Int? = null,
        val episodeTitle: String? = null,
        val numberOfNormalSeasons: Int? = null,
        val hasSpecialSeason: Boolean? = null,
        val season: Int? = null,
        val appLocale: String? = null
    ) : Show {

        @OptIn(ExperimentalSerializationApi::class)
        fun toByteArray(): ByteArray {
            return protobuf.encodeToByteArray(this)
        }
    }

    @Serializable
    data class Response(
        val recapRange: Skipable? = null,
        val introRange: Skipable? = null,
        val outroRange: Skipable? = null,
        val previewRange: Skipable? = null,
        val sources: Map<SourceInfo, List<String>> = emptyMap()
    ) : Show {

        operator fun plus(other: Response): Response = this.copy(
            recapRange = this.recapRange ?: other.recapRange,
            introRange = this.introRange ?: other.introRange,
            outroRange = this.outroRange ?: other.outroRange,
            previewRange = this.previewRange ?: other.previewRange,
            sources = this.sources + other.sources
        )

        fun isEmpty(): Boolean {
            return sources.isEmpty()
        }

        @Serializable
        data class Skipable(
            val start: Long? = null,
            val end: Long? = null
        )

        @Serializable
        data class SourceInfo(
            val sourceTitle: String? = null,
            val sourceLocale: String? = null,
            val locale: String? = null
        )

        companion object {

            @OptIn(ExperimentalSerializationApi::class)
            operator fun invoke(bytes: ByteArray?): Response? {
                if (bytes == null || bytes.isEmpty()) {
                    return null
                }

                return scopeCatching {
                    protobuf.decodeFromByteArray<Response>(bytes)
                }.getOrNull()
            }
        }
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val protobuf = ProtoBuf {
            encodeDefaults = false
        }
    }
}