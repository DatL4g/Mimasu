package dev.datlag.mimasu.extension.model

import dev.datlag.tooling.scopeCatching
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
sealed interface Movie {

    @Serializable
    data class Request(
        val tmdbId: Int? = null,
        val imdbId: String? = null,
        val wikidataId: String? = null,
        val title: String? = null,
        val originalTitle: String? = null,
        val firstReleaseYear: Int? = null,
        val isAnimation: Boolean? = null,
        val appLocale: String? = null
    ) : Movie {

        @OptIn(ExperimentalSerializationApi::class)
        fun toByteArray(): ByteArray {
            return protobuf.encodeToByteArray(this)
        }
    }

    @Serializable
    data class Response(
        val sources: Map<SourceInfo, List<String>> = emptyMap()
    ) : Movie {

        operator fun plus(other: Response): Response = this.copy(
            sources = this.sources + other.sources
        )

        fun isEmpty(): Boolean {
            return sources.isEmpty()
        }

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