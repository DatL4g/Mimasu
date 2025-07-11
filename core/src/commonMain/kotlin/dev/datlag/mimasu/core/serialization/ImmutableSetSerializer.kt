package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class ImmutableSetSerializer<T>(
    serializer: KSerializer<T>
) : KSerializer<ImmutableSet<T>> by CommonImmutableCollectionSerializer(
    serializer = serializer,
    transform = { decodedList -> decodedList.toImmutableSet() }
)

typealias SerializableImmutableSet<T> = @Serializable(ImmutableSetSerializer::class) ImmutableSet<T>