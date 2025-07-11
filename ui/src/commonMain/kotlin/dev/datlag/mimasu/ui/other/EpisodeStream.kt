package dev.datlag.mimasu.ui.other

import kotlinx.coroutines.flow.StateFlow
import dev.datlag.mimasu.extension.model.Show as Extension

expect class EpisodeStream {
    val state: StateFlow<EpisodeStreamState>

    suspend fun getStream(): Extension.Response?
}