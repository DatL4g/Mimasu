package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.backhandler.PredictiveBackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.justwatch
import dev.datlag.mimasu.composeapp.generated.resources.movie_watch
import dev.datlag.mimasu.tmdb.common.logos
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.custom.ErrorState
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieToolbar
import dev.datlag.mimasu.ui.navigation.detail.movie.components.MovieWatchProviderFAB
import dev.datlag.tolgee.stringResource
import dev.datlag.tooling.Platform
import dev.datlag.tooling.async.suspendCatching
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.listFrom
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MovieDetail(
    onBack: () -> Unit,
    onCastClick: (Movie.Credits.Cast) -> Unit,
    onWatchClick: () -> Unit = {}
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
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
            )
        },
        floatingActionButton = {
            MovieWatchProviderFAB(
                movie = movieState.getOrNull(),
                onWatchClick = onWatchClick
            )
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
                movie = current.movie,
                initial = initial,
                padding = padding,
                onCastClick = onCastClick
            )
        }
    }
}