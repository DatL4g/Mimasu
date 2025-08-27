package dev.datlag.mimasu.tv.ui.custom

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import dev.datlag.mimasu.core.serialization.SerializableImmutableSet
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.TV
import dev.datlag.mimasu.tmdb.model.details.Show
import dev.datlag.mimasu.tv.common.color
import dev.datlag.mimasu.tv.common.fadeHighlightColor
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.common.rememberResolvedKanaTitles
import dev.datlag.tooling.compose.LaunchedMain
import dev.datlag.tooling.compose.MainThread

@Composable
internal fun ShowCard(
    tv: TV?,
    orientation: Orientation,
    onClick: (TV) -> Unit = { },
    onFocus: suspend (TV?) -> Unit = { }
) {
    val posters = remember(tv) { tv.posters(fallbackShow = null) }
    val backdrops = remember(tv) { tv.backdrops(fallback = null) }
    val (romajiTitle, originalTitle) = rememberResolvedKanaTitles(tv)

    ShowCard(
        onClick = { tv?.let(onClick) },
        onFocusChange = {
            if (it) {
                onFocus(tv)
            } else {
                onFocus(null)
            }
        },
        placeholder = tv == null,
        orientation = orientation,
        posters = posters,
        backdrops = backdrops,
        name = romajiTitle ?: tv?.name,
        originalName = originalTitle ?: tv?.originalName
    )
}

@Composable
internal fun ShowCard(
    show: Show?,
    orientation: Orientation,
    onClick: (Show) -> Unit = { },
    onFocus: suspend (Show?) -> Unit = { }
) {
    val posters = remember(show) { show.posters(fallbackShow = null) }
    val backdrops = remember(show) { show.backdrops(fallback = null) }
    val (romajiTitle, originalTitle) = rememberResolvedKanaTitles(show)

    ShowCard(
        onClick = { show?.let(onClick) },
        onFocusChange = {
            if (it) {
                onFocus(show)
            } else {
                onFocus(null)
            }
        },
        placeholder = show == null,
        orientation = orientation,
        posters = posters,
        backdrops = backdrops,
        name = romajiTitle ?: show?.name,
        originalName = originalTitle ?: show?.originalName
    )
}

@OptIn(MainThread::class)
@Composable
private fun ShowCard(
    placeholder: Boolean,
    orientation: Orientation,
    posters: SerializableImmutableSet<String>,
    backdrops: SerializableImmutableSet<String>,
    name: String?,
    originalName: String?,
    onClick: () -> Unit = { },
    onFocusChange: suspend (Boolean) -> Unit = {}
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
    val interaction = remember { MutableInteractionSource() }
    val isFocused by interaction.collectIsFocusedAsState()

    LaunchedMain(isFocused) {
        onFocusChange(isFocused)
    }

    StandardCardContainer(
        interactionSource = interaction,
        imageCard = { interactionSource ->
            Card(
                modifier = cardModifier,
                onClick = onClick,
                interactionSource = interactionSource
            ) {
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
                            visible = placeholder,
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
                    contentDescription = name
                )
            }
        },
        title = {
            Text(
                modifier = textModifier.placeholder(
                    visible = placeholder || name.isNullOrBlank(),
                    shape = MaterialTheme.shapes.small,
                    highlight = PlaceholderHighlight.fade(
                        highlightColor = PlaceholderDefaults.fadeHighlightColor()
                    ),
                    color = PlaceholderDefaults.color()
                ),
                text = name ?: "",
                textAlign = TextAlign.Center,
                softWrap = true,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        subtitle = {
            originalName?.takeUnless {
                it.equals(name, ignoreCase = true)
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