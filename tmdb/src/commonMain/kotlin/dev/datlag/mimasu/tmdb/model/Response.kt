package dev.datlag.mimasu.tmdb.model

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = Response.Serializer::class)
sealed interface Response {
    @SerialName("id")
    val id: Int

    @OptIn(ExperimentalSerializationApi::class)
    @SerialName("media_type")
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val mediaType: String?

    companion object Serializer : JsonContentPolymorphicSerializer<Response>(Response::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Response> {
            val mediaType = element.jsonObject["media_type"]?.jsonPrimitive?.contentOrNull

            return when {
                mediaType.equals("movie", ignoreCase = true) -> Movie.serializer()
                mediaType.equals("tv", ignoreCase = true) || mediaType.equals("show", ignoreCase = true) -> TV.serializer()
                mediaType.equals("person", ignoreCase = true) || mediaType.equals("people", ignoreCase = true) -> People.serializer()
                else -> {
                    when {
                        !element.jsonObject["title"]?.jsonPrimitive?.contentOrNull.isNullOrBlank() -> Movie.serializer()
                        else -> throw SerializationException("Serializer not found for: $mediaType")
                    }
                }
            }
        }
    }
}