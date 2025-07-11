package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class ImmutableListSerializer<T>(
    serializer: KSerializer<T>
) : KSerializer<ImmutableList<T>> by CommonImmutableCollectionSerializer(
    serializer = serializer,
    transform = { decodedList -> decodedList.toImmutableList() }
)

typealias SerializableImmutableList<T> = @Serializable(ImmutableListSerializer::class) ImmutableList<T>