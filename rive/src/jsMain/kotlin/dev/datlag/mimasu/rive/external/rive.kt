@file:JsModule("@rive-app/canvas")
@file:JsNonModule

package dev.datlag.mimasu.rive.external

import dev.datlag.mimasu.rive.common.EventCallback
import org.khronos.webgl.ArrayBuffer
import org.w3c.dom.HTMLCanvasElement

external interface RiveParameters {
    var canvas: HTMLCanvasElement
    var src: String?
    var buffer: ArrayBuffer?
    var artboard: String?
    var animations: String?
    var autoplay: Boolean?
    var stateMachines: String?

    var onLoad: EventCallback?
    var onLoadError: EventCallback?

    companion object
}

external class Rive(params: RiveParameters) {
    val isPlaying: Boolean = definedExternally

    fun resizeDrawingSurfaceToCanvas(customDevicePixelRatio: Number? = definedExternally)
    fun play(animationNames: String? = definedExternally, autoplay: Boolean = definedExternally)
    fun pause(animationNames: String? = definedExternally)
    fun stop(animationNames: String? = definedExternally)
    fun reset(params: RiveParameters? = definedExternally)
    fun cleanup()
}