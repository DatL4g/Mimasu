package dev.datlag.mimasu.ui.navigation.detail.show

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowToolbar
import dev.datlag.mimasu.ui.navigation.detail.show.components.ShowWatchProviderFAB
import dev.datlag.mimasu.ui.other.rememberShowAvailability
import dev.datlag.mimasu.ui.viewmodel.ShowViewModel
import dev.datlag.mimasu.ui.viewmodel.VideoViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShowDetail(
    onBack: () -> Unit,
    onStream: (VideoViewModel.WatchType.Show) -> Unit,
    onDiscover: (Int) -> Unit,
    onLogin: () -> Unit
) {
    val showViewModel = kodeinViewModel<ShowViewModel>()
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsStateWithLifecycle()
    val showState by showViewModel.show.collectAsStateWithLifecycle(ShowViewModel.ShowState.Loading)
    val initial by showViewModel.initialShow.collectAsStateWithLifecycle()
    val showSeason by showViewModel.showSeason.collectAsStateWithLifecycle()
    val initialSeasonState = remember(showSeason) {
        if (showSeason == null) {
            ShowViewModel.SeasonState.Empty
        } else {
            ShowViewModel.SeasonState.Loading
        }
    }
    val seasonState by showViewModel.season.collectAsStateWithLifecycle(initialSeasonState)

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val haze = remember { HazeState() }
    val listState = rememberLazyListState()
    val showAvailability = rememberShowAvailability(
        show = showState.getOrNull(),
        initial = initial
    )
    val episodesData by showViewModel.episodesData.collectAsStateWithLifecycle(null)

    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ShowToolbar(
                appBarState = appBarState,
                scrollBehavior = scrollBehavior,
                hazeState = haze,
                listState = listState,
                show = showState.getOrNull(),
                initial = initial,
                loggedIn = user != null,
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack,
                onLogin = onLogin
            )
        },
        floatingActionButton = {
            ShowWatchProviderFAB(
                show = showState.getOrNull(),
                season = seasonState.getOrNull()
            )
        }
    ) { padding ->
        when (val current = showState) {
            is ShowViewModel.ShowState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.5F).clip(CircleShape)
                    )
                }
            }
            is ShowViewModel.ShowState.Error -> {
                ErrorState(
                    throwable = current.throwable,
                    additionalInfo = "ShowDetail [ShowState]",
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            }
            is ShowViewModel.ShowState.Success -> ShowContent(
                hazeState = haze,
                listState = listState,
                show = current.show,
                showSeason = showSeason,
                seasonState = seasonState,
                initial = initial,
                showAvailability = showAvailability,
                padding = padding,
                episodesData = episodesData.orEmpty().toImmutableList(),
                loggedIn = user != null,
                onSelectSeason = {
                    showViewModel.select(it)
                },
                onStream = onStream,
                markAsWatched = {
                    val seasonNumber = seasonState.getOrNull()?.seasonNumber ?: showSeason?.seasonNumber

                    if (seasonNumber != null) {
                        showViewModel.markAsWatched(
                            tmdbId = current.show.id,
                            seasonNumber = seasonNumber,
                            episode = it
                        )
                    }
                },
                markAsUnWatched = {
                    val seasonNumber = seasonState.getOrNull()?.seasonNumber ?: showSeason?.seasonNumber

                    if (seasonNumber != null) {
                        showViewModel.markAsUnwatched(
                            tmdbId = current.show.id,
                            seasonNumber = seasonNumber,
                            episode = it
                        )
                    }
                },
                onDiscover = onDiscover,
                onLogin = onLogin
            )
        }
    }
}