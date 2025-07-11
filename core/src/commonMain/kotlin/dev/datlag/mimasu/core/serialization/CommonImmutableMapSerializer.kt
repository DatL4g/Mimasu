package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class CommonImmutableMapSerializer<K, V, M : ImmutableMap<K, V>>(
    keySerializer: KSerializer<K>,
    valueSerializer: KSerializer<V>,
    private val transform: (Map<K, V>) -> M
) : KSerializer<M> {
    private val mapSerializer = MapSerializer(keySerializer, valueSerializer)
    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: M) {
        return mapSerializer.serialize(encoder, value.toMap())
    }

    override fun deserialize(decoder: Decoder): M {
        return mapSerializer.deserialize(decoder).let(transform)
    }
}