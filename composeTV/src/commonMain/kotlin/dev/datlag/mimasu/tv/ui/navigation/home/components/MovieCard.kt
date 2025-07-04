package dev.datlag.mimasu.tv.ui.navigation.home.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.eygraber.compose.placeholder.PlaceholderDefaults
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.fade
import com.eygraber.compose.placeholder.placeholder
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.mimasu.tv.common.color
import dev.datlag.mimasu.tv.common.fadeHighlightColor
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter

@Composable
fun MovieCard(
    movie: Movie?
) {
    StandardCardContainer(
        imageCard = { interactionSource ->
            Card(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .width(200.dp)
                    .aspectRatio(CardDefaults.HorizontalImageAspectRatio),
                onClick = { },
                interactionSource = interactionSource
            ) {
                val backdrops = remember(movie) { movie.backdrops(fallback = null) }
                val posters = remember(movie) { movie.posters(fallbackShow = null) }

                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .placeholder(
                            visible = movie == null,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = PlaceholderDefaults.fadeHighlightColor()
                            ),
                            color = PlaceholderDefaults.color()
                        ),
                    model = backdrops.firstOrNull(),
                    error = rememberNestedImagePainter(
                        models = backdrops.drop(1),
                        error = rememberNestedImagePainter(
                            models = posters,
                            contentScale = ContentScale.Crop
                        ),
                        contentScale = ContentScale.Crop,
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = movie?.title
                )
            }
        },
        title = {
            Text(
                modifier = Modifier
                    .width(200.dp)
                    .placeholder(
                        visible = movie == null,
                        shape = MaterialTheme.shapes.small,
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = PlaceholderDefaults.fadeHighlightColor()
                        ),
                        color = PlaceholderDefaults.color()
                    ),
                text = movie?.title ?: "",
                textAlign = TextAlign.Center,
                softWrap = true,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        subtitle = {
            movie?.originalTitle?.takeUnless {
                it.equals(movie.title, ignoreCase = true)
            }?.let {
                Text(
                    modifier = Modifier.width(200.dp),
                    text = it,
                    textAlign = TextAlign.Center,
                    softWrap = true,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    )
}