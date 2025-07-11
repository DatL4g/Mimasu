package dev.datlag.mimasu.core.serialization

import kotlinx.collections.immutable.ImmutableCollection
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class CommonImmutableCollectionSerializer<T, C : ImmutableCollection<T>>(
    serializer: KSerializer<T>,
    private val transform: (Collection<T>) -> C
) : KSerializer<C> {
    private val listSerializer = ListSerializer(serializer)
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: C) {
        return listSerializer.serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): C {
        return listSerializer.deserialize(decoder).let(transform)
    }
}