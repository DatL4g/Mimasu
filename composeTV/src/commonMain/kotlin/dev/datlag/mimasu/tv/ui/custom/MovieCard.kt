package dev.datlag.mimasu.tv.ui.custom

import androidx.compose.foundation.gestures.Orientation
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
internal fun MovieCard(
    movie: Movie?,
    orientation: Orientation,
    onClick: (Movie) -> Unit = { }
) {
    val cardModifier = when (orientation) {
        Orientation.Horizontal -> Modifier
            .padding(bottom = 8.dp)
            .width(200.dp)
            .aspectRatio(CardDefaults.HorizontalImageAspectRatio)
        Orientation.Vertical -> Modifier
            .padding(bottom = 8.dp)
            .width(120.dp)
            .aspectRatio(CardDefaults.VerticalImageAspectRatio)
    }
    val textModifier = when (orientation) {
        Orientation.Horizontal -> Modifier.width(200.dp)
        Orientation.Vertical -> Modifier.width(120.dp)
    }

    StandardCardContainer(
        imageCard = { interactionSource ->
            Card(
                modifier = cardModifier,
                onClick = {
                    if (movie != null) {
                        onClick(movie)
                    }
                },
                interactionSource = interactionSource
            ) {
                val backdrops = remember(movie) { movie.backdrops(fallback = null) }
                val posters = remember(movie) { movie.posters(fallbackShow = null) }
                val (mainImages, fallbackImages) = remember(backdrops, posters, orientation) {
                    when (orientation) {
                        Orientation.Horizontal -> backdrops to posters
                        Orientation.Vertical -> posters to backdrops
                    }
                }

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
                    model = mainImages.firstOrNull(),
                    error = rememberNestedImagePainter(
                        models = mainImages.drop(1),
                        error = rememberNestedImagePainter(
                            models = fallbackImages,
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
                modifier = textModifier.placeholder(
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
                    modifier = textModifier,
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