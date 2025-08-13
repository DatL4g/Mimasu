package dev.datlag.mimasu.rive.common

import dev.datlag.mimasu.rive.external.RiveParameters
import org.khronos.webgl.Int8Array
import org.w3c.dom.events.Event
import kotlin.apply
import kotlin.js.unsafeCast

/** Returns a new [ByteArray] containing all the elements of this [Int8Array]. */
public inline fun Int8Array.toByteArray(): ByteArray =
    unsafeCast<ByteArray>()

/** Returns a new [Int8Array] containing all the elements of this [ByteArray]. */
public inline fun ByteArray.toInt8Array(): Int8Array =
    unsafeCast<Int8Array>()

operator fun RiveParameters.Companion.invoke(block: RiveParameters.() -> Unit): RiveParameters {
    return js("{}").unsafeCast<RiveParameters>().apply(block)
}

typealias EventCallback = (event: Event) -> Unit