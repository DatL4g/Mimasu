package dev.datlag.mimasu.ui.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import dev.datlag.mimasu.extension.ExtensionInitializer
import dev.datlag.mimasu.extension.ShowProvider
import dev.datlag.mimasu.extension.ShowProviderAndroid
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.ui.produceVirtualIOState
import dev.datlag.tooling.compose.LaunchedVirtualIO
import org.kodein.di.compose.localDI
import org.kodein.di.instanceOrNull
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
actual fun rememberShowAvailability(
    show: Show?,
    initial: TV?
): ShowState = with(localDI()) {
    if (show == null && initial == null) return ShowState.Unavailable

    val context = LocalContext.current
    val singletonProvider by instanceOrNull<ShowProvider>()
    val showProvider = singletonProvider ?: remember(context) {
        ExtensionInitializer.getShowProvider(context)
    }

    LaunchedVirtualIO(Unit) {
        (showProvider as? ShowProviderAndroid)?.rebindIfNoneAvailable(context)
    }

    val request = remember(show, initial) {
        Extension.Request(
            tmdbId = show?.id?.takeIf { it > 0 } ?: initial?.id,
            imdbId = show?.imdbId?.ifBlank { null },
            wikidataId = show?.externalIDs?.wikidataId?.ifBlank { null },
            title = show?.name?.ifBlank { null } ?: initial?.name?.ifBlank { null },
            originalTitle = show?.originalName?.ifBlank { null } ?: initial?.originalName?.ifBlank { null },
            firstReleaseYear = show?.firstAirLocalDate?.year ?: initial?.firstAirLocalDate?.year,
            isAnimation = show?.genres?.any { it.id == 16 } ?: initial?.genreIds?.any { it == 16 },
            appLocale = Locale.current.toLanguageTag()
        )
    }

    return produceVirtualIOState<ShowState>(initialValue = ShowState.Initializing, request) {
        val anyWatchProvider = showProvider.requestId(request)

        value = ShowState.Available(anyWatchProvider)
    }.value
}

@Composable
actual fun rememberEpisodeStream(
    showState: ShowState,
    tmdbId: Int?,
    seasonNumber: Int?,
    episode: Season.Episode,
): EpisodeStream = with(localDI()) {
    val context = LocalContext.current
    val singletonProvider by instanceOrNull<ShowProvider>()
    val showProvider = singletonProvider ?: remember(context) {
        ExtensionInitializer.getShowProvider(context)
    }

    LaunchedVirtualIO(Unit) {
        (showProvider as? ShowProviderAndroid)?.rebindIfNoneAvailable(context)
    }

    val request = remember(episode, seasonNumber) {
        Extension.EpisodeRequest(
            episodeNumber = episode.episodeNumber,
            episodeTitle = episode.name,
            season = seasonNumber,
            appLocale = Locale.current.toLanguageTag()
        )
    }

    val state = remember(showState, showProvider, tmdbId, request) {
        EpisodeStream(
            showState = showState,
            provider = showProvider,
            tmdbId = tmdbId,
            request = request
        )
    }

    LaunchedVirtualIO(state) {
        state.requestEpisodeAvailability()
    }

    return state
}
