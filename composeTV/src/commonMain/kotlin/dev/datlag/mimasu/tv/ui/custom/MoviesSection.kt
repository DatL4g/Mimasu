package dev.datlag.mimasu.tv.ui.custom

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.Movie
import kotlinx.coroutines.flow.Flow

@Composable
internal fun MoviesSection(
    flow: Flow<PagingData<Movie>>,
    title: String,
    orientation: Orientation,
    modifier: Modifier = Modifier,
    onClick: (Movie) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val movies = flow.collectAsLazyPagingItems()
        val (refreshPlaceholder, appendPlaceholder) = remember(orientation) {
            when (orientation) {
                Orientation.Horizontal -> 5 to 3
                Orientation.Vertical -> 8 to 5
            }
        }

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp)
        ) {
            items(movies.itemCount) { index ->
                val movie = movies[index]

                MovieCard(movie, orientation, onClick)
            }
            when {
                movies.loadState.refresh is LoadState.Loading -> {
                    items(refreshPlaceholder) {
                        MovieCard(movie = null, orientation)
                    }
                }
                movies.loadState.append is LoadState.Loading -> {
                    items(appendPlaceholder) {
                        MovieCard(movie = null, orientation)
                    }
                }
            }
        }
    }
}