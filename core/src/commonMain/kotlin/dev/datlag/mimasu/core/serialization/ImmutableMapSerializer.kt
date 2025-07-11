package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

class ImmutableMapSerializer<K, V>(
    keySerializer: KSerializer<K>,
    valueSerializer: KSerializer<V>
) : KSerializer<ImmutableMap<K, V>> by CommonImmutableMapSerializer(
    keySerializer = keySerializer,
    valueSerializer = valueSerializer,
    transform = { decodedMap -> decodedMap.toImmutableMap() }
)

typealias SerializableImmutableMap<K, V> = @Serializable(ImmutableMapSerializer::class) ImmutableMap<K, V>