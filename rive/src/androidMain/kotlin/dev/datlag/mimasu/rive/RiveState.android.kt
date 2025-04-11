package dev.datlag.mimasu.rive

import app.rive.runtime.kotlin.RiveAnimationView

actual data class RiveState(
    val view: RiveAnimationView
) {

    actual val isPlaying: Boolean
        get() = view.isPlaying

    actual fun fire(stateMachineName: String, inputName: String) {
        view.fireState(
            stateMachineName = stateMachineName,
            inputName = inputName
        )
    }

    actual fun setBoolean(stateMachineName: String, inputName: String, value: Boolean) {
        view.setBooleanState(
            stateMachineName = stateMachineName,
            inputName = inputName,
            value = value
        )
    }

    actual fun setNumber(stateMachineName: String, inputName: String, value: Float) {
        view.setNumberState(
            stateMachineName = stateMachineName,
            inputName = inputName,
            value = value
        )
    }

    actual fun pause() {
        view.pause()
    }

    actual fun stop() {
        view.stop()
    }

    actual fun reset() {
        view.reset()
    }

}