package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

class PersistentSetSerializer<T>(
    serializer: KSerializer<T>
) : KSerializer<PersistentSet<T>> by CommonImmutableCollectionSerializer(
    serializer = serializer,
    transform = { decodedList -> decodedList.toPersistentSet() }
)

typealias SerializablePersistentSet<T> = @Serializable(PersistentSetSerializer::class) PersistentSet<T>