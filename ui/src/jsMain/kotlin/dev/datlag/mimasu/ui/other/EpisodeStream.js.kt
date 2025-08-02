package dev.datlag.mimasu.ui.other

import dev.datlag.mimasu.extension.model.Show
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class EpisodeStream(
    private val showState: ShowState,
    private val tmdbId: Int?,
) {
    private val _available = MutableStateFlow(coreAvailability(EpisodeStreamState.Initializing))
    actual val state: StateFlow<EpisodeStreamState> = _available.asStateFlow()

    actual suspend fun getStream(): Show.Response? {
        return null
    }

    private fun coreAvailability(available: EpisodeStreamState): EpisodeStreamState {
        return when (showState) {
            is ShowState.Initializing -> EpisodeStreamState.Initializing
            is ShowState.Unavailable -> EpisodeStreamState.Unavailable
            else -> if (tmdbId?.takeIf { it > 0 } == null) {
                EpisodeStreamState.Unavailable
            } else {
                available
            }
        }
    }
}