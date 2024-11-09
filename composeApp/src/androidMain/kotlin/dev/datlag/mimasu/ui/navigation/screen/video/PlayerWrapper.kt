package dev.datlag.mimasu.ui.navigation.screen.video

import android.content.Context
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.VIDEO_SCALING_MODE_DEFAULT
import androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT
import androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import androidx.media3.common.C.VOLUME_FLAG_SHOW_UI
import androidx.media3.common.C.VideoScalingMode
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Timeline
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import dev.datlag.tooling.async.scopeCatching
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

@UnstableApi
class PlayerWrapper(
    private val context: Context,
    private val castContext: CastContext?,
    cronetEngine: CronetEngine?,
    cache: Cache,
    private val onFirstFrame: () -> Unit = { }
) : Player, SessionAvailabilityListener, Player.Listener {

    private val extractorFactory = DefaultExtractorsFactory().setTsExtractorFlags(
        FLAG_ALLOW_NON_IDR_KEYFRAMES and FLAG_DETECT_ACCESS_UNITS and FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS
    )

    private val cronetExecutor = Executors.newSingleThreadExecutor()
    private val cronetDataSourceFactory = cronetEngine?.let {
        CronetDataSource.Factory(it, cronetExecutor)
            .setKeepPostFor302Redirects(true)
            .setHandleSetCookieRequests(true)
    }

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setKeepPostFor302Redirects(true)

    private val fallbackDataSourceFactory = DataSource.Factory {
        scopeCatching {
            cronetDataSourceFactory?.createDataSource()
        }.getOrNull() ?: httpDataSourceFactory.createDataSource()
    }

    private val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(fallbackDataSourceFactory)

    private val localPlayer = ExoPlayer.Builder(context).apply {
        setSeekBackIncrementMs(10000)
        setSeekForwardIncrementMs(10000)
        setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
        setMediaSourceFactory(
            DefaultMediaSourceFactory(
                DefaultDataSource.Factory(context, cacheDataSourceFactory),
                extractorFactory
            )
        )
    }.build()

    private val localPlayerListener = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()

            // Playing works, can switch to casting
            castSupported = true
            aspectRatio.update { calculateAspectRatio() }
            onFirstFrame()
        }
    }

    /**
     * [CastPlayer] if a [CastContext] exists.
     */
    private val castPlayer = castContext?.let(::CastPlayer)

    /**
     * Get the current [CastState] if a [CastContext] exists.
     */
    private val castState: Int?
        get() = castContext?.castState

    /**
     * Check if the current [CastState] is connected.
     */
    private val casting: Boolean
        get() = castState == CastState.CONNECTED

    /**
     * If the casting process can be started or not.
     */
    private var castSupported: Boolean = false
        set(value) {
            field = value

            if (value && castSessionAvailable) {
                useCastPlayer = true
            }
        }

    /**
     * Checks if a casting session is available or not and switches player.
     */
    @Volatile
    private var castSessionAvailable: Boolean = casting
        set(value) {
            field = value

            useCastPlayer = castSupported && value
        }

    /**
     * Whether or not the casting player should be used.
     */
    private var useCastPlayer = castSupported && castSessionAvailable
        set(value) {
            val previous = field
            field = value

            if (value != previous) {
                player = if (value) {
                    castPlayer.also {
                        localPlayer.pause()
                    } ?: localPlayer
                } else {
                    localPlayer
                }
            }
        }

    /**
     * The currently used [Player].
     * Either [ExoPlayer] or [CastPlayer]
     */
    private var player: Player = if (useCastPlayer) castPlayer ?: localPlayer else localPlayer
        private set(value) {
            val previous = field
            field = value

            if (previous != value) {
                usingCastPlayer.update { value is CastPlayer }

                // Create meta data
                // Create new session
            }
        }

    var videoScaling: @VideoScalingMode Int
        get() = localPlayer.videoScalingMode
        set(value) {
            localPlayer.videoScalingMode = value
        }

    val aspectRatio = MutableStateFlow(calculateAspectRatio())
    val usingCastPlayer = MutableStateFlow(player is CastPlayer)

    init {
        castPlayer?.addListener(this)
        localPlayer.addListener(localPlayerListener)
        localPlayer.addListener(this)

        castPlayer?.setSessionAvailabilityListener(this)

        castPlayer?.playWhenReady = true
        localPlayer.playWhenReady = true
    }

    override fun onCastSessionAvailable() {
        castSessionAvailable = true
    }

    override fun onCastSessionUnavailable() {
        castSessionAvailable = false
    }

    override fun getApplicationLooper(): Looper {
        return player.applicationLooper
    }

    override fun addListener(listener: Player.Listener) {
        castPlayer?.addListener(listener)
        return localPlayer.addListener(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        castPlayer?.removeListener(listener)
        return localPlayer.removeListener(listener)
    }

    override fun setMediaItems(mediaItems: List<MediaItem>) {
        castPlayer?.setMediaItems(mediaItems.map { it.forPlayer(castPlayer) })
        return localPlayer.setMediaItems(mediaItems.map { it.forPlayer(localPlayer) })
    }

    override fun setMediaItems(mediaItems: List<MediaItem>, resetPosition: Boolean) {
        castPlayer?.setMediaItems(mediaItems.map { it.forPlayer(castPlayer) })
        return localPlayer.setMediaItems(mediaItems.map { it.forPlayer(localPlayer) }, resetPosition)
    }

    override fun setMediaItems(
        mediaItems: List<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ) {
        castPlayer?.setMediaItems(mediaItems.map { it.forPlayer(castPlayer) }, startIndex, startPositionMs)
        return localPlayer.setMediaItems(mediaItems.map { it.forPlayer(localPlayer) }, startIndex, startPositionMs)
    }

    override fun setMediaItem(mediaItem: MediaItem) {
        castPlayer?.setMediaItem(mediaItem.forPlayer(castPlayer))
        return localPlayer.setMediaItem(mediaItem.forPlayer(localPlayer))
    }

    override fun setMediaItem(mediaItem: MediaItem, startPositionMs: Long) {
        castPlayer?.setMediaItem(mediaItem.forPlayer(castPlayer), startPositionMs)
        return localPlayer.setMediaItem(mediaItem.forPlayer(localPlayer), startPositionMs)
    }

    override fun setMediaItem(mediaItem: MediaItem, resetPosition: Boolean) {
        castPlayer?.setMediaItem(mediaItem.forPlayer(castPlayer), resetPosition)
        return localPlayer.setMediaItem(mediaItem.forPlayer(localPlayer), resetPosition)
    }

    override fun addMediaItem(mediaItem: MediaItem) {
        castPlayer?.addMediaItem(mediaItem.forPlayer(castPlayer))
        return localPlayer.addMediaItem(mediaItem.forPlayer(localPlayer))
    }

    override fun addMediaItem(index: Int, mediaItem: MediaItem) {
        castPlayer?.addMediaItem(index, mediaItem.forPlayer(castPlayer))
        return localPlayer.addMediaItem(index, mediaItem.forPlayer(localPlayer))
    }

    override fun addMediaItems(mediaItems: List<MediaItem>) {
        castPlayer?.addMediaItems(mediaItems.map { it.forPlayer(castPlayer) })
        return localPlayer.addMediaItems(mediaItems.map { it.forPlayer(localPlayer) })
    }

    override fun addMediaItems(index: Int, mediaItems: List<MediaItem>) {
        castPlayer?.addMediaItems(index, mediaItems.map { it.forPlayer(castPlayer) })
        return localPlayer.addMediaItems(index, mediaItems.map { it.forPlayer(localPlayer) })
    }

    override fun moveMediaItem(currentIndex: Int, newIndex: Int) {
        castPlayer?.moveMediaItem(currentIndex, newIndex)
        return localPlayer.moveMediaItem(currentIndex, newIndex)
    }

    override fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {
        castPlayer?.moveMediaItems(fromIndex, toIndex, newIndex)
        return localPlayer.moveMediaItems(fromIndex, toIndex, newIndex)
    }

    override fun replaceMediaItem(index: Int, mediaItem: MediaItem) {
        castPlayer?.replaceMediaItem(index, mediaItem.forPlayer(castPlayer))
        return localPlayer.replaceMediaItem(index, mediaItem.forPlayer(localPlayer))
    }

    override fun replaceMediaItems(
        fromIndex: Int,
        toIndex: Int,
        mediaItems: List<MediaItem>
    ) {
        castPlayer?.replaceMediaItems(fromIndex, toIndex, mediaItems.map { it.forPlayer(castPlayer) })
        return localPlayer.replaceMediaItems(fromIndex, toIndex, mediaItems.map { it.forPlayer(localPlayer) })
    }

    override fun removeMediaItem(index: Int) {
        castPlayer?.removeMediaItem(index)
        return localPlayer.removeMediaItem(index)
    }

    override fun removeMediaItems(fromIndex: Int, toIndex: Int) {
        castPlayer?.removeMediaItems(fromIndex, toIndex)
        return localPlayer.removeMediaItems(fromIndex, toIndex)
    }

    override fun clearMediaItems() {
        castPlayer?.clearMediaItems()
        return localPlayer.clearMediaItems()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        return player.isCommandAvailable(command)
    }

    override fun canAdvertiseSession(): Boolean {
        return player.canAdvertiseSession()
    }

    override fun getAvailableCommands(): Player.Commands {
        return player.availableCommands
    }

    override fun prepare() {
        return player.prepare()
    }

    override fun getPlaybackState(): Int {
        return player.playbackState
    }

    override fun getPlaybackSuppressionReason(): Int {
        return player.playbackSuppressionReason
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun getPlayerError(): PlaybackException? {
        return player.playerError
    }

    override fun play() {
        return player.play()
    }

    override fun pause() {
        return player.pause()
    }

    override fun setPlayWhenReady(playWhenReady: Boolean) {
        return player.setPlayWhenReady(playWhenReady)
    }

    override fun getPlayWhenReady(): Boolean {
        return player.playWhenReady
    }

    override fun setRepeatMode(repeatMode: Int) {
        return player.setRepeatMode(repeatMode)
    }

    override fun getRepeatMode(): Int {
        return player.repeatMode
    }

    override fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        return player.setShuffleModeEnabled(shuffleModeEnabled)
    }

    override fun getShuffleModeEnabled(): Boolean {
        return player.shuffleModeEnabled
    }

    override fun isLoading(): Boolean {
        return player.isLoading
    }

    override fun seekToDefaultPosition() {
        return player.seekToDefaultPosition()
    }

    override fun seekToDefaultPosition(mediaItemIndex: Int) {
        return player.seekToDefaultPosition(mediaItemIndex)
    }

    override fun seekTo(positionMs: Long) {
        return player.seekTo(positionMs)
    }

    override fun seekTo(mediaItemIndex: Int, positionMs: Long) {
        return player.seekTo(mediaItemIndex, positionMs)
    }

    override fun getSeekBackIncrement(): Long {
        return player.seekBackIncrement
    }

    override fun seekBack() {
        return player.seekBack()
    }

    override fun getSeekForwardIncrement(): Long {
        return player.seekForwardIncrement
    }

    override fun seekForward() {
        return player.seekForward()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("hasPreviousMediaItem()"))
    override fun hasPrevious(): Boolean {
        return hasPreviousMediaItem()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("hasPreviousMediaItem()"))
    override fun hasPreviousWindow(): Boolean {
        return hasPreviousMediaItem()
    }

    override fun hasPreviousMediaItem(): Boolean {
        return player.hasPreviousMediaItem()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("seekToPreviousMediaItem()"))
    override fun previous() {
        return seekToPreviousMediaItem()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("seekToPreviousMediaItem()"))
    override fun seekToPreviousWindow() {
        return seekToPreviousMediaItem()
    }

    override fun seekToPreviousMediaItem() {
        return player.seekToPreviousMediaItem()
    }

    override fun getMaxSeekToPreviousPosition(): Long {
        return player.maxSeekToPreviousPosition
    }

    override fun seekToPrevious() {
        return player.seekToPrevious()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("hasNextMediaItem()"))
    override fun hasNext(): Boolean {
        return hasNextMediaItem()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("hasNextMediaItem()"))
    override fun hasNextWindow(): Boolean {
        return hasNextMediaItem()
    }

    override fun hasNextMediaItem(): Boolean {
        return player.hasNextMediaItem()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("seekToNextMediaItem()"))
    override fun next() {
        return seekToNextMediaItem()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("seekToNextMediaItem()"))
    override fun seekToNextWindow() {
        return seekToNextMediaItem()
    }

    override fun seekToNextMediaItem() {
        return player.seekToNextMediaItem()
    }

    override fun seekToNext() {
        return player.seekToNext()
    }

    override fun setPlaybackParameters(playbackParameters: PlaybackParameters) {
        return player.setPlaybackParameters(playbackParameters)
    }

    override fun setPlaybackSpeed(speed: Float) {
        return player.setPlaybackSpeed(speed)
    }

    override fun getPlaybackParameters(): PlaybackParameters {
        return player.playbackParameters
    }

    override fun stop() {
        return player.stop()
    }

    /**
     * Releasing player objects.
     * Does not release [CastPlayer] to keep the connection open, but stops playing.
     */
    override fun release() {
        localPlayer.removeListener(this)
        localPlayer.removeListener(localPlayerListener)

        castPlayer?.removeListener(this)
        castPlayer?.setSessionAvailabilityListener(null)

        localPlayer.release()
        castPlayer?.stop()
        castPlayer?.clearMediaItems()
        // release session
    }

    override fun getCurrentTracks(): Tracks {
        return player.currentTracks
    }

    override fun getTrackSelectionParameters(): TrackSelectionParameters {
        return player.trackSelectionParameters
    }

    override fun setTrackSelectionParameters(parameters: TrackSelectionParameters) {
        return player.setTrackSelectionParameters(parameters)
    }

    override fun getMediaMetadata(): MediaMetadata {
        return player.mediaMetadata
    }

    override fun getPlaylistMetadata(): MediaMetadata {
        return player.playlistMetadata
    }

    override fun setPlaylistMetadata(mediaMetadata: MediaMetadata) {
        return player.setPlaylistMetadata(mediaMetadata)
    }

    override fun getCurrentManifest(): Any? {
        return player.currentManifest
    }

    override fun getCurrentTimeline(): Timeline {
        return player.currentTimeline
    }

    override fun getCurrentPeriodIndex(): Int {
        return player.currentPeriodIndex
    }

    @Deprecated("Deprecated in Java", ReplaceWith("currentMediaItemIndex"))
    override fun getCurrentWindowIndex(): Int {
        return currentMediaItemIndex
    }

    override fun getCurrentMediaItemIndex(): Int {
        return player.currentMediaItemIndex
    }

    @Deprecated("Deprecated in Java", ReplaceWith("nextMediaItemIndex"))
    override fun getNextWindowIndex(): Int {
        return nextMediaItemIndex
    }

    override fun getNextMediaItemIndex(): Int {
        return player.nextMediaItemIndex
    }

    @Deprecated("Deprecated in Java", ReplaceWith("previousMediaItemIndex"))
    override fun getPreviousWindowIndex(): Int {
        return previousMediaItemIndex
    }

    override fun getPreviousMediaItemIndex(): Int {
        return player.previousMediaItemIndex
    }

    override fun getCurrentMediaItem(): MediaItem? {
        return player.currentMediaItem
    }

    override fun getMediaItemCount(): Int {
        return player.mediaItemCount
    }

    override fun getMediaItemAt(index: Int): MediaItem {
        return player.getMediaItemAt(index)
    }

    override fun getDuration(): Long {
        return player.duration
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    override fun getBufferedPosition(): Long {
        return player.bufferedPosition
    }

    override fun getBufferedPercentage(): Int {
        return player.bufferedPercentage
    }

    override fun getTotalBufferedDuration(): Long {
        return player.totalBufferedDuration
    }

    @Deprecated("Deprecated in Java", ReplaceWith("isCurrentMediaItemDynamic"))
    override fun isCurrentWindowDynamic(): Boolean {
        return isCurrentMediaItemDynamic
    }

    override fun isCurrentMediaItemDynamic(): Boolean {
        return player.isCurrentMediaItemDynamic
    }

    @Deprecated("Deprecated in Java", ReplaceWith("isCurrentMediaItemLive"))
    override fun isCurrentWindowLive(): Boolean {
        return isCurrentMediaItemLive
    }

    override fun isCurrentMediaItemLive(): Boolean {
        return player.isCurrentMediaItemLive
    }

    override fun getCurrentLiveOffset(): Long {
        return player.currentLiveOffset
    }

    @Deprecated("Deprecated in Java", ReplaceWith("isCurrentMediaItemSeekable"))
    override fun isCurrentWindowSeekable(): Boolean {
        return isCurrentMediaItemSeekable
    }

    override fun isCurrentMediaItemSeekable(): Boolean {
        return player.isCurrentMediaItemSeekable
    }

    override fun isPlayingAd(): Boolean {
        return player.isPlayingAd
    }

    override fun getCurrentAdGroupIndex(): Int {
        return player.currentAdGroupIndex
    }

    override fun getCurrentAdIndexInAdGroup(): Int {
        return player.currentAdIndexInAdGroup
    }

    override fun getContentDuration(): Long {
        return player.contentDuration
    }

    override fun getContentPosition(): Long {
        return player.contentPosition
    }

    override fun getContentBufferedPosition(): Long {
        return player.contentBufferedPosition
    }

    override fun getAudioAttributes(): AudioAttributes {
        return player.audioAttributes
    }

    override fun setVolume(volume: Float) {
        return player.setVolume(volume)
    }

    override fun getVolume(): Float {
        return player.volume
    }

    override fun clearVideoSurface() {
        return player.clearVideoSurface()
    }

    override fun clearVideoSurface(surface: Surface?) {
        return player.clearVideoSurface(surface)
    }

    override fun setVideoSurface(surface: Surface?) {
        return player.setVideoSurface(surface)
    }

    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        return player.setVideoSurfaceHolder(surfaceHolder)
    }

    override fun clearVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
        return player.clearVideoSurfaceHolder(surfaceHolder)
    }

    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
        return player.setVideoSurfaceView(surfaceView)
    }

    override fun clearVideoSurfaceView(surfaceView: SurfaceView?) {
        return player.clearVideoSurfaceView(surfaceView)
    }

    override fun setVideoTextureView(textureView: TextureView?) {
        return player.setVideoTextureView(textureView)
    }

    override fun clearVideoTextureView(textureView: TextureView?) {
        return player.clearVideoTextureView(textureView)
    }

    override fun getVideoSize(): VideoSize {
        return player.videoSize
    }

    override fun getSurfaceSize(): Size {
        return player.surfaceSize
    }

    override fun getCurrentCues(): CueGroup {
        return player.currentCues
    }

    override fun getDeviceInfo(): DeviceInfo {
        return player.deviceInfo
    }

    override fun getDeviceVolume(): Int {
        return player.deviceVolume
    }

    override fun isDeviceMuted(): Boolean {
        return player.isDeviceMuted
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "setDeviceVolume(volume, VOLUME_FLAG_SHOW_UI)",
            "androidx.media3.common.C.VOLUME_FLAG_SHOW_UI"
        )
    )
    override fun setDeviceVolume(volume: Int) {
        return setDeviceVolume(volume, VOLUME_FLAG_SHOW_UI)
    }

    override fun setDeviceVolume(volume: Int, flags: Int) {
        return player.setDeviceVolume(volume, flags)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "increaseDeviceVolume(VOLUME_FLAG_SHOW_UI)",
            "androidx.media3.common.C.VOLUME_FLAG_SHOW_UI"
        )
    )
    override fun increaseDeviceVolume() {
        return increaseDeviceVolume(VOLUME_FLAG_SHOW_UI)
    }

    override fun increaseDeviceVolume(flags: Int) {
        return player.increaseDeviceVolume(flags)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "decreaseDeviceVolume(VOLUME_FLAG_SHOW_UI)",
            "androidx.media3.common.C.VOLUME_FLAG_SHOW_UI"
        )
    )
    override fun decreaseDeviceVolume() {
        return decreaseDeviceVolume(VOLUME_FLAG_SHOW_UI)
    }

    override fun decreaseDeviceVolume(flags: Int) {
        return player.decreaseDeviceVolume(flags)
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith(
            "setDeviceMuted(muted, VOLUME_FLAG_SHOW_UI)",
            "androidx.media3.common.C.VOLUME_FLAG_SHOW_UI"
        )
    )
    override fun setDeviceMuted(muted: Boolean) {
        return setDeviceMuted(muted, VOLUME_FLAG_SHOW_UI)
    }

    override fun setDeviceMuted(muted: Boolean, flags: Int) {
        return player.setDeviceMuted(muted, flags)
    }

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {
        return player.setAudioAttributes(audioAttributes, handleAudioFocus)
    }

    fun calculateAspectRatio(): Float {
        val height = videoSize.height
        val width = videoSize.width

        return if (height != 0 && width != 0) {
            width.toFloat() / height.toFloat()
        } else {
            16F / 9F
        }
    }

    fun togglePlay() {
        if (isPlaying && !isPlayingAd) {
            pause()
        } else {
            play()
        }
    }

    /**
     * Sets a mime type for [CastPlayer] as it won't work without.
     */
    private fun MediaItem.forPlayer(player: Player): MediaItem {
        return if (player is CastPlayer) {
            this.buildUpon().setMimeType(MimeTypes.VIDEO_UNKNOWN).build()
        } else {
            this
        }
    }
}