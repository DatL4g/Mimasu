package dev.datlag.mimasu.other

import android.content.Context
import android.media.AudioManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.media.AudioManagerCompat

class AudioHelper(
    private val context: Context
) {
    private val audioManager = context.getSystemService<AudioManager>() ?: ContextCompat.getSystemService(context, AudioManager::class.java)
    private val minVolume = audioManager?.let {
        AudioManagerCompat.getStreamMinVolume(it, AudioManager.STREAM_MUSIC)
    } ?: 0
    private val maxVolume = audioManager?.let {
        AudioManagerCompat.getStreamMaxVolume(it, AudioManager.STREAM_MUSIC)
    } ?: 0

    var volume: Int
        get() = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)?.minus(minVolume) ?: minVolume
        set(value) {
            val vol = value.coerceIn(minVolume, maxVolume)
            audioManager?.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0)
        }

    private val initialVolume = volume

    var volumeProgress: Float
        get() = (volume - minVolume).toFloat() / (maxVolume - minVolume).toFloat()
        set(value) {
            volume = (minVolume + value * (maxVolume - minVolume)).toInt()
        }

    fun dispose() {
        volume = initialVolume
    }
}