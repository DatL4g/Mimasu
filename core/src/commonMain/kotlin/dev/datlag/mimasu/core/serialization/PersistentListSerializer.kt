package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class PersistentListSerializer<T>(
    serializer: KSerializer<T>
) : KSerializer<PersistentList<T>> by CommonImmutableCollectionSerializer(
    serializer = serializer,
    transform = { decodedList -> decodedList.toPersistentList() }
)

typealias SerializablePersistentList<T> = @Serializable(PersistentListSerializer::class) PersistentList<T>