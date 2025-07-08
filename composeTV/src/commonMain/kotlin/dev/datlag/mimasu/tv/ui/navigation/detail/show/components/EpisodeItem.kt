package dev.datlag.mimasu.tv.ui.navigation.detail.show.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.SwitchDefaults
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols

@Composable
fun EpisodeItem(
    selected: Boolean,
    episode: Season.Episode,
    modifier: Modifier = Modifier,
    markAsWatched: suspend () -> Unit,
    markAsUnWatched: suspend () -> Unit,
) {
    var watched by remember(episode) { mutableStateOf(false) }
    val overview = remember(episode.overview) { episode.overview }
    val posters = remember(episode) { episode.posters(fallbackShow = null) }

    ListItem(
        modifier = modifier,
        selected = selected,
        onClick = {

        },
        onLongClick = {
            watched = !watched
        },
        headlineContent = {
            Text(
                text = episode.name ?: "Episode ${episode.episodeNumber}",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = if (overview.isNullOrBlank()) {
            null
        } else {
            {
                Text(
                    text = overview,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .height(84.dp)
                    .aspectRatio(1.75F, true)
                    .clip(MaterialTheme.shapes.medium),
                model = posters.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = rememberNestedImagePainter(
                    models = posters.drop(1),
                    contentScale = ContentScale.Crop
                )
            )
        },
        trailingContent = {
            Switch(
                checked = watched,
                onCheckedChange = {
                    watched = it
                },
                thumbContent = if (watched) {
                    {
                        MaterialSymbols(
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                            name = MaterialSymbols.CHECK,
                            contentDescription = null
                        )
                    }
                } else {
                    null
                }
            )
        },
        shape = ListItemDefaults.shape(
            shape = MaterialTheme.shapes.large
        )
    )
}