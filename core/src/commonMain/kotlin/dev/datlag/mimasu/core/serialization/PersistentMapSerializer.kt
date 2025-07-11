package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class PersistentMapSerializer<K, V>(
    keySerializer: KSerializer<K>,
    valueSerializer: KSerializer<V>
) : KSerializer<PersistentMap<K, V>> by CommonImmutableMapSerializer(
    keySerializer = keySerializer,
    valueSerializer = valueSerializer,
    transform = { decodedMap -> decodedMap.toPersistentMap() }
)

typealias SerializablePersistentMap<K, V> = @Serializable(PersistentMapSerializer::class) PersistentMap<K, V>