package dev.datlag.mimasu.tv.ui.navigation.home.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dev.datlag.mimasu.tmdb.model.details.Movie
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_home_your_movies
import dev.datlag.mimasu.tv.ui.custom.MovieCard
import dev.datlag.mimasu.ui.viewmodel.FirebaseViewModel
import dev.datlag.mimasu.ui.viewmodel.kodeinViewModel
import io.tolgee.stringResource

@Composable
internal fun BookmarkedMovies(
    modifier: Modifier = Modifier,
    onClick: (Movie) -> Unit
) {
    val firebaseViewModel = kodeinViewModel<FirebaseViewModel>()
    val bookmarked = firebaseViewModel.bookmarkedMovies.collectAsLazyPagingItems()
    val hasBookmarks by firebaseViewModel.hasBookmarkedMovies.collectAsState(false)

    if (hasBookmarks || bookmarked.itemCount > 0) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = stringResource(Res.string.tv_home_your_movies),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp)
            ) {
                items(bookmarked.itemCount) { index ->
                    val movie = bookmarked[index]

                    MovieCard(
                        detail = movie,
                        orientation = Orientation.Horizontal,
                        onClick = onClick
                    )
                }
                when {
                    bookmarked.loadState.refresh is LoadState.Loading -> {
                        items(5) {
                            MovieCard(detail = null, Orientation.Horizontal)
                        }
                    }
                    bookmarked.loadState.append is LoadState.Loading -> {
                        items(3) {
                            MovieCard(detail = null, Orientation.Horizontal)
                        }
                    }
                }
            }
        }
    }
}