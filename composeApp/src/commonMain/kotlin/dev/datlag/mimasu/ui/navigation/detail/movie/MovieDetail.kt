package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieToolbar
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieWatchProviderFAB
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.accountViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MovieDetail(
    onBack: () -> Unit,
    onCastClick: (Movie.Credits.Cast) -> Unit,
    onWatchClick: () -> Unit = {},
    onLogin: () -> Unit
) {
    val movieViewModel = kodeinViewModel<MovieViewModel>()
    val accountViewModel = accountViewModel()
    val user by accountViewModel.user.collectAsStateWithLifecycle()
    val movieState by movieViewModel.movie.collectAsStateWithLifecycle(MovieViewModel.State.Loading)
    val initial by movieViewModel.initialMovie.collectAsStateWithLifecycle()

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val haze = remember { HazeState() }
    val listState = rememberLazyListState()
    val snackbarState = remember { SnackbarHostState() }

    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MovieToolbar(
                appBarState = appBarState,
                scrollBehavior = scrollBehavior,
                hazeState = haze,
                listState = listState,
                movie = movieState.getOrNull(),
                initial = initial,
                loggedIn = user != null,
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack,
                onLogin = onLogin
            )
        },
        floatingActionButton = {
            MovieWatchProviderFAB(
                movie = movieState.getOrNull(),
                onWatchClick = onWatchClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        }
    ) { padding ->
        when (val current = movieState) {
            is MovieViewModel.State.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(0.5F).clip(CircleShape)
                    )
                }
            }
            is MovieViewModel.State.Error -> {
                ErrorState(
                    throwable = current.throwable,
                    additionalInfo = "MovieDetail",
                    modifier = Modifier.fillMaxSize().padding(padding)
                )
            }
            is MovieViewModel.State.Success -> MovieContent(
                hazeState = haze,
                listState = listState,
                snackbarState = snackbarState,
                movie = current.movie,
                initial = initial,
                padding = padding,
                onCastClick = onCastClick
            )
        }
    }
}