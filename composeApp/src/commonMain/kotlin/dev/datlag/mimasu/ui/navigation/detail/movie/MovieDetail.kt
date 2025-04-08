package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.datlag.mimasu.ui.viewmodel.MovieViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_watch
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieToolbar
import dev.datlag.tolgee.stringResource
import dev.datlag.tooling.async.suspendCatching

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MovieDetail(
    onBack: () -> Unit
) {
    val movieViewModel = kodeinViewModel<MovieViewModel>()
    val movieState by movieViewModel.movie.collectAsStateWithLifecycle(MovieViewModel.State.Loading)
    val initial by movieViewModel.initialMovie.collectAsStateWithLifecycle()

    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = appBarState
    )
    val haze = remember { HazeState() }
    val listState = rememberLazyListState()

    PredictiveBackHandler(enabled = true) { state ->
        suspendCatching {
            state.collect {
                // Collecting required, but does not contain relevant data
            }
            onBack()
        }
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
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { },
                icon = {
                    MaterialSymbols(
                        name = MaterialSymbols.PLAY_ARROW,
                        contentDescription = null,
                        filled = true
                    )
                },
                text = {
                    Text(text = stringResource(Res.string.movie_watch))
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
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
                Box(
                    modifier = Modifier.padding(padding)
                ) {
                    Text(text = "Loading Movie failed: ${current.throwable}")
                }
            }
            is MovieViewModel.State.Success -> MovieContent(
                hazeState = haze,
                listState = listState,
                movie = current.movie,
                initial = initial,
                padding = padding
            )
        }
    }
}