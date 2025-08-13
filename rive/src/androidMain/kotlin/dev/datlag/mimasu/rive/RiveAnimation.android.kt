package dev.datlag.mimasu.rive

import androidx.annotation.RawRes
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.File
import dev.datlag.mimasu.rive.common.toAndroid

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
    var used by remember(bytes, artboardName, animationName, stateMachineName) {
        mutableStateOf(false)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {
                if (bytes.isNotEmpty()) {
                    setRiveBytes(
                        bytes = bytes,
                        artboardName = artboardName,
                        animationName = animationName,
                        stateMachineName = stateMachineName,
                        autoplay = autoplay,
                        fit = fit.toAndroid(),
                        alignment = alignment.toAndroid(),
                        loop = loop.toAndroid(),
                    )

                    used = true
                }
            }
        },
        update = {
            it.autoplay = autoplay
            it.fit = fit.toAndroid()
            it.alignment = alignment.toAndroid()
            it.controller.loop = loop.toAndroid()

            state(RiveState(it.apply {
                if (!used && bytes.isNotEmpty()) {
                    it.setRiveBytes(
                        bytes = bytes,
                        artboardName = artboardName,
                        animationName = animationName,
                        stateMachineName = stateMachineName,
                        autoplay = autoplay,
                        fit = fit.toAndroid(),
                        alignment = alignment.toAndroid(),
                        loop = loop.toAndroid(),
                    )

                    used = true
                }
            }))
        },
        onRelease = {
            it.stop()
            it.reset()
        },
        onReset = {
            it.reset()
        }
    )
}

@Composable
fun RiveAnimation(
    @RawRes resId: Int,
    artboardName: String? = null,
    animationName: String? = null,
    stateMachineName: String? = null,
    autoplay: Boolean = true,
    fit: RiveFit = RiveFit.Contain,
    alignment: RiveAlignment = RiveAlignment.Center,
    loop: RiveLoop = RiveLoop.Auto,
    modifier: Modifier = Modifier,
    state: (RiveState) -> Unit
) {
    var used by remember(resId, artboardName, animationName, stateMachineName) {
        mutableStateOf(false)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {
                setRiveResource(
                    resId = resId,
                    artboardName = artboardName,
                    animationName = animationName,
                    stateMachineName = stateMachineName,
                    autoplay = autoplay,
                    fit = fit.toAndroid(),
                    alignment = alignment.toAndroid(),
                    loop = loop.toAndroid(),
                )

                used = true
            }
        },
        update = {
            it.autoplay = autoplay
            it.fit = fit.toAndroid()
            it.alignment = alignment.toAndroid()
            it.controller.loop = loop.toAndroid()

            state(RiveState(it.apply {
                if (!used) {
                    setRiveResource(
                        resId = resId,
                        artboardName = artboardName,
                        animationName = animationName,
                        stateMachineName = stateMachineName,
                        autoplay = autoplay,
                        fit = fit.toAndroid(),
                        alignment = alignment.toAndroid(),
                        loop = loop.toAndroid(),
                    )

                    used = true
                }
            }))
        },
        onRelease = {
            it.stop()
            it.reset()
        },
        onReset = {
            it.reset()
        }
    )
}

@Composable
fun RiveAnimation(
    file: File,
    artboardName: String?,
    animationName: String?,
    stateMachineName: String?,
    autoplay: Boolean,
    fit: RiveFit,
    alignment: RiveAlignment,
    loop: RiveLoop,
    modifier: Modifier,
    state: (RiveState) -> Unit
) {
    var used by remember(file, artboardName, animationName, stateMachineName) {
        mutableStateOf(false)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RiveAnimationView(context).apply {
                setRiveFile(
                    file = file,
                    artboardName = artboardName,
                    animationName = animationName,
                    stateMachineName = stateMachineName,
                    autoplay = autoplay,
                    fit = fit.toAndroid(),
                    alignment = alignment.toAndroid(),
                    loop = loop.toAndroid(),
                )

                used = true
            }
        },
        update = {
            it.autoplay = autoplay
            it.fit = fit.toAndroid()
            it.alignment = alignment.toAndroid()
            it.controller.loop = loop.toAndroid()

            state(RiveState(it.apply {
                if (!used) {
                    setRiveFile(
                        file = file,
                        artboardName = artboardName,
                        animationName = animationName,
                        stateMachineName = stateMachineName,
                        autoplay = autoplay,
                        fit = fit.toAndroid(),
                        alignment = alignment.toAndroid(),
                        loop = loop.toAndroid(),
                    )

                    used = true
                }
            }))
        },
        onRelease = {
            it.stop()
            it.reset()
        },
        onReset = {
            it.reset()
        }
    )
}