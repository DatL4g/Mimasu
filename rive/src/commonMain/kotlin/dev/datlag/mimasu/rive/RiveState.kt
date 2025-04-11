package dev.datlag.mimasu.rive

expect class RiveState {

    val isPlaying: Boolean

    fun fire(stateMachineName: String, inputName: String)
    fun setBoolean(stateMachineName: String, inputName: String, value: Boolean)
    fun setNumber(stateMachineName: String, inputName: String, value: Float)

    fun pause()
    fun stop()
    fun reset()

}