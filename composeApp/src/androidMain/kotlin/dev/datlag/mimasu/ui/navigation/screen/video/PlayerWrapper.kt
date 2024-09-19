package dev.datlag.mimasu.ui.navigation.screen.video

import android.content.Context
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.DatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
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
import okio.FileSystem
import org.chromium.net.CronetEngine
import java.util.concurrent.Executors

@UnstableApi
class PlayerWrapper(
    private val context: Context,
    private val castContext: CastContext?,
    private val cronetEngine: CronetEngine?,
    private val databaseProvider: DatabaseProvider
) : SessionAvailabilityListener, Player.Listener {

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

    private val cache = SimpleCache(
        (FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "video").toFile(),
        LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024),
        databaseProvider
    )
    private val cacheDataSourceFactory = CacheDataSource.Factory()
        .setCache(cache)
        .setUpstreamDataSourceFactory(fallbackDataSourceFactory)

    private val localPlayer = ExoPlayer.Builder(context).apply {
        setSeekBackIncrementMs(10000)
        setSeekForwardIncrementMs(10000)
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
    var player: Player = if (useCastPlayer) castPlayer ?: localPlayer else localPlayer
        private set(value) {
            val previous = field
            field = value

            if (previous != value) {
                val metadata = mediaItem?.let {
                    val prepared = it.forPlayer(value)
                    value.setMediaItem(prepared)
                    value.prepare()

                    prepared.mediaMetadata
                } ?: mediaItem?.mediaMetadata ?: value.mediaMetadata
                // Create meta data
                // Create new session
            }
        }

    private var mediaItem: MediaItem? = player.currentMediaItem
        set(value) {
            val previous = field
            field = value

            if (previous != value && value != null) {
                player.setMediaItem(value.forPlayer(player))
                player.prepare()
                // create new session
            }
        }

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

    fun play(mediaItem: MediaItem) {
        this.mediaItem = mediaItem
    }

    /**
     * Releasing player objects.
     * Does not release [CastPlayer] to keep the connection open, but stops playing.
     */
    fun release() {
        localPlayer.removeListener(this)
        localPlayer.removeListener(localPlayerListener)

        castPlayer?.removeListener(this)
        castPlayer?.setSessionAvailabilityListener(null)

        localPlayer.release()
        castPlayer?.stop()
        castPlayer?.clearMediaItems()
        // release session
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