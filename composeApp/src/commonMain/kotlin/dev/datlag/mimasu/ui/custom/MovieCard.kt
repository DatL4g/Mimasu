package dev.datlag.mimasu.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import dev.datlag.mimasu.common.rememberNestedImagePainter
import dev.datlag.mimasu.core.round
import dev.datlag.mimasu.tmdb.common.backdrops
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.Movie
import dev.datlag.tooling.Platform
import dev.datlag.tooling.compose.platform.colorScheme
import dev.datlag.tooling.compose.platform.shapes
import dev.datlag.tooling.compose.platform.typography
import kotlinx.collections.immutable.ImmutableList
import dev.datlag.mimasu.tmdb.model.details.Movie as DetailedMovie

@Composable
fun MovieCard(
    movie: Movie?,
    modifier: Modifier = Modifier,
    onClick: (Movie) -> Unit = { }
) {
    MovieCard(
        onClick = { movie?.let(onClick) },
        placeholder = movie == null,
        modifier = modifier,
        id = movie?.id,
        posters = movie.posters(fallbackMovie = null),
        title = movie?.title,
        originalTitle = movie?.originalTitle
    )
}

@Composable
fun MovieCard(
    detailed: DetailedMovie?,
    modifier: Modifier = Modifier,
    onClick: (DetailedMovie) -> Unit = { }
) {
    MovieCard(
        onClick = { detailed?.let(onClick) },
        placeholder = detailed == null,
        modifier = modifier,
        id = detailed?.id,
        posters = detailed.posters(fallbackMovie = null),
        title = detailed?.title,
        originalTitle = detailed?.originalTitle
    )
}

@Composable
private fun MovieCard(
    placeholder: Boolean,
    id: Int?,
    posters: Collection<String>,
    title: String?,
    originalTitle: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(100.dp).height(220.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    ) {
        var loading by remember(id) { mutableStateOf(true) }

        AsyncImage(
            modifier = Modifier
                .size(width = 100.dp, height = 160.dp)
                .clip(Platform.shapes().medium)
                .placeholder(
                    visible = placeholder || loading,
                    shape = Platform.shapes().medium,
                    highlight = PlaceholderHighlight.fade()
                ),
            model = posters.firstOrNull(),
            contentScale = ContentScale.Crop,
            error = rememberNestedImagePainter(
                models = posters.drop(1),
                contentScale = ContentScale.Crop,
                onError = {
                    loading = true
                },
                onSuccess = {
                    loading = false
                }
            ),
            contentDescription = title,
            onLoading = {
                loading = true
            },
            onSuccess = {
                loading = false
            }
        )

        Text(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .placeholder(
                    visible = placeholder,
                    shape = Platform.shapes().small,
                    highlight = PlaceholderHighlight.fade()
                ),
            text = title ?: originalTitle ?: "",
            maxLines = 2,
            textAlign = TextAlign.Center,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
    }
}