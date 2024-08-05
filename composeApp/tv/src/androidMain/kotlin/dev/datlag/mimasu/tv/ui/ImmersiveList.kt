package dev.datlag.mimasu.tv.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import dev.datlag.mimasu.tmdb.api.Trending
import dev.datlag.mimasu.tv.LocalTVPadding
import dev.datlag.mimasu.tv.common.gradientOverlay
import kotlinx.collections.immutable.ImmutableCollection

@Composable
fun ImmersiveList(
    selectedMovie: Trending.Response.Media.Movie?,
    isListFocused: Boolean,
    gradientColor: Color,
    movies: ImmutableCollection<Trending.Response.Media.Movie>,
    sectionTitle: String?,
    onFocusChanged: (FocusState) -> Unit,
    onMovieFocused: (Trending.Response.Media.Movie) -> Unit,
    onMovieClick: (Trending.Response.Media.Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = modifier
    ) {
        Background(
            movie = selectedMovie,
            visible = isListFocused,
            modifier = modifier
                .fillMaxSize()
                .gradientOverlay(gradientColor)
        )
        Column(
            modifier = Modifier.padding(LocalTVPadding.current)
        ) {
            if (isListFocused && selectedMovie != null) {
                MediaDescription(
                    movie = selectedMovie,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        bottom = 40.dp
                    )
                )
            }

            ImmersiveListMediaRow(
                movies = movies,
                itemDirection = ItemDirection.Horizontal,
                title = sectionTitle,
                showItemTitle = !isListFocused,
                onMovieSelected = onMovieClick,
                onMovieFocused = onMovieFocused,
                modifier = Modifier
                    .onFocusChanged(onFocusChanged)
                    .height(196.dp)
            )
        }
    }
}

@Composable
fun ImmersiveList(
    selectedShow: Trending.Response.Media.TV?,
    isListFocused: Boolean,
    gradientColor: Color,
    shows: ImmutableCollection<Trending.Response.Media.TV>,
    sectionTitle: String?,
    onFocusChanged: (FocusState) -> Unit,
    onShowFocused: (Trending.Response.Media.TV) -> Unit,
    onShowClick: (Trending.Response.Media.TV) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = modifier
    ) {
        Background(
            show = selectedShow,
            visible = isListFocused,
            modifier = modifier
                .fillMaxSize()
                .gradientOverlay(gradientColor)
        )
        Column(
            modifier = Modifier.padding(LocalTVPadding.current)
        ) {
            if (isListFocused && selectedShow != null) {
                MediaDescription(
                    show = selectedShow,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        bottom = 40.dp
                    )
                )
            }

            ImmersiveListShowRow(
                shows = shows,
                itemDirection = ItemDirection.Horizontal,
                title = sectionTitle,
                showItemTitle = !isListFocused,
                onShowSelected = onShowClick,
                onShowFocused = onShowFocused,
                modifier = Modifier
                    .onFocusChanged(onFocusChanged)
                    .height(196.dp)
            )
        }
    }
}

@Composable
private fun Background(
    movie: Trending.Response.Media.Movie?,
    visible: Boolean,
    modifier: Modifier = Modifier
) = Background(
    backdrop = movie?.backdropPicture,
    backdropFallback = movie?.backdropPictureW500,
    visible = visible,
    modifier = modifier
)

@Composable
private fun Background(
    show: Trending.Response.Media.TV?,
    visible: Boolean,
    modifier: Modifier = Modifier
) = Background(
    backdrop = show?.backdropPicture,
    backdropFallback = show?.backdropPictureW500,
    visible = visible,
    modifier = modifier
)

@Composable
private fun Background(
    backdrop: Any?,
    backdropFallback: Any?,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        AsyncImage(
            model = backdrop,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.TopEnd,
            error = rememberAsyncImagePainter(
                model = backdropFallback,
                contentScale = ContentScale.FillHeight
            )
        )
    }
}

@Composable
private fun MediaDescription(
    movie: Trending.Response.Media.Movie,
    modifier: Modifier = Modifier,
) = MediaDescription(
    title = movie.title,
    alternativeTitle = movie.alternativeTitle,
    description = movie.overview,
    modifier = modifier
)

@Composable
private fun MediaDescription(
    show: Trending.Response.Media.TV,
    modifier: Modifier = Modifier,
) = MediaDescription(
    title = show.name,
    alternativeTitle = show.alternativeName,
    description = show.overview,
    modifier = modifier
)

@Composable
private fun MediaDescription(
    title: String,
    alternativeTitle: String?,
    description: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.displaySmall)
        description?.let { desc ->
            Text(
                modifier = Modifier.fillMaxWidth(0.5f),
                text = desc,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                fontWeight = FontWeight.Light,
                maxLines = 3
            )
        }
    }
}