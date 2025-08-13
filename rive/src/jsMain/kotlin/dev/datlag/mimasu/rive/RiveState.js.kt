package dev.datlag.mimasu.rive

import dev.datlag.mimasu.rive.external.Rive

actual data class RiveState(
    val instance: Rive
) {
    actual val isPlaying: Boolean
        get() = instance.isPlaying

    actual fun fire(stateMachineName: String, inputName: String) {
    }

    actual fun setBoolean(
        stateMachineName: String,
        inputName: String,
        value: Boolean
    ) {
    }

    actual fun setNumber(
        stateMachineName: String,
        inputName: String,
        value: Float
    ) {
    }

    actual fun pause() {
        instance.pause()
    }

    actual fun stop() {
        instance.stop()
    }

    actual fun reset() {
        instance.reset()
    }
}