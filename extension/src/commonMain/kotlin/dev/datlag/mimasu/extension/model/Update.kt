package dev.datlag.mimasu.extension.model

import dev.datlag.tooling.async.scopeCatching
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
data class Update(
    private val _available: Boolean? = null,
    private val _downloadUrl: String? = null,
    private val _viewUrl: String? = null
) {

    @Transient
    val available: Boolean = _available == true

    @Transient
    val downloadUrl: String? = if (_available == true) {
        _downloadUrl?.ifBlank { null }?.trim()
    } else {
        null
    }

    @Transient
    val viewUrl: String? = if (_available == true) {
        _viewUrl?.ifBlank { null }?.trim()
    } else {
        null
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        private val protobuf = ProtoBuf {
            encodeDefaults = false
        }

        @OptIn(ExperimentalSerializationApi::class)
        operator fun invoke(bytes: ByteArray?): Update? {
            if (bytes == null || bytes.isEmpty()) {
                return null
            }

            return scopeCatching {
                protobuf.decodeFromByteArray<Update>(bytes)
            }.getOrNull()
        }
    }
}