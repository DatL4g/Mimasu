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
        val runtimeInMinutes: Int? = null,
    ) : Movie {

        @OptIn(ExperimentalSerializationApi::class)
        fun toByteArray(): ByteArray {
            return protobuf.encodeToByteArray(this)
        }
    }

    @Serializable
    @OptIn(ExperimentalSerializationApi::class)
    data class Response(
        val recapRange: Skipable? = null,
        val introRange: Skipable? = null,
        val outroRange: Skipable? = null,
        val previewRange: Skipable? = null,
        val sources: Map<String, List<String>> = emptyMap()
    ) : Movie {

        @Serializable
        data class Skipable(
            val start: Long? = null,
            val end: Long? = null,
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