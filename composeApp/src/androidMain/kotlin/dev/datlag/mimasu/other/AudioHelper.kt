package dev.datlag.mimasu.other

import android.content.Context
import android.media.AudioManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.media3.common.C
import androidx.media3.common.audio.AudioManagerCompat
import androidx.media3.common.util.UnstableApi
import dev.datlag.tooling.scopeCatching

@UnstableApi
class AudioHelper(
    private val context: Context
) {
    private val audioManager = context.getSystemService<AudioManager>() ?: scopeCatching {
        AudioManagerCompat.getAudioManager(context)
    }.getOrNull() ?: ContextCompat.getSystemService(context, AudioManager::class.java)

    private val minVolume = audioManager?.let {
        AudioManagerCompat.getStreamMinVolume(it, C.STREAM_TYPE_MUSIC)
    } ?: 0

    private val maxVolume = audioManager?.let {
        AudioManagerCompat.getStreamMaxVolume(it, C.STREAM_TYPE_MUSIC)
    } ?: minVolume

    var volume: Int
        get() = audioManager?.getStreamVolume(C.STREAM_TYPE_MUSIC)?.minus(minVolume) ?: minVolume
        set(value) {
            val vol = value.coerceIn(minVolume, maxVolume)
            audioManager?.setStreamVolume(C.STREAM_TYPE_MUSIC, vol, 0)
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