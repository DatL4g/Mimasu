package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.ShowProvider
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
actual fun rememberShowAvailability(
    show: Show?,
    initial: TV?
): Boolean = with(localDI()) {
    if (show == null && initial == null) return false

    val context = LocalContext.current
    val singletonProvider by instanceOrNull<ShowProvider>()
    val showProvider = singletonProvider ?: remember(context) {
        ExtensionInitializer.getShowProvider(context)
    }
    val request = remember(show, initial) {
        Extension.Request(
            tmdbId = show?.id?.takeIf { it > 0 } ?: initial?.id,
            imdbId = show?.imdbId?.ifBlank { null },
            wikidataId = show?.externalIDs?.wikidataId?.ifBlank { null },
            title = show?.name?.ifBlank { null } ?: initial?.name?.ifBlank { null },
            originalTitle = show?.originalName?.ifBlank { null } ?: initial?.originalName?.ifBlank { null },
            firstReleaseYear = show?.firstAirLocalDate?.year ?: initial?.firstAirLocalDate?.year,
            isAnimation = show?.genres?.any { it.id == 16 } ?: initial?.genreIds?.any { it == 16 }
        )
    }

    return produceState<Boolean>(initialValue = false) {
        val anyWatchProvider = showProvider.requestId(request)

        value = anyWatchProvider
    }.value
}

@Composable
actual fun rememberEpisodeWatchInfo(
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode
): Boolean = with(localDI()) {
    if (tmdbId == null) return false

    val context = LocalContext.current
    val singletonProvider by instanceOrNull<ShowProvider>()
    val showProvider = singletonProvider ?: remember(context) {
        ExtensionInitializer.getShowProvider(context)
    }
    val request = remember(tmdbId, episode) {
        Extension.EpisodeRequest(
            episodeNumber = episode.episodeNumber,
            episodeTitle = episode.name,
            season = seasonNumber
        )
    }

    return produceState<Boolean>(initialValue = false) {
        val watchInfo = showProvider.requestEpisode(tmdbId, request)

        value = watchInfo
    }.value
}

@Composable
actual fun rememberEpisodeStreamState(
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode,
): EpisodeStreamState? = with(localDI()) {
    if (tmdbId == null) return null

    val context = LocalContext.current
    val singletonProvider by instanceOrNull<ShowProvider>()
    val showProvider = singletonProvider ?: remember(context) {
        ExtensionInitializer.getShowProvider(context)
    }
    val request = remember(tmdbId, episode) {
        Extension.EpisodeRequest(
            episodeNumber = episode.episodeNumber,
            episodeTitle = episode.name,
            season = seasonNumber
        )
    }

    return EpisodeStreamState(
        provider = showProvider,
        tmdbId = tmdbId,
        request = request
    )
}

actual class EpisodeStreamState(
    private val provider: ShowProvider,
    private val tmdbId: Int,
    private val request: Extension.EpisodeRequest
) {
    actual suspend fun getStream(): Extension.Response? {
        return provider.requestStream(tmdbId, request)
    }
}