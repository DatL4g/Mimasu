package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MovieToolbar(
                appBarState = appBarState,
                scrollBehavior = scrollBehavior,
                movie = movieState.getOrNull(),
                initial = initial,
                modifier = Modifier.fillMaxWidth(),
                onBack = onBack
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
                Box(
                    modifier = Modifier.padding(padding)
                ) {
                    Text(text = "Loading Movie failed: ${current.throwable}")
                }
            }
            is MovieViewModel.State.Success -> MovieContent(
                movie = current.movie,
                initial = initial,
                padding = padding
            )
        }
    }
}