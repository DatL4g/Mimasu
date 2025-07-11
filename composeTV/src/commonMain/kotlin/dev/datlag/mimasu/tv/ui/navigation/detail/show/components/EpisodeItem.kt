package dev.datlag.mimasu.tv.ui.navigation.detail.show.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import dev.datlag.mimasu.firebase.firestore.ShowData
import dev.datlag.mimasu.tmdb.common.posters
import dev.datlag.mimasu.tmdb.model.details.Season
import dev.datlag.mimasu.tv.Res
import dev.datlag.mimasu.tv.tv_show_episode_placeholder
import dev.datlag.mimasu.ui.common.rememberNestedImagePainter
import dev.datlag.mimasu.ui.custom.MaterialSymbols
import dev.datlag.mimasu.ui.other.EpisodeStreamState
import dev.datlag.mimasu.ui.other.ShowState
import dev.datlag.mimasu.ui.other.rememberEpisodeStream
import dev.datlag.tooling.compose.launchIO
import dev.datlag.tooling.compose.withMainContext
import org.jetbrains.compose.resources.stringResource
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.datlag.mimasu.extension.model.Show as Extension

@Composable
fun EpisodeItem(
    selected: Boolean,
    tmdbId: Int?,
    episode: Season.Episode,
    episodeData: ShowData.EpisodeData?,
    seasonNumber: Int?,
    showAvailability: ShowState,
    modifier: Modifier = Modifier,
    onStream: (Extension.Response) -> Unit,
    markAsWatched: suspend () -> Unit,
    markAsUnWatched: suspend () -> Unit,
) {
    val episodeStream = rememberEpisodeStream(
        showState = showAvailability,
        tmdbId = tmdbId,
        seasonNumber = seasonNumber,
        episode = episode
    )
    val scope = rememberCoroutineScope()
    val episodeStreamState by episodeStream.state.collectAsState()

    val watched = remember(episodeData) {
        if (episodeData?.markedAsWatched == false) {
            false
        } else {
            episodeData?.markedAsWatched == true || episodeData?.finished == true
        }
    }
    val overview = remember(episode.overview) { episode.overview }
    val posters = remember(episode) { episode.posters(fallbackShow = null) }

    ListItem(
        modifier = modifier,
        selected = selected,
        onClick = {
            scope.launchIO {
                val stream = episodeStream.getStream() ?: return@launchIO

                withMainContext {
                    onStream(stream)
                }
            }
        },
        onLongClick = {
            scope.launchIO {
                if (watched) {
                    markAsUnWatched()
                } else {
                    markAsWatched()
                }
            }
        },
        overlineContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (val current = episodeStreamState) {
                    is EpisodeStreamState.Available -> MaterialSymbols(
                        modifier = Modifier.size(12.dp),
                        name = if (current.state) {
                            MaterialSymbols.PLAY_ARROW
                        } else {
                            MaterialSymbols.WARNING
                        },
                        contentDescription = null,
                        filled = true,
                    )
                    else -> { }
                }
                episode.runtime.takeIf { it > 0 }?.let { runtime ->
                    Text(
                        text = runtime.toDuration(DurationUnit.MINUTES).toString(),
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        headlineContent = {
            Text(
                text = episode.name ?: stringResource(Res.string.tv_show_episode_placeholder, episode.episodeNumber),
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
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    softWrap = true
                )
            }
        },
        leadingContent = {
            AsyncImage(
                modifier = Modifier
                    .height(84.dp)
                    .aspectRatio(1.75F, true)
                    .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
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
                onCheckedChange = { },
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