package dev.datlag.mimasu.ui.navigation.screen.video

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.media3.common.C
import androidx.media3.common.C.TRACK_TYPE_TEXT
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.text.Cue
import androidx.media3.common.text.CueGroup
import androidx.media3.common.util.UnstableApi
import com.vanniktech.locale.Locale
import com.vanniktech.locale.displayName
import dev.datlag.mimasu.core.serializer.SerializableImmutableSet
import dev.datlag.mimasu.other.CountryImage
import dev.datlag.tooling.compose.withDefaultContext
import dev.datlag.tooling.compose.withIOContext
import dev.datlag.tooling.compose.withMainContext
import io.github.aakira.napier.Napier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.onClosed
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.isActive
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Serializable
data class VideoPlayerState internal constructor(
    private val player: Player,
    private val hide: Duration = 2.seconds
): Player.Listener {
    private val _controlsVisibility = MutableStateFlow(true)
    val controlsVisibility = _controlsVisibility.asStateFlow()

    val controlsVisible: Boolean
        get() = controlsVisibility.value

    private val channel = Channel<Long>(CONFLATED)

    private val _contentPosition = MutableStateFlow(getContentPosition())
    val contentPosition = _contentPosition.asStateFlow()

    private val _contentDuration = MutableStateFlow(getContentDuration())
    val contentDuration = _contentDuration.asStateFlow()

    private val _contentBufferedPosition = MutableStateFlow(getContentBufferedPosition())
    val contentBufferedPosition = _contentBufferedPosition.asStateFlow()

    private val _position = MutableStateFlow(getPosition())
    val position = _position.asStateFlow()

    private val _duration = MutableStateFlow(getDuration())
    val duration = _duration.asStateFlow()

    private val _bufferedPosition = MutableStateFlow(getBufferedPosition())
    val bufferedPosition = _bufferedPosition.asStateFlow()

    private val _isPlaying = MutableStateFlow(player.isPlaying)
    val isPlaying = _isPlaying.asStateFlow()

    val currentlyPlaying: Boolean
        get() = isPlaying.value

    private val _isLoading = MutableStateFlow(player.isLoading)
    val isLoading = _isLoading.asStateFlow()

    val currentlyLoading: Boolean
        get() = isLoading.value

    private val _canPlayPause = MutableStateFlow(getPlayPauseAvailable())
    val canPlayPause = _canPlayPause.asStateFlow()

    private val _adInfo = MutableStateFlow(getAdInfo())
    val adInfo = _adInfo.asStateFlow()

    private val _isSeekable = MutableStateFlow(getSeekingAvailable())
    val isSeekable = _isSeekable.asStateFlow()

    private val _canSeekBack = MutableStateFlow(getSeekingBackAvailable())
    val canSeekBack = _canSeekBack.asStateFlow()

    private val _canSeekForward = MutableStateFlow(getSeekingForwardAvailable())
    val canSeekForward = _canSeekForward.asStateFlow()

    private val _liveInfo = MutableStateFlow(getLiveInfo())
    val liveInfo = _liveInfo.asStateFlow()

    private val _subTitle = MutableStateFlow(SubTitle())
    val subTitle = _subTitle.asStateFlow()

    @Transient
    private val _cues = MutableStateFlow(getPlayerCues())

    @Transient
    val cues = _cues.asStateFlow()

    init {
        player.addListener(this)

        channel.sendSafely(hide.inWholeSeconds)
    }

    fun showControls(duration: Duration = hide) {
        _controlsVisibility.update { true }
        channel.sendSafely(duration.inWholeSeconds)
    }

    fun hideControls() {
        _controlsVisibility.update { false }
        channel.sendSafely(1L)
    }

    fun toggleControls(duration: Duration = hide) {
        if (controlsVisible) {
            hideControls()
        } else {
            showControls(duration)
        }
    }

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce { it * 1000 }
            .collect { _controlsVisibility.emit(false) }
    }

    suspend fun update() = withDefaultContext {
        _contentPosition.emit(withMainContext { getContentPosition() })
        _contentDuration.emit(withMainContext { getContentDuration() })
        _contentBufferedPosition.emit(withMainContext { getContentBufferedPosition() })

        _position.emit(withMainContext { getPosition() })
        _duration.emit(withMainContext { getDuration() })
        _bufferedPosition.emit(withMainContext { getBufferedPosition() })

        _isPlaying.emit(withMainContext { player.isPlaying })
        _isLoading.emit(withMainContext { player.isLoading })
        _canPlayPause.emit(withMainContext { getPlayPauseAvailable() })

        _isSeekable.emit(withMainContext { getSeekingAvailable() })
        _canSeekBack.emit(withMainContext { getSeekingBackAvailable() })
        _canSeekForward.emit(withMainContext { getSeekingForwardAvailable() })

        _adInfo.emit(withMainContext { getAdInfo() })
        _liveInfo.emit(withMainContext { getLiveInfo() })
    }

    suspend fun poll(rate: Duration = 100.milliseconds) = withDefaultContext {
        do {
            update()

            delay(max(rate.inWholeMilliseconds, 100))
        } while (isActive)
    }

    @JvmOverloads
    fun seekBack(showControls: Boolean = true) {
        if (showControls) {
            showControls()
        }

        if (getSeekingBackAvailable()) {
            player.seekBack()
        }
    }

    @JvmOverloads
    fun seekForward(showControls: Boolean = true) {
        if (showControls) {
            showControls()
        }

        if (getSeekingForwardAvailable()) {
            player.seekForward()
        }
    }

    @JvmOverloads
    fun seekTo(positionMs: Long, showControls: Boolean = true) {
        if (showControls) {
            showControls()
        }

        if (player.isCommandAvailable(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)) {
            player.seekTo(positionMs)
        }
    }

    @JvmOverloads
    fun play(showControls: Boolean = true) {
        if (showControls) {
            showControls()
        }

        if (player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
            player.play()
        }
    }

    @JvmOverloads
    fun pause(showControls: Boolean = true) {
        if (showControls) {
            showControls()
        }

        if (player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)) {
            player.pause()
        }
    }

    @JvmOverloads
    fun togglePlayPause(showControls: Boolean = true) {
        if (currentlyPlaying) {
            pause(showControls)
        } else {
            play(showControls)
        }
    }

    @JvmOverloads
    fun select(language: Language?, hideControls: Boolean = true) {
        val selected = _subTitle.updateAndGet {
            it.copy(selected = language)
        }.selected

        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setPreferredTextLanguage(selected?.code)
            .build()

        if (hideControls) {
            hideControls()
        }
    }

    override fun onTracksChanged(tracks: Tracks) {
        super.onTracksChanged(tracks)

        _subTitle.update {
            SubTitle(
                selected = null,
                available = languagesFromTrack(tracks)
            )
        }
    }

    override fun onCues(cueGroup: CueGroup) {
        super.onCues(cueGroup)

        _cues.update { getCuesFrom(cueGroup) }
    }

    private fun languagesFromTrack(tracks: Tracks): ImmutableSet<Language> {
        return tracks.groups.asSequence().mapNotNull {
            if (it.type != TRACK_TYPE_TEXT) {
                return@mapNotNull null
            }

            (0 until it.length).map { index ->
                it.getTrackFormat(index)
            }.mapNotNull { format ->
                format.language?.ifBlank { null }
            }
        }.flatten().toSet().mapNotNull { code ->
            val locale = Locale.fromOrNull(code) ?: return@mapNotNull null

            Language(
                name = locale.language.displayName(),
                code = code,
                icon = CountryImage.getByCode(code) ?: CountryImage.getByCode(locale.language.code)
            )
        }.toList().toImmutableSet()
    }

    private fun getContentPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.contentPosition
        } else {
            0L
        }
    }

    private fun getContentDuration(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.contentDuration
        } else {
            0L
        }
    }

    private fun getContentBufferedPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.contentBufferedPosition
        } else {
            0L
        }
    }

    private fun getPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.currentPosition
        } else {
            0L
        }
    }

    private fun getDuration(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.duration
        } else {
            0L
        }
    }

    private fun getBufferedPosition(): Long {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.bufferedPosition
        } else {
            0L
        }
    }

    private fun getAdInfo(): AdInfo {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            AdInfo(
                playing = player.isPlayingAd,
                groupIndex = player.currentAdGroupIndex,
                indexInGroup = player.currentAdIndexInAdGroup
            )
        } else {
            AdInfo(playing = false)
        }
    }

    private fun getPlayPauseAvailable(): Boolean {
        return player.isCommandAvailable(Player.COMMAND_PLAY_PAUSE)
    }

    private fun getSeekingAvailable(): Boolean {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            player.isCurrentMediaItemSeekable
        } else {
            false
        }
    }

    private fun getSeekingBackAvailable(): Boolean {
        return player.isCommandAvailable(Player.COMMAND_SEEK_BACK)
    }

    private fun getSeekingForwardAvailable(): Boolean {
        return player.isCommandAvailable(Player.COMMAND_SEEK_FORWARD)
    }

    private fun getLiveInfo(): LiveInfo {
        return if (player.isCommandAvailable(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)) {
            LiveInfo(
                isLive = player.isCurrentMediaItemLive,
                offset = player.currentLiveOffset
            )
        } else {
            LiveInfo(isLive = false)
        }
    }

    private fun getPlayerCues(): ImmutableList<Cue> {
        return if (player.isCommandAvailable(Player.COMMAND_GET_TEXT)) {
            getCuesFrom(player.currentCues)
        } else {
            persistentListOf()
        }
    }

    private fun getCuesFrom(group: CueGroup): ImmutableList<Cue> {
        return group.cues.toImmutableList()
    }

    private fun <T> Channel<T>.sendSafely(value: T) {
        this.trySend(value).onFailure {
            this.trySendBlocking(value)
        }
    }

    @Serializable
    data class AdInfo(
        val playing: Boolean,
        val groupIndex: Int = C.INDEX_UNSET,
        val indexInGroup: Int = C.INDEX_UNSET
    )

    @Serializable
    data class LiveInfo(
        val isLive: Boolean,
        val offset: Long = C.TIME_UNSET
    )

    @Serializable
    data class SubTitle(
        val selected: Language? = null,
        val available: SerializableImmutableSet<Language> = persistentSetOf()
    )

    @Serializable
    data class Language(
        val name: String,
        val code: String,
        @Transient val icon: DrawableResource? = CountryImage.getByCode(code) ?: Locale.fromOrNull(code)?.let {
            CountryImage.getByCode(it.language.code)
        }
    )

}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberVideoPlayerState(
    player: Player,
    hide: Duration = 2.seconds
): VideoPlayerState {
    val state = remember(player, hide) { VideoPlayerState(player, hide) }

    LaunchedEffect(state) {
        state.poll()
    }

    LaunchedEffect(state) {
        state.observe()
    }

    return state
}