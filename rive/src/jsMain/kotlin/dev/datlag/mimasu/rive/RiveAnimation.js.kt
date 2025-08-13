package dev.datlag.mimasu.rive

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.WebElementView
import dev.datlag.mimasu.rive.common.invoke
import dev.datlag.mimasu.rive.common.toInt8Array
import dev.datlag.mimasu.rive.external.Rive
import dev.datlag.mimasu.rive.external.RiveParameters
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun RiveAnimation(
    bytes: ByteArray,
    artboardName: String?,
    animationName: String?,
    stateMachineName: String?,
    autoplay: Boolean,
    fit: RiveFit,
    alignment: RiveAlignment,
    loop: RiveLoop,
    modifier: Modifier,
    onUnavailable: () -> Unit,
    state: (RiveState) -> Unit
) {
    var rive by remember { mutableStateOf<Rive?>(null) }

    WebElementView(
        modifier = modifier,
        factory = {
            document.createElement("canvas") as HTMLCanvasElement
        },
        update = { canvas ->
            if (!isWebGL2Supported(canvas)) {
                onUnavailable()
            }

            val params = RiveParameters {
                this.canvas = canvas
                this.buffer = bytes.toInt8Array().buffer
                this.artboard = artboardName
                this.animations = animationName
                this.autoplay = autoplay
                this.stateMachines = stateMachineName
                this.onLoadError = {
                    onUnavailable()
                }
            }
            rive?.cleanup()
            rive = Rive(params).also {
                it.resizeDrawingSurfaceToCanvas()
                state(RiveState(it))
            }
        },
        onRelease = {
            rive?.cleanup()
        }
    )
}

private fun isWebGL2Supported(canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement): Boolean {
    return (canvas.getContext("webgl2") != null)
}

private fun isWebGL1Supported(canvas: HTMLCanvasElement = document.createElement("canvas") as HTMLCanvasElement): Boolean {
    return (canvas.getContext("webgl") ?: canvas.getContext("experimental-webgl")) != null
}