package dev.datlag.mimasu.ui.navigation.detail.movie

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.datlag.mimasu.common.plus
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.movie_overview
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.typography
import org.jetbrains.compose.resources.stringResource
import dev.datlag.mimasu.tmdb.model.Movie as CommonMovie

@Composable
fun MovieContent(
    movie: Movie,
    initial: CommonMovie?,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = padding
    ) {
        item {
            MoviePosterContent(
                movie = movie,
                initial = initial,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(16.dp)
            )
        }
        item {
            MovieInfo(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
        item {
            MovieGenres(
                movie = movie,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
        item {
            MovieOverview(
                movie = movie,
                initial = initial,
                modifier = Modifier
                    .fillParentMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}