package dev.datlag.mimasu.ui.custom.video

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.DefaultMediaItemConverter
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import androidx.media3.common.C.VideoScalingMode
import androidx.media3.common.DeviceInfo
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
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
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS
import androidx.media3.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import dev.datlag.kast.Kast
import dev.datlag.mimasu.ui.common.cronetEngine
import dev.datlag.mimasu.ui.common.videoCache
import dev.datlag.tooling.scopeCatching
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.OkHttpClient
import org.chromium.net.CronetEngine
import org.kodein.di.compose.localDI
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@UnstableApi
class PlayerWrapper(
    private val context: Context,
    private val castContext: CastContext? = Kast.castContext,
    cronetEngine: CronetEngine?,
    cache: Cache
) : Player, SessionAvailabilityListener, Player.Listener {

    private val extractorFactory = DefaultExtractorsFactory().setTsExtractorFlags(
        FLAG_ALLOW_NON_IDR_KEYFRAMES and FLAG_DETECT_ACCESS_UNITS and FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS
    )

    @SuppressLint("CustomX509TrustManager")
    private val trustAllCerts = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<out X509Certificate?>?, authType: String?) { }
        override fun checkServerTrusted(chain: Array<out X509Certificate?>?, authType: String?) { }
        override fun getAcceptedIssuers(): Array<out X509Certificate?>? = arrayOf()
    }

    private val sslContext = scopeCatching {
        SSLContext.getInstance("TLS")
    }.getOrNull() ?: scopeCatching {
        SSLContext.getInstance("SSL")
    }.getOrNull()

    private val trustAllSocketFactory = scopeCatching {
        sslContext?.init(null, arrayOf(trustAllCerts), SecureRandom())
        sslContext?.socketFactory
    }.getOrNull()

    private val cronetExecutor = Executors.newSingleThreadExecutor()
    private val cronetDataSourceFactory = cronetEngine?.let {
        CronetDataSource.Factory(it, cronetExecutor)
            .setKeepPostFor302Redirects(true)
            .setHandleSetCookieRequests(true)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .apply {
            if (trustAllSocketFactory != null) {
                sslSocketFactory(trustAllSocketFactory, trustAllCerts)
            }
        }
        .hostnameVerifier { _, _ -> true }
        .followRedirects(true)
        .build()

    private val okHttpDataSource = OkHttpDataSource.Factory(okHttpClient)
    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setKeepPostFor302Redirects(true)

    private val fallbackDataSourceFactory = DataSource.Factory {
        FallbackDataSource(
            cronetDataSourceFactory?.createDataSource(),
            okHttpDataSource.createDataSource(),
            httpDataSourceFactory.createDataSource()
        )
    }

    private val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(fallbackDataSourceFactory)

    private val renderer = DefaultRenderersFactory(context)
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
        .setEnableDecoderFallback(true)

    private val mediaProvider = DefaultMediaSourceFactory(
        DefaultDataSource.Factory(context, cacheDataSourceFactory),
        extractorFactory
    )

    private val localPlayer = ExoPlayer.Builder(context, renderer, mediaProvider).apply {
        setSeekBackIncrementMs(10000)
        setSeekForwardIncrementMs(10000)
        setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
    }.build()

    private val castPlayer = castContext?.let {
        CastPlayer(it, DefaultMediaItemConverter(), 10000L, 10000L)
    }

    private val castState: Int?
        get() = castContext?.castState

    private val casting: Boolean
        get() = castState == CastState.CONNECTED

    private var castSupported: Boolean = false
        set(value) {
            field = value

            if (value && castSessionAvailable) {
                useCastPlayer = true
            }
        }

    @Volatile
    private var castSessionAvailable: Boolean = casting
        set(value) {
            field = value

            useCastPlayer = value && castSupported
        }

    private var availableMediaItem by atomic<MediaItem?>(null)
    private val availableOrLocalMediaItem: MediaItem?
        get() {
            return availableMediaItem ?: if (localPlayer.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
                localPlayer.currentMediaItem
            } else {
                null
            }
        }

    private var useCastPlayer = castSupported && castSessionAvailable
        set(value) {
            val previous = field
            field = value

            if (value != previous) {
                player = if (value) {
                    castPlayer?.also {
                        localPlayer.pause()

                        if (it.mediaItemCount <= 0) {
                            availableOrLocalMediaItem?.forPlayer(it)?.let(it::setMediaItem)
                        }
                    } ?: localPlayer
                } else {
                    localPlayer
                }
            }
        }

    private var player: Player = (if (useCastPlayer) castPlayer ?: localPlayer else localPlayer)
        set(value) {
            val previous = field
            field = value

            if (previous != value) {
                _usingCastPlayer.update { value is CastPlayer }
            }
        }

    private val localPlayerListener = object : Player.Listener {
        override fun onRenderedFirstFrame() {
            super.onRenderedFirstFrame()

            castSupported = true
            firstFrameListener?.invoke()
        }
    }

    private val _usingCastPlayer = MutableStateFlow(player is CastPlayer)
    val usingCastPlayer = _usingCastPlayer.asStateFlow()

    var videoScaling: @VideoScalingMode Int
        get() = localPlayer.videoScalingMode
        set(value) {
            localPlayer.videoScalingMode = value
        }

    private var firstFrameListener: FirstFrame? = null
    private var onErrorListener: OnError? = null
    private var finishListener: OnFinish? = null

    init {
        castPlayer?.addListener(this)
        localPlayer.addListener(localPlayerListener)
        localPlayer.addListener(this)

        castPlayer?.setSessionAvailabilityListener(this)

        castPlayer?.playWhenReady = true
        localPlayer.playWhenReady = true
    }

    fun onFirstFrame(listener: FirstFrame) = apply {
        firstFrameListener = listener
    }

    fun onError(listener: OnError) = apply {
        onErrorListener = listener
    }

    fun onFinish(listener: OnFinish) = apply {
        finishListener = listener
    }

    override fun onCastSessionAvailable() {
        castSessionAvailable = true
    }

    override fun onCastSessionUnavailable() {
        castSessionAvailable = false
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        if (playbackState == Player.STATE_ENDED) {
            finishListener?.invoke()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)

        availableMediaItem = mediaItem
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)

        onErrorListener?.invoke()
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
        castPlayer?.setMediaItems(mediaItems.map { it.forPlayer(castPlayer) }, resetPosition)
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

    override fun replaceMediaItems(fromIndex: Int, toIndex: Int, mediaItems: List<MediaItem>) {
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

    override fun hasPreviousMediaItem(): Boolean {
        return player.hasPreviousMediaItem()
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

    override fun hasNextMediaItem(): Boolean {
        return player.hasNextMediaItem()
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

    fun releaseCasting() {
        release()

        castPlayer?.stop()
        castPlayer?.clearMediaItems()
    }

    override fun release() {
        localPlayer.removeListener(this)
        localPlayer.removeListener(localPlayerListener)

        castPlayer?.removeListener(this)
        castPlayer?.setSessionAvailabilityListener(null)

        localPlayer.release()
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

    @Deprecated("Deprecated in Java")
    override fun getCurrentWindowIndex(): Int {
        return player.currentWindowIndex
    }

    override fun getCurrentMediaItemIndex(): Int {
        return player.currentMediaItemIndex
    }

    @Deprecated("Deprecated in Java")
    override fun getNextWindowIndex(): Int {
        return player.nextWindowIndex
    }

    override fun getNextMediaItemIndex(): Int {
        return player.nextMediaItemIndex
    }

    @Deprecated("Deprecated in Java")
    override fun getPreviousWindowIndex(): Int {
        return player.previousWindowIndex
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

    @Deprecated("Deprecated in Java")
    override fun isCurrentWindowDynamic(): Boolean {
        return player.isCurrentWindowDynamic
    }

    override fun isCurrentMediaItemDynamic(): Boolean {
        return player.isCurrentMediaItemDynamic
    }

    @Deprecated("Deprecated in Java")
    override fun isCurrentWindowLive(): Boolean {
        return player.isCurrentWindowLive
    }

    override fun isCurrentMediaItemLive(): Boolean {
        return player.isCurrentMediaItemLive
    }

    override fun getCurrentLiveOffset(): Long {
        return player.currentLiveOffset
    }

    @Deprecated("Deprecated in Java")
    override fun isCurrentWindowSeekable(): Boolean {
        return player.isCurrentWindowSeekable
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

    @Deprecated("Deprecated in Java")
    override fun setDeviceVolume(volume: Int) {
        player.deviceVolume = volume
    }

    override fun setDeviceVolume(volume: Int, flags: Int) {
        return player.setDeviceVolume(volume, flags)
    }

    @Deprecated("Deprecated in Java")
    override fun increaseDeviceVolume() {
        return player.increaseDeviceVolume()
    }

    override fun increaseDeviceVolume(flags: Int) {
        return player.increaseDeviceVolume(flags)
    }

    @Deprecated("Deprecated in Java")
    override fun decreaseDeviceVolume() {
        return player.decreaseDeviceVolume()
    }

    override fun decreaseDeviceVolume(flags: Int) {
        return player.decreaseDeviceVolume(flags)
    }

    @Deprecated("Deprecated in Java")
    override fun setDeviceMuted(muted: Boolean) {
        return player.setDeviceMuted(muted)
    }

    override fun setDeviceMuted(muted: Boolean, flags: Int) {
        return player.setDeviceMuted(muted, flags)
    }

    override fun setAudioAttributes(audioAttributes: AudioAttributes, handleAudioFocus: Boolean) {
        return player.setAudioAttributes(audioAttributes, handleAudioFocus)
    }

    private fun MediaItem.forPlayer(player: Player): MediaItem {
        return if (player is CastPlayer) {
            this.buildUpon().setMimeType(MimeTypes.VIDEO_UNKNOWN).build()
        } else {
            this
        }
    }

    fun interface FirstFrame {
        operator fun invoke()
    }

    fun interface OnError {
        operator fun invoke()
    }

    fun interface OnFinish {
        operator fun invoke()
    }
}

@OptIn(UnstableApi::class)
@Composable
fun rememberPlayerWrapper(
    context: Context = LocalContext.current,
    castContext: CastContext? = Kast.castContext,
    cronetEngine: CronetEngine? = localDI().cronetEngine(),
    cache: Cache = localDI().videoCache()
): PlayerWrapper {
    val wrapper = remember(context, castContext, cronetEngine, cache) {
        PlayerWrapper(
            context = context,
            castContext = castContext,
            cronetEngine = cronetEngine,
            cache = cache
        )
    }
    return wrapper
}