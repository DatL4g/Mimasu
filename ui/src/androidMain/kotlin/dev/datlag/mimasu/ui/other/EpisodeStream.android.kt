package dev.datlag.mimasu.ui.other

import dev.datlag.mimasu.extension.ShowProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.datlag.mimasu.extension.model.Show as Extension

actual class EpisodeStream(
    private val showState: ShowState,
    private val provider: ShowProvider?,
    private val tmdbId: Int?,
    private val request: Extension.EpisodeRequest
) {
    private val _available = MutableStateFlow(coreAvailability(EpisodeStreamState.Initializing))
    actual val state = _available.asStateFlow()

    actual suspend fun getStream(): Extension.Response? {
        if (showState is ShowState.Available && showState.state) {
            if (provider != null && tmdbId?.takeIf { it > 0 } != null) {
                return provider.requestStream(tmdbId, request)
            }
        }
        return null
    }

    internal suspend fun requestEpisodeAvailability() {
        if (showState is ShowState.Available && showState.state) {
            _available.emit(EpisodeStreamState.Requesting)

            val state = if (provider != null && tmdbId?.takeIf { it > 0 } != null) {
                EpisodeStreamState.Available(provider.requestEpisode(tmdbId, request))
            } else {
                EpisodeStreamState.Unavailable
            }
            _available.emit(state)
        }
    }

    private fun coreAvailability(available: EpisodeStreamState): EpisodeStreamState {
        return when (showState) {
            is ShowState.Initializing -> EpisodeStreamState.Initializing
            is ShowState.Unavailable -> EpisodeStreamState.Unavailable
            else -> if (provider == null || tmdbId?.takeIf { it > 0 } == null) {
                EpisodeStreamState.Unavailable
            } else {
                available
            }
        }
    }
}